package com.tss.Service;

import com.tss.Dto.StatementDto;
import com.tss.Dto.StatementRequestDto;
import com.tss.Dto.TransactionSummaryDto;
import com.tss.Entity.Account;
import com.tss.Entity.Transaction;
import com.tss.Exception.ResourceNotFoundException;
import com.tss.Repository.AccountRepository;
import com.tss.Repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatementService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public StatementDto generateStatement(StatementRequestDto request) {
        Account account = accountRepository.findById(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        Instant fromInstant = request.getFromDate().atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant toInstant = request.getToDate().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();

        List<Transaction> transactions = transactionRepository.findByAccountAndDateRange(
                request.getAccountNumber(), fromInstant, toInstant);

        BigDecimal openingBalance = calculateOpeningBalance(account, fromInstant);

        BigDecimal totalDebits = transactions.stream()
                .filter(t -> "DEBIT".equals(t.getType()) || "WITHDRAWAL".equals(t.getType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredits = transactions.stream()
                .filter(t -> "CREDIT".equals(t.getType()) || "DEPOSIT".equals(t.getType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal closingBalance = openingBalance.add(totalCredits).subtract(totalDebits);

        List<TransactionSummaryDto> transactionSummaries = transactions.stream()
                .map(this::convertToTransactionSummary)
                .collect(Collectors.toList());

        return StatementDto.builder()
                .accountNumber(account.getAccountNumber())
                .accountHolderName(account.getName())
                .fromDate(request.getFromDate())
                .toDate(request.getToDate())
                .openingBalance(openingBalance)
                .closingBalance(closingBalance)
                .totalDebits(totalDebits)
                .totalCredits(totalCredits)
                .transactions(transactionSummaries)
                .build();
    }

    private BigDecimal calculateOpeningBalance(Account account, Instant fromInstant) {
        List<Transaction> previousTransactions = transactionRepository.findByAccountBeforeDate(
                account.getAccountNumber(), fromInstant);

        if (previousTransactions.isEmpty()) {
            return account.getBalance(); 
        }

        BigDecimal totalCredits = previousTransactions.stream()
                .filter(t -> "CREDIT".equals(t.getType()) || "DEPOSIT".equals(t.getType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDebits = previousTransactions.stream()
                .filter(t -> "DEBIT".equals(t.getType()) || "WITHDRAWAL".equals(t.getType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalCredits.subtract(totalDebits);
    }

    private TransactionSummaryDto convertToTransactionSummary(Transaction transaction) {
        return TransactionSummaryDto.builder()
                .id(transaction.getId())
                .fromAccount(transaction.getFromAccount())
                .toAccount(transaction.getToAccount())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .category(transaction.getCategory())
                .description(transaction.getDescription())
                .referenceNumber(transaction.getReferenceNumber())
                .timestamp(transaction.getTimestamp())
                .build();
    }
}
