package com.tss.Controller;

import com.tss.Dto.DepositRequestDto;
import com.tss.Dto.StatementDto;
import com.tss.Dto.StatementRequestDto;
import com.tss.Dto.TransferRequestDto;
import com.tss.Dto.WithdrawalRequestDto;
import com.tss.Entity.Transaction;
import com.tss.Service.DepositWithdrawalService;
import com.tss.Service.AccountService;
import com.tss.Service.StatementService;
import com.tss.Service.TransactionService;
import com.tss.Service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransferService transferService;
    private final TransactionService transactionService;
    private final DepositWithdrawalService depositWithdrawalService;
    private final StatementService statementService;
    private final AccountService accountService;

    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@RequestBody TransferRequestDto request, Authentication authentication) {
        if (request.getFromAccount() == null || request.getFromAccount().isBlank()) {
            String username = (String) authentication.getPrincipal();
            String accountNumber = accountService.getAccountByUsername(username).getAccountNumber();
            request.setFromAccount(accountNumber);
        }
        transferService.transfer(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/deposit")
    public ResponseEntity<Void> deposit(@RequestBody DepositRequestDto request, Authentication authentication) {
        if (request.getAccountNumber() == null || request.getAccountNumber().isBlank()) {
            String username = (String) authentication.getPrincipal();
            String accountNumber = accountService.getAccountByUsername(username).getAccountNumber();
            request.setAccountNumber(accountNumber);
        }
        depositWithdrawalService.deposit(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<Void> withdrawal(@RequestBody WithdrawalRequestDto request, Authentication authentication) {
        if (request.getAccountNumber() == null || request.getAccountNumber().isBlank()) {
            String username = (String) authentication.getPrincipal();
            String accountNumber = accountService.getAccountByUsername(username).getAccountNumber();
            request.setAccountNumber(accountNumber);
        }
        depositWithdrawalService.withdrawal(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/statement")
    public ResponseEntity<StatementDto> generateStatement(@RequestBody StatementRequestDto request, Authentication authentication) {
        if (request.getAccountNumber() == null || request.getAccountNumber().isBlank()) {
            String username = (String) authentication.getPrincipal();
            String accountNumber = accountService.getAccountByUsername(username).getAccountNumber();
            request.setAccountNumber(accountNumber);
        }
        return ResponseEntity.ok(statementService.generateStatement(request));
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<Transaction>> byAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(transactionService.getByAccount(accountNumber));
    }
}


