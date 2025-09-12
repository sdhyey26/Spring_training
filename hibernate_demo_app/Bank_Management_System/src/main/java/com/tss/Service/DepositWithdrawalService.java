package com.tss.Service;

import com.tss.Dto.DepositRequestDto;
import com.tss.Dto.WithdrawalRequestDto;
import com.tss.Entity.Account;
import com.tss.Entity.Transaction;
import com.tss.Exception.BadRequestException;
import com.tss.Exception.ResourceNotFoundException;
import com.tss.Repository.AccountRepository;
import com.tss.Repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DepositWithdrawalService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public void deposit(DepositRequestDto request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be positive");
        }

        Account account = accountRepository.findById(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!"ACTIVE".equals(account.getStatus())) {
            throw new BadRequestException("Account is not active. Current status: " + account.getStatus());
        }

        // Update account balance
        account.setBalance(account.getBalance().add(request.getAmount()));
        accountRepository.save(account);

        // Create transaction record
        Transaction transaction = Transaction.builder()
                .fromAccount(request.getAccountNumber())
                .toAccount(request.getAccountNumber())
                .amount(request.getAmount())
                .type("DEPOSIT")
                .category("DEPOSIT")
                .description(request.getDescription() != null ? request.getDescription() : "Cash Deposit")
                .referenceNumber(request.getReferenceNumber() != null ? request.getReferenceNumber() : generateReferenceNumber())
                .timestamp(Instant.now())
                .build();

        transactionRepository.save(transaction);
    }

    @Transactional
    public void withdrawal(WithdrawalRequestDto request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be positive");
        }

        Account account = accountRepository.findById(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!"ACTIVE".equals(account.getStatus())) {
            throw new BadRequestException("Account is not active. Current status: " + account.getStatus());
        }

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BadRequestException("Insufficient balance");
        }

        // Update account balance
        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepository.save(account);

        // Create transaction record
        Transaction transaction = Transaction.builder()
                .fromAccount(request.getAccountNumber())
                .toAccount(request.getAccountNumber())
                .amount(request.getAmount())
                .type("WITHDRAWAL")
                .category("WITHDRAWAL")
                .description(request.getDescription() != null ? request.getDescription() : "Cash Withdrawal")
                .referenceNumber(request.getReferenceNumber() != null ? request.getReferenceNumber() : generateReferenceNumber())
                .timestamp(Instant.now())
                .build();

        transactionRepository.save(transaction);
    }

    private String generateReferenceNumber() {
        return "REF" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
