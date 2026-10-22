package com.premiumcalculator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Premium Calculator - Main Spring Boot Application Entry Point.
 * Demonstrates a complete DevOps pipeline for insurance premium calculation.
 */
@SpringBootApplication
public class PremiumCalculatorApplication {

    private static final Logger logger = LoggerFactory.getLogger(PremiumCalculatorApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(PremiumCalculatorApplication.class, args);
        logger.info("====================================================================");
        logger.info("  Premium Calculator Application successfully started!");
        logger.info("  Web UI:         http://localhost:8080");
        logger.info("  Health Check:   http://localhost:8080/actuator/health");
        logger.info("  Calculations:   POST http://localhost:8080/api/calculations");
        logger.info("  Transactions:   GET  http://localhost:8080/api/transactions");
        logger.info("====================================================================");
    }
}
