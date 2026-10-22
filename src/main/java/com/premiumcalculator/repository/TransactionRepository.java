package com.premiumcalculator.repository;

import com.premiumcalculator.model.PremiumTransaction;

import java.util.List;
import java.util.Optional;

/**
 * Repository abstraction for persisting and retrieving insurance premium transactions.
 * Structured to allow seamless future migration from in-memory to relational databases (e.g., PostgreSQL / H2 / MySQL).
 */
public interface TransactionRepository {

    PremiumTransaction save(PremiumTransaction transaction);

    Optional<PremiumTransaction> findById(String id);

    List<PremiumTransaction> findAll();

    boolean existsById(String id);

    void deleteAll();
}
