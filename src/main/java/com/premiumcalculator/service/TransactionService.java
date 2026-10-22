package com.premiumcalculator.service;

import com.premiumcalculator.dto.CalculationRequest;
import com.premiumcalculator.dto.CalculationResponse;
import com.premiumcalculator.dto.StatusUpdateRequest;
import com.premiumcalculator.exception.InvalidInputException;
import com.premiumcalculator.exception.InvalidStatusTransitionException;
import com.premiumcalculator.exception.ResourceNotFoundException;
import com.premiumcalculator.model.PolicyType;
import com.premiumcalculator.model.PremiumTransaction;
import com.premiumcalculator.model.TransactionStatus;
import com.premiumcalculator.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service managing insurance premium transaction lifecycle, calculations, and receipt formatting.
 */
@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private static final DateTimeFormatter RECEIPT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final TransactionRepository repository;
    private final PremiumCalculationService calculationService;

    public TransactionService(TransactionRepository repository, PremiumCalculationService calculationService) {
        this.repository = repository;
        this.calculationService = calculationService;
    }

    /**
     * Calculates premium and creates a new transaction in SUBMITTED state.
     */
    public CalculationResponse createTransaction(CalculationRequest request) {
        if (request == null) {
            throw new InvalidInputException("Calculation request cannot be null.");
        }

        PolicyType policyType;
        try {
            policyType = PolicyType.fromString(request.getPolicyType());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException(e.getMessage());
        }

        var calcResult = calculationService.calculate(
                policyType,
                request.getCoverageAmount(),
                request.getPolicyTerm()
        );

        String id = generateTransactionId();
        LocalDateTime now = LocalDateTime.now();

        PremiumTransaction transaction = new PremiumTransaction(
                id,
                request.getCustomerName().trim(),
                policyType,
                request.getCoverageAmount(),
                request.getPolicyTerm(),
                calcResult.annualRate(),
                calcResult.annualPremium(),
                calcResult.totalPremium(),
                TransactionStatus.SUBMITTED,
                now
        );

        PremiumTransaction saved = repository.save(transaction);
        logger.info("Created new transaction: id={}, customer='{}', type={}, totalPremium={}",
                saved.getId(), saved.getCustomerName(), saved.getPolicyType(), saved.getTotalPremium());

        return CalculationResponse.fromEntity(saved);
    }

    /**
     * Retrieves all transactions ordered by most recent first.
     */
    public List<CalculationResponse> getAllTransactions() {
        return repository.findAll().stream()
                .map(CalculationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a single transaction by ID.
     */
    public CalculationResponse getTransactionById(String id) {
        PremiumTransaction tx = findTransactionOrThrow(id);
        return CalculationResponse.fromEntity(tx);
    }

    /**
     * Updates the status of an existing transaction (SUBMITTED -> APPROVED or REJECTED).
     */
    public CalculationResponse updateStatus(String id, StatusUpdateRequest request) {
        if (request == null || request.getStatus() == null || request.getStatus().trim().isEmpty()) {
            throw new InvalidInputException("Status update request and status value are required.");
        }

        TransactionStatus targetStatus;
        try {
            targetStatus = TransactionStatus.fromString(request.getStatus());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException(e.getMessage());
        }

        PremiumTransaction tx = findTransactionOrThrow(id);

        // Validation of status transitions
        if (tx.getStatus() != TransactionStatus.SUBMITTED) {
            throw new InvalidStatusTransitionException(String.format(
                    "Cannot update transaction '%s'. Current status is '%s'. Only SUBMITTED transactions can be updated.",
                    id, tx.getStatus()
            ));
        }

        if (targetStatus == TransactionStatus.SUBMITTED) {
            throw new InvalidStatusTransitionException("Transaction is already in SUBMITTED state.");
        }

        tx.setStatus(targetStatus);
        tx.setUpdatedAt(LocalDateTime.now());
        PremiumTransaction updated = repository.save(tx);

        logger.info("Transaction status updated: id={}, newStatus={}", id, targetStatus);
        return CalculationResponse.fromEntity(updated);
    }

    /**
     * Generates a plain-text receipt for the given transaction.
     */
    public String generateReceipt(String id) {
        PremiumTransaction tx = findTransactionOrThrow(id);

        String formattedDate = tx.getCreatedAt().format(RECEIPT_DATE_FORMAT);
        String updatedDate = tx.getUpdatedAt() != null ? tx.getUpdatedAt().format(RECEIPT_DATE_FORMAT) : formattedDate;
        String ratePercentage = tx.getAnnualRate().multiply(java.math.BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString() + "%";

        StringBuilder sb = new StringBuilder();
        sb.append("================================================================\n");
        sb.append("                 PREMIUM CALCULATOR RECEIPT                     \n");
        sb.append("              Demonstration Estimate Voucher                    \n");
        sb.append("================================================================\n");
        sb.append(String.format(" Transaction ID   : %s%n", tx.getId()));
        sb.append(String.format(" Status           : %s%n", tx.getStatus()));
        sb.append(String.format(" Date Created     : %s%n", formattedDate));
        sb.append(String.format(" Last Updated     : %s%n", updatedDate));
        sb.append("----------------------------------------------------------------\n");
        sb.append(" CUSTOMER DETAILS\n");
        sb.append(String.format(" Customer Name    : %s%n", tx.getCustomerName()));
        sb.append("----------------------------------------------------------------\n");
        sb.append(" POLICY SPECIFICATIONS\n");
        sb.append(String.format(" Policy Type      : %s (%s)%n", tx.getPolicyType().getDisplayName(), tx.getPolicyType()));
        sb.append(String.format(" Coverage Amount  : Rs. %s%n", tx.getCoverageAmount().toPlainString()));
        sb.append(String.format(" Policy Term      : %d years%n", tx.getPolicyTerm()));
        sb.append(String.format(" Annual Rate      : %s (illustrative demo rate)%n", ratePercentage));
        sb.append("----------------------------------------------------------------\n");
        sb.append(" PREMIUM BREAKDOWN\n");
        sb.append(String.format(" Estimated Annual : Rs. %s%n", tx.getAnnualPremium().toPlainString()));
        sb.append(String.format(" Estimated Total  : Rs. %s%n", tx.getTotalPremium().toPlainString()));
        sb.append("================================================================\n");
        sb.append(" DISCLAIMER:\n");
        sb.append(" ").append(tx.getDisclaimer()).append("\n");
        sb.append(" This document is generated for academic demonstration only.\n");
        sb.append(" Not a binding insurance contract or financial commitment.\n");
        sb.append("================================================================\n");

        return sb.toString();
    }

    public void clearAll() {
        repository.deleteAll();
    }

    private PremiumTransaction findTransactionOrThrow(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidInputException("Transaction ID cannot be blank.");
        }
        return repository.findById(id.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));
    }

    private String generateTransactionId() {
        // Human-friendly uppercase ID e.g., TXN-A1B2C3D4
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "TXN-" + uuid;
    }
}
