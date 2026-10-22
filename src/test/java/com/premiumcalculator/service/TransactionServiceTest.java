package com.premiumcalculator.service;

import com.premiumcalculator.dto.CalculationRequest;
import com.premiumcalculator.dto.CalculationResponse;
import com.premiumcalculator.dto.StatusUpdateRequest;
import com.premiumcalculator.exception.InvalidInputException;
import com.premiumcalculator.exception.InvalidStatusTransitionException;
import com.premiumcalculator.exception.ResourceNotFoundException;
import com.premiumcalculator.model.PolicyType;
import com.premiumcalculator.model.TransactionStatus;
import com.premiumcalculator.repository.InMemoryTransactionRepository;
import com.premiumcalculator.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests: TransactionService")
class TransactionServiceTest {

    private TransactionRepository repository;
    private PremiumCalculationService calculationService;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTransactionRepository();
        calculationService = new PremiumCalculationService();
        transactionService = new TransactionService(repository, calculationService);
    }

    @Test
    @DisplayName("Should create transaction in SUBMITTED status with correct values")
    void shouldCreateTransactionSuccessfully() {
        CalculationRequest request = new CalculationRequest("Ananya Roy", "LIFE", new BigDecimal("200000.00"), 10);

        CalculationResponse response = transactionService.createTransaction(request);

        assertNotNull(response.getId());
        assertTrue(response.getId().startsWith("TXN-"));
        assertEquals("Ananya Roy", response.getCustomerName());
        assertEquals(PolicyType.LIFE, response.getPolicyType());
        assertEquals(TransactionStatus.SUBMITTED, response.getStatus());
        assertEquals(new BigDecimal("2400.00"), response.getAnnualPremium());
        assertEquals(new BigDecimal("24000.00"), response.getTotalPremium());
        assertNotNull(response.getCreatedAt());
    }

    @Test
    @DisplayName("Should retrieve all transactions latest first")
    void shouldRetrieveAllTransactions() {
        CalculationRequest req1 = new CalculationRequest("Alice", "HEALTH", new BigDecimal("100000.00"), 2);
        CalculationRequest req2 = new CalculationRequest("Bob", "TRAVEL", new BigDecimal("50000.00"), 1);

        transactionService.createTransaction(req1);
        transactionService.createTransaction(req2);

        List<CalculationResponse> list = transactionService.getAllTransactions();
        assertEquals(2, list.size());
    }

    @Test
    @DisplayName("Should retrieve transaction by valid ID")
    void shouldRetrieveTransactionById() {
        CalculationRequest req = new CalculationRequest("Vikram", "VEHICLE", new BigDecimal("150000.00"), 3);
        CalculationResponse created = transactionService.createTransaction(req);

        CalculationResponse fetched = transactionService.getTransactionById(created.getId());
        assertEquals(created.getId(), fetched.getId());
        assertEquals("Vikram", fetched.getCustomerName());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for unknown ID")
    void shouldThrowNotFoundForUnknownId() {
        assertThrows(ResourceNotFoundException.class, () ->
                transactionService.getTransactionById("TXN-NONEXISTENT"));
    }

    @Test
    @DisplayName("Should approve transaction from SUBMITTED to APPROVED")
    void shouldApproveTransaction() {
        CalculationRequest req = new CalculationRequest("Sneha", "LIFE", new BigDecimal("100000.00"), 5);
        CalculationResponse created = transactionService.createTransaction(req);

        StatusUpdateRequest updateReq = new StatusUpdateRequest("APPROVED", "Verified credentials");
        CalculationResponse updated = transactionService.updateStatus(created.getId(), updateReq);

        assertEquals(TransactionStatus.APPROVED, updated.getStatus());
    }

    @Test
    @DisplayName("Should reject transaction from SUBMITTED to REJECTED")
    void shouldRejectTransaction() {
        CalculationRequest req = new CalculationRequest("Sneha", "LIFE", new BigDecimal("100000.00"), 5);
        CalculationResponse created = transactionService.createTransaction(req);

        StatusUpdateRequest updateReq = new StatusUpdateRequest("REJECTED", "Risk threshold exceeded");
        CalculationResponse updated = transactionService.updateStatus(created.getId(), updateReq);

        assertEquals(TransactionStatus.REJECTED, updated.getStatus());
    }

    @Test
    @DisplayName("Should reject invalid status transition from APPROVED to REJECTED")
    void shouldRejectTransitionFromApprovedState() {
        CalculationRequest req = new CalculationRequest("Sneha", "LIFE", new BigDecimal("100000.00"), 5);
        CalculationResponse created = transactionService.createTransaction(req);

        // First transition: SUBMITTED -> APPROVED
        transactionService.updateStatus(created.getId(), new StatusUpdateRequest("APPROVED"));

        // Second transition attempt: APPROVED -> REJECTED (must fail)
        assertThrows(InvalidStatusTransitionException.class, () ->
                transactionService.updateStatus(created.getId(), new StatusUpdateRequest("REJECTED")));
    }

    @Test
    @DisplayName("Should generate complete text receipt containing customer and premium details")
    void shouldGenerateReceipt() {
        CalculationRequest req = new CalculationRequest("Deepak Kumar", "HEALTH", new BigDecimal("300000.00"), 4);
        CalculationResponse created = transactionService.createTransaction(req);

        String receipt = transactionService.generateReceipt(created.getId());

        assertNotNull(receipt);
        assertTrue(receipt.contains("PREMIUM CALCULATOR RECEIPT"));
        assertTrue(receipt.contains(created.getId()));
        assertTrue(receipt.contains("Deepak Kumar"));
        assertTrue(receipt.contains("Health Insurance"));
        assertTrue(receipt.contains("Demonstration estimate only"));
    }

    @Test
    @DisplayName("Should throw InvalidInputException for null calculation request")
    void shouldThrowExceptionForNullRequest() {
        assertThrows(InvalidInputException.class, () ->
                transactionService.createTransaction(null));
    }
}
