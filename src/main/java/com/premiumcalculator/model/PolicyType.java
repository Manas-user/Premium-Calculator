package com.premiumcalculator.model;

import java.math.BigDecimal;

/**
 * Supported policy types with illustrative demonstration annual rates.
 * NOTE: These rates are for demonstration estimates only, not real insurance underwriting quotes.
 */
public enum PolicyType {
    LIFE("Life Insurance", new BigDecimal("0.0120")),       // 1.20%
    HEALTH("Health Insurance", new BigDecimal("0.0240")),   // 2.40%
    VEHICLE("Vehicle Insurance", new BigDecimal("0.0310")), // 3.10%
    TRAVEL("Travel Insurance", new BigDecimal("0.0160"));   // 1.60%

    private final String displayName;
    private final BigDecimal annualRate;

    PolicyType(String displayName, BigDecimal annualRate) {
        this.displayName = displayName;
        this.annualRate = annualRate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BigDecimal getAnnualRate() {
        return annualRate;
    }

    public static PolicyType fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Policy type cannot be empty. Allowed: LIFE, HEALTH, VEHICLE, TRAVEL");
        }
        try {
            return PolicyType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid policy type: '" + value + "'. Allowed types: LIFE, HEALTH, VEHICLE, TRAVEL");
        }
    }
}
