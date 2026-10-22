package com.premiumcalculator.controller;

import com.premiumcalculator.dto.CalculationRequest;
import com.premiumcalculator.dto.CalculationResponse;
import com.premiumcalculator.dto.StatusUpdateRequest;
import com.premiumcalculator.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * REST API controller exposing endpoints for calculating insurance premiums,
 * listing transaction history, updating approval statuses, and downloading receipts.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allows local development and cross-origin frontend testing
public class PremiumCalculatorController {

    private final TransactionService transactionService;

    public PremiumCalculatorController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * POST /api/calculations
     * Calculates premium and records a new transaction.
     */
    @PostMapping("/calculations")
    public ResponseEntity<CalculationResponse> calculatePremium(@Valid @RequestBody CalculationRequest request) {
        CalculationResponse response = transactionService.createTransaction(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * GET /api/transactions
     * Returns all transactions, latest first.
     */
    @GetMapping("/transactions")
    public ResponseEntity<List<CalculationResponse>> getAllTransactions() {
        List<CalculationResponse> list = transactionService.getAllTransactions();
        return ResponseEntity.ok(list);
    }

    /**
     * GET /api/transactions/{id}
     * Retrieves a single transaction by ID.
     */
    @GetMapping("/transactions/{id}")
    public ResponseEntity<CalculationResponse> getTransactionById(@PathVariable String id) {
        CalculationResponse response = transactionService.getTransactionById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/transactions/{id}/status
     * Approves or rejects a submitted transaction.
     */
    @PatchMapping("/transactions/{id}/status")
    public ResponseEntity<CalculationResponse> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody StatusUpdateRequest request) {
        CalculationResponse updated = transactionService.updateStatus(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * GET /api/transactions/{id}/receipt
     * Downloads an official text receipt file.
     */
    @GetMapping(value = "/transactions/{id}/receipt", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable String id) {
        String receiptText = transactionService.generateReceipt(id);
        byte[] receiptBytes = receiptText.getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("receipt-" + id.toUpperCase() + ".txt")
                .build());
        headers.setContentLength(receiptBytes.length);

        return new ResponseEntity<>(receiptBytes, headers, HttpStatus.OK);
    }
}
