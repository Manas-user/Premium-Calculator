package com.premiumcalculator.dto;

import com.premiumcalculator.model.PolicyType;
import com.premiumcalculator.model.PremiumTransaction;
import com.premiumcalculator.model.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO returned after calculation or transaction query.
 */
public class CalculationResponse {

    private String id;
    private String customerName;
    private PolicyType policyType;
    private String policyDisplayName;
    private BigDecimal coverageAmount;
    private int policyTerm;
    private BigDecimal annualRate;
    private String ratePercentage;
    private BigDecimal annualPremium;
    private BigDecimal totalPremium;
    private TransactionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String disclaimer;

    public CalculationResponse() {
    }

    public static CalculationResponse fromEntity(PremiumTransaction tx) {
        CalculationResponse resp = new CalculationResponse();
        resp.setId(tx.getId());
        resp.setCustomerName(tx.getCustomerName());
        resp.setPolicyType(tx.getPolicyType());
        resp.setPolicyDisplayName(tx.getPolicyType() != null ? tx.getPolicyType().getDisplayName() : null);
        resp.setCoverageAmount(tx.getCoverageAmount());
        resp.setPolicyTerm(tx.getPolicyTerm());
        resp.setAnnualRate(tx.getAnnualRate());
        if (tx.getAnnualRate() != null) {
            resp.setRatePercentage(tx.getAnnualRate().multiply(BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString() + "%");
        }
        resp.setAnnualPremium(tx.getAnnualPremium());
        resp.setTotalPremium(tx.getTotalPremium());
        resp.setStatus(tx.getStatus());
        resp.setCreatedAt(tx.getCreatedAt());
        resp.setUpdatedAt(tx.getUpdatedAt());
        resp.setDisclaimer(tx.getDisclaimer());
        return resp;
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

    public String getPolicyDisplayName() {
        return policyDisplayName;
    }

    public void setPolicyDisplayName(String policyDisplayName) {
        this.policyDisplayName = policyDisplayName;
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

    public String getRatePercentage() {
        return ratePercentage;
    }

    public void setRatePercentage(String ratePercentage) {
        this.ratePercentage = ratePercentage;
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
