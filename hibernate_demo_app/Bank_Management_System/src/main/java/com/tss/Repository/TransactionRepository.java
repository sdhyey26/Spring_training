package com.tss.Repository;

import com.tss.Entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByFromAccountOrToAccountOrderByTimestampDesc(String fromAccount, String toAccount);

    Page<Transaction> findByFromAccountOrToAccount(String fromAccount, String toAccount, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE (t.fromAccount = :accountNumber OR t.toAccount = :accountNumber) " +
           "AND t.timestamp >= :fromDate AND t.timestamp <= :toDate ORDER BY t.timestamp ASC")
    List<Transaction> findByAccountAndDateRange(String accountNumber, Instant fromDate, Instant toDate);

    @Query("SELECT t FROM Transaction t WHERE (t.fromAccount = :accountNumber OR t.toAccount = :accountNumber) " +
           "AND t.timestamp < :beforeDate ORDER BY t.timestamp DESC")
    List<Transaction> findByAccountBeforeDate(String accountNumber, Instant beforeDate);
}


