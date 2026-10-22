package com.premiumcalculator.service;

import com.premiumcalculator.exception.InvalidInputException;
import com.premiumcalculator.model.PolicyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests: PremiumCalculationService")
class PremiumCalculationServiceTest {

    private PremiumCalculationService calculationService;

    @BeforeEach
    void setUp() {
        calculationService = new PremiumCalculationService();
    }

    @Test
    @DisplayName("Should correctly calculate Life Insurance premium (1.20%)")
    void shouldCalculateLifeInsurancePremium() {
        BigDecimal coverage = new BigDecimal("100000.00");
        int term = 5;

        var result = calculationService.calculate(PolicyType.LIFE, coverage, term);

        assertEquals(new BigDecimal("0.0120"), result.annualRate());
        assertEquals(new BigDecimal("1200.00"), result.annualPremium());
        assertEquals(new BigDecimal("6000.00"), result.totalPremium());
    }

    @Test
    @DisplayName("Should correctly calculate Health Insurance premium (2.40%)")
    void shouldCalculateHealthInsurancePremium() {
        BigDecimal coverage = new BigDecimal("500000.00");
        int term = 3;

        var result = calculationService.calculate(PolicyType.HEALTH, coverage, term);

        assertEquals(new BigDecimal("0.0240"), result.annualRate());
        assertEquals(new BigDecimal("12000.00"), result.annualPremium());
        assertEquals(new BigDecimal("36000.00"), result.totalPremium());
    }

    @Test
    @DisplayName("Should correctly calculate Vehicle Insurance premium (3.10%)")
    void shouldCalculateVehicleInsurancePremium() {
        BigDecimal coverage = new BigDecimal("200000.00");
        int term = 1;

        var result = calculationService.calculate(PolicyType.VEHICLE, coverage, term);

        assertEquals(new BigDecimal("0.0310"), result.annualRate());
        assertEquals(new BigDecimal("6200.00"), result.annualPremium());
        assertEquals(new BigDecimal("6200.00"), result.totalPremium());
    }

    @Test
    @DisplayName("Should correctly calculate Travel Insurance premium (1.60%)")
    void shouldCalculateTravelInsurancePremium() {
        BigDecimal coverage = new BigDecimal("50000.00");
        int term = 2;

        var result = calculationService.calculate(PolicyType.TRAVEL, coverage, term);

        assertEquals(new BigDecimal("0.0160"), result.annualRate());
        assertEquals(new BigDecimal("800.00"), result.annualPremium());
        assertEquals(new BigDecimal("1600.00"), result.totalPremium());
    }

    @ParameterizedTest(name = "Policy {0}: Coverage {1}, Term {2} => Annual {3}, Total {4}")
    @CsvSource({
            "LIFE, 10000.00, 10, 120.00, 1200.00",
            "HEALTH, 250000.00, 4, 6000.00, 24000.00",
            "VEHICLE, 750000.00, 2, 23250.00, 46500.00",
            "TRAVEL, 1000000.00, 1, 16000.00, 16000.00"
    })
    @DisplayName("Parameterized test across policy types, coverage, and terms")
    void parameterizedCalculationTest(PolicyType policyType, BigDecimal coverage, int term,
                                      BigDecimal expectedAnnual, BigDecimal expectedTotal) {
        var result = calculationService.calculate(policyType, coverage, term);
        assertEquals(expectedAnnual, result.annualPremium());
        assertEquals(expectedTotal, result.totalPremium());
    }

    @Test
    @DisplayName("Should reject coverage amount below minimum (₹1,000)")
    void shouldRejectCoverageBelowMinimum() {
        BigDecimal belowMin = new BigDecimal("999.00");
        assertThrows(InvalidInputException.class, () ->
                calculationService.calculate(PolicyType.LIFE, belowMin, 5));
    }

    @Test
    @DisplayName("Should reject coverage amount above maximum (₹10,000,000)")
    void shouldRejectCoverageAboveMaximum() {
        BigDecimal aboveMax = new BigDecimal("10000001.00");
        assertThrows(InvalidInputException.class, () ->
                calculationService.calculate(PolicyType.LIFE, aboveMax, 5));
    }

    @Test
    @DisplayName("Should reject policy term less than 1 year")
    void shouldRejectTermBelowOne() {
        assertThrows(InvalidInputException.class, () ->
                calculationService.calculate(PolicyType.LIFE, new BigDecimal("100000.00"), 0));
    }

    @Test
    @DisplayName("Should reject policy term exceeding 50 years")
    void shouldRejectTermAboveFifty() {
        assertThrows(InvalidInputException.class, () ->
                calculationService.calculate(PolicyType.LIFE, new BigDecimal("100000.00"), 51));
    }

    @Test
    @DisplayName("Should reject null policy type")
    void shouldRejectNullPolicyType() {
        assertThrows(InvalidInputException.class, () ->
                calculationService.calculate(null, new BigDecimal("100000.00"), 5));
    }

    @Test
    @DisplayName("Should reject null coverage amount")
    void shouldRejectNullCoverageAmount() {
        assertThrows(InvalidInputException.class, () ->
                calculationService.calculate(PolicyType.LIFE, null, 5));
    }
}
