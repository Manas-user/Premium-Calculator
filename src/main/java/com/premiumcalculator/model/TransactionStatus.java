package com.premiumcalculator.model;

/**
 * Status enumeration for calculated premium transactions.
 */
public enum TransactionStatus {
    SUBMITTED,
    APPROVED,
    REJECTED;

    public static TransactionStatus fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty. Allowed: APPROVED, REJECTED");
        }
        try {
            return TransactionStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid status: '" + value + "'. Allowed: APPROVED, REJECTED");
        }
    }
}
