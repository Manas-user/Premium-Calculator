package com.premiumcalculator.service;

import com.premiumcalculator.exception.InvalidInputException;
import com.premiumcalculator.model.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Service responsible for calculating illustrative insurance premium estimates.
 * Formula:
 *   Annual Premium = Coverage Amount × Policy Rate
 *   Total Premium  = Annual Premium × Policy Term
 *
 * NOTE: These calculations are educational demonstration estimates and not real insurance underwriting quotes.
 */
@Service
public class PremiumCalculationService {

    private static final Logger logger = LoggerFactory.getLogger(PremiumCalculationService.class);
    private static final BigDecimal MIN_COVERAGE = new BigDecimal("1000.00");
    private static final BigDecimal MAX_COVERAGE = new BigDecimal("10000000.00");
    private static final int MIN_TERM = 1;
    private static final int MAX_TERM = 50;

    /**
     * Calculates the annual premium for a given coverage amount and policy rate.
     * Rounded to 2 decimal places using HALF_UP.
     */
    public BigDecimal calculateAnnualPremium(BigDecimal coverageAmount, BigDecimal annualRate) {
        validateCoverageAmount(coverageAmount);
        if (annualRate == null || annualRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputException("Policy rate must be a positive value.");
        }
        return coverageAmount.multiply(annualRate).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates the total premium over the entire policy term.
     * Rounded to 2 decimal places using HALF_UP.
     */
    public BigDecimal calculateTotalPremium(BigDecimal annualPremium, int policyTerm) {
        validatePolicyTerm(policyTerm);
        if (annualPremium == null || annualPremium.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidInputException("Annual premium cannot be null or negative.");
        }
        return annualPremium.multiply(BigDecimal.valueOf(policyTerm)).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Complete calculation bundle returning annual and total premiums.
     */
    public CalculationResult calculate(PolicyType policyType, BigDecimal coverageAmount, int policyTerm) {
        if (policyType == null) {
            throw new InvalidInputException("Policy type is required.");
        }
        validateCoverageAmount(coverageAmount);
        validatePolicyTerm(policyTerm);

        BigDecimal annualRate = policyType.getAnnualRate();
        BigDecimal annualPremium = calculateAnnualPremium(coverageAmount, annualRate);
        BigDecimal totalPremium = calculateTotalPremium(annualPremium, policyTerm);

        logger.debug("Calculation computed: type={}, coverage={}, term={}, annualPremium={}, totalPremium={}",
                policyType, coverageAmount, policyTerm, annualPremium, totalPremium);

        return new CalculationResult(annualRate, annualPremium, totalPremium);
    }

    private void validateCoverageAmount(BigDecimal coverageAmount) {
        if (coverageAmount == null) {
            throw new InvalidInputException("Coverage amount cannot be null.");
        }
        if (coverageAmount.compareTo(MIN_COVERAGE) < 0 || coverageAmount.compareTo(MAX_COVERAGE) > 0) {
            throw new InvalidInputException(String.format(
                    "Coverage amount must be between ₹%s and ₹%s.",
                    MIN_COVERAGE.toPlainString(), MAX_COVERAGE.toPlainString()
            ));
        }
    }

    private void validatePolicyTerm(int policyTerm) {
        if (policyTerm < MIN_TERM || policyTerm > MAX_TERM) {
            throw new InvalidInputException(String.format(
                    "Policy term must be between %d and %d years.",
                    MIN_TERM, MAX_TERM
            ));
        }
    }

    /**
     * Value record for calculation outputs.
     */
    public record CalculationResult(BigDecimal annualRate, BigDecimal annualPremium, BigDecimal totalPremium) {}
}
