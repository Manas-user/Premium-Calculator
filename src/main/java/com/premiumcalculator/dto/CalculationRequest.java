package com.premiumcalculator.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Incoming request DTO for calculating insurance premium estimates.
 */
public class CalculationRequest {

    @NotBlank(message = "Customer name is required and cannot be blank.")
    @Size(min = 2, max = 60, message = "Customer name must be between 2 and 60 characters.")
    @Pattern(regexp = "^[a-zA-Z\\s.'-]+$", message = "Customer name can only contain letters, spaces, dots, hyphens, and apostrophes.")
    private String customerName;

    @NotBlank(message = "Policy type is required (Life, Health, Vehicle, Travel).")
    private String policyType;

    @NotNull(message = "Coverage amount is required.")
    @DecimalMin(value = "1000.00", message = "Coverage amount must be at least ₹1,000.00.")
    @DecimalMax(value = "10000000.00", message = "Coverage amount cannot exceed ₹10,000,000.00.")
    private BigDecimal coverageAmount;

    @NotNull(message = "Policy term in years is required.")
    @Min(value = 1, message = "Policy term must be at least 1 year.")
    @Max(value = 50, message = "Policy term cannot exceed 50 years.")
    private Integer policyTerm;

    public CalculationRequest() {
    }

    public CalculationRequest(String customerName, String policyType, BigDecimal coverageAmount, Integer policyTerm) {
        this.customerName = customerName;
        this.policyType = policyType;
        this.coverageAmount = coverageAmount;
        this.policyTerm = policyTerm;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public BigDecimal getCoverageAmount() {
        return coverageAmount;
    }

    public void setCoverageAmount(BigDecimal coverageAmount) {
        this.coverageAmount = coverageAmount;
    }

    public Integer getPolicyTerm() {
        return policyTerm;
    }

    public void setPolicyTerm(Integer policyTerm) {
        this.policyTerm = policyTerm;
    }
}
