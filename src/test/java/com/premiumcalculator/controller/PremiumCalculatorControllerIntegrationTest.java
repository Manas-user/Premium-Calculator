package com.premiumcalculator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.premiumcalculator.dto.CalculationRequest;
import com.premiumcalculator.dto.CalculationResponse;
import com.premiumcalculator.dto.StatusUpdateRequest;
import com.premiumcalculator.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration Tests: PremiumCalculatorController & REST API")
class PremiumCalculatorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionService.clearAll();
    }

    @Test
    @DisplayName("POST /api/calculations - Successfully calculates and creates transaction")
    void testCalculatePremiumSuccess() throws Exception {
        CalculationRequest request = new CalculationRequest("Manas Chand", "LIFE", new BigDecimal("100000.00"), 5);

        mockMvc.perform(post("/api/calculations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", startsWith("TXN-")))
                .andExpect(jsonPath("$.customerName", is("Manas Chand")))
                .andExpect(jsonPath("$.policyType", is("LIFE")))
                .andExpect(jsonPath("$.annualPremium", is(1200.00)))
                .andExpect(jsonPath("$.totalPremium", is(6000.00)))
                .andExpect(jsonPath("$.status", is("SUBMITTED")));
    }

    @Test
    @DisplayName("POST /api/calculations - Returns 400 Bad Request when customer name is blank")
    void testCalculatePremiumValidationFailureBlankName() throws Exception {
        CalculationRequest request = new CalculationRequest("", "LIFE", new BigDecimal("100000.00"), 5);

        mockMvc.perform(post("/api/calculations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.details", hasItem(containsString("customerName"))));
    }

    @Test
    @DisplayName("POST /api/calculations - Returns 400 Bad Request when coverage amount is below minimum")
    void testCalculatePremiumValidationFailureLowCoverage() throws Exception {
        CalculationRequest request = new CalculationRequest("Pooja Sen", "HEALTH", new BigDecimal("500.00"), 3);

        mockMvc.perform(post("/api/calculations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.details", hasItem(containsString("coverageAmount"))));
    }

    @Test
    @DisplayName("GET /api/transactions - Returns empty list or transactions array")
    void testGetAllTransactions() throws Exception {
        CalculationRequest request = new CalculationRequest("John Doe", "TRAVEL", new BigDecimal("50000.00"), 2);
        transactionService.createTransaction(request);

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].customerName", is("John Doe")));
    }

    @Test
    @DisplayName("GET /api/transactions/{id} - Returns 200 OK with transaction when ID is valid")
    void testGetTransactionByIdFound() throws Exception {
        CalculationRequest request = new CalculationRequest("Karan Singh", "VEHICLE", new BigDecimal("250000.00"), 2);
        CalculationResponse created = transactionService.createTransaction(request);

        mockMvc.perform(get("/api/transactions/{id}", created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(created.getId())))
                .andExpect(jsonPath("$.customerName", is("Karan Singh")));
    }

    @Test
    @DisplayName("GET /api/transactions/{id} - Returns 404 Not Found when ID does not exist")
    void testGetTransactionByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/transactions/{id}", "TXN-99999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")));
    }

    @Test
    @DisplayName("PATCH /api/transactions/{id}/status - Successfully approves transaction")
    void testApproveTransactionStatus() throws Exception {
        CalculationRequest request = new CalculationRequest("Sara Ali", "HEALTH", new BigDecimal("200000.00"), 4);
        CalculationResponse created = transactionService.createTransaction(request);

        StatusUpdateRequest updateReq = new StatusUpdateRequest("APPROVED", "Verified medical records");

        mockMvc.perform(patch("/api/transactions/{id}/status", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    @DisplayName("PATCH /api/transactions/{id}/status - Returns 400 when attempting transition from APPROVED")
    void testInvalidStatusTransition() throws Exception {
        CalculationRequest request = new CalculationRequest("Sara Ali", "HEALTH", new BigDecimal("200000.00"), 4);
        CalculationResponse created = transactionService.createTransaction(request);

        // First transition
        transactionService.updateStatus(created.getId(), new StatusUpdateRequest("APPROVED"));

        // Second transition attempt: APPROVED -> REJECTED
        StatusUpdateRequest invalidReq = new StatusUpdateRequest("REJECTED");

        mockMvc.perform(patch("/api/transactions/{id}/status", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Invalid Status Transition")));
    }

    @Test
    @DisplayName("GET /api/transactions/{id}/receipt - Downloads text receipt attachment")
    void testDownloadReceipt() throws Exception {
        CalculationRequest request = new CalculationRequest("Amitabh", "LIFE", new BigDecimal("500000.00"), 10);
        CalculationResponse created = transactionService.createTransaction(request);

        mockMvc.perform(get("/api/transactions/{id}/receipt", created.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(header().string("Content-Disposition", containsString("attachment; filename=\"receipt-" + created.getId() + ".txt\"")))
                .andExpect(content().string(containsString("PREMIUM CALCULATOR RECEIPT")))
                .andExpect(content().string(containsString(created.getId())));
    }

    @Test
    @DisplayName("GET /actuator/health - Returns 200 OK and UP status")
    void testActuatorHealth() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")));
    }
}
