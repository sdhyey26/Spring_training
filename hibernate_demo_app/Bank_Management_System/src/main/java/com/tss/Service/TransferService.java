package com.tss.Service;

import com.tss.Dto.TransferRequestDto;
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

@Service
@RequiredArgsConstructor
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public void transfer(TransferRequestDto request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be positive");
        }

        Account from = accountRepository.findById(request.getFromAccount())
                .orElseThrow(() -> new ResourceNotFoundException("From account not found"));
        Account to = accountRepository.findById(request.getToAccount())
                .orElseThrow(() -> new ResourceNotFoundException("To account not found"));

        if (from.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BadRequestException("Insufficient balance");
        }

        from.setBalance(from.getBalance().subtract(request.getAmount()));
        to.setBalance(to.getBalance().add(request.getAmount()));

        accountRepository.save(from);
        accountRepository.save(to);

        Transaction debit = Transaction.builder()
                .fromAccount(from.getAccountNumber())
                .toAccount(to.getAccountNumber())
                .amount(request.getAmount())
                .type("DEBIT")
                .timestamp(Instant.now())
                .build();
        Transaction credit = Transaction.builder()
                .fromAccount(from.getAccountNumber())
                .toAccount(to.getAccountNumber())
                .amount(request.getAmount())
                .type("CREDIT")
                .timestamp(Instant.now())
                .build();

        transactionRepository.save(debit);
        transactionRepository.save(credit);
    }
}


