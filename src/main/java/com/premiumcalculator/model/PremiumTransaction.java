package com.premiumcalculator.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain entity representing an insurance premium calculation transaction.
 */
public class PremiumTransaction {

    public static final String DEMONSTRATION_DISCLAIMER =
            "Demonstration estimate only, not a real insurance quote. Illustrative educational rates.";

    private String id;
    private String customerName;
    private PolicyType policyType;
    private BigDecimal coverageAmount;
    private int policyTerm;
    private BigDecimal annualRate;
    private BigDecimal annualPremium;
    private BigDecimal totalPremium;
    private TransactionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String disclaimer;

    public PremiumTransaction() {
        this.disclaimer = DEMONSTRATION_DISCLAIMER;
    }

    public PremiumTransaction(String id, String customerName, PolicyType policyType,
                              BigDecimal coverageAmount, int policyTerm, BigDecimal annualRate,
                              BigDecimal annualPremium, BigDecimal totalPremium,
                              TransactionStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.customerName = customerName;
        this.policyType = policyType;
        this.coverageAmount = coverageAmount;
        this.policyTerm = policyTerm;
        this.annualRate = annualRate;
        this.annualPremium = annualPremium;
        this.totalPremium = totalPremium;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
        this.disclaimer = DEMONSTRATION_DISCLAIMER;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public PolicyType getPolicyType() {
        return policyType;
    }

    public void setPolicyType(PolicyType policyType) {
        this.policyType = policyType;
    }

    public BigDecimal getCoverageAmount() {
        return coverageAmount;
    }

    public void setCoverageAmount(BigDecimal coverageAmount) {
        this.coverageAmount = coverageAmount;
    }

    public int getPolicyTerm() {
        return policyTerm;
    }

    public void setPolicyTerm(int policyTerm) {
        this.policyTerm = policyTerm;
    }

    public BigDecimal getAnnualRate() {
        return annualRate;
    }

    public void setAnnualRate(BigDecimal annualRate) {
        this.annualRate = annualRate;
    }

    public BigDecimal getAnnualPremium() {
        return annualPremium;
    }

    public void setAnnualPremium(BigDecimal annualPremium) {
        this.annualPremium = annualPremium;
    }

    public BigDecimal getTotalPremium() {
        return totalPremium;
    }

    public void setTotalPremium(BigDecimal totalPremium) {
        this.totalPremium = totalPremium;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }
}
