package com.premiumcalculator.repository;

import com.premiumcalculator.model.PremiumTransaction;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory thread-safe implementation of TransactionRepository.
 *
 * NOTE: Data stored in this repository resides in memory only and will reset upon application restart.
 * To enable persistent storage, implement a Spring Data JPA repository targeting a relational database.
 */
@Repository
public class InMemoryTransactionRepository implements TransactionRepository {

    private final Map<String, PremiumTransaction> store = new ConcurrentHashMap<>();

    @Override
    public PremiumTransaction save(PremiumTransaction transaction) {
        if (transaction == null || transaction.getId() == null) {
            throw new IllegalArgumentException("Transaction and transaction ID cannot be null.");
        }
        store.put(transaction.getId(), transaction);
        return transaction;
    }

    @Override
    public Optional<PremiumTransaction> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<PremiumTransaction> findAll() {
        return store.values().stream()
                .sorted(Comparator.comparing(PremiumTransaction::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(String id) {
        return id != null && store.containsKey(id);
    }

    @Override
    public void deleteAll() {
        store.clear();
    }
}
