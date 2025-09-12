package com.tss.Service;

import com.tss.Dto.AccountStatusRequestDto;
import com.tss.Entity.Account;
import com.tss.Exception.BadRequestException;
import com.tss.Exception.ResourceNotFoundException;
import com.tss.Repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountManagementService {

    private final AccountRepository accountRepository;

    @Transactional
    public Account updateAccountStatus(AccountStatusRequestDto request) {
        Account account = accountRepository.findById(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        String currentStatus = account.getStatus();
        String newStatus = request.getStatus().toUpperCase();

        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new BadRequestException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }

        account.setStatus(newStatus);
        Account updatedAccount = accountRepository.save(account);

        return updatedAccount;
    }

    @Transactional
    public Account freezeAccount(String accountNumber, String reason) {
        return updateAccountStatus(AccountStatusRequestDto.builder()
                .accountNumber(accountNumber)
                .status("FROZEN")
                .reason(reason)
                .build());
    }

    @Transactional
    public Account unfreezeAccount(String accountNumber) {
        return updateAccountStatus(AccountStatusRequestDto.builder()
                .accountNumber(accountNumber)
                .status("ACTIVE")
                .reason("Account unfrozen by admin")
                .build());
    }

    @Transactional
    public Account suspendAccount(String accountNumber, String reason) {
        return updateAccountStatus(AccountStatusRequestDto.builder()
                .accountNumber(accountNumber)
                .status("SUSPENDED")
                .reason(reason)
                .build());
    }

    @Transactional
    public Account closeAccount(String accountNumber, String reason) {
        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!account.getBalance().equals(java.math.BigDecimal.ZERO)) {
            throw new BadRequestException("Cannot close account with non-zero balance. Current balance: " + account.getBalance());
        }

        return updateAccountStatus(AccountStatusRequestDto.builder()
                .accountNumber(accountNumber)
                .status("CLOSED")
                .reason(reason)
                .build());
    }

    public List<Account> getAccountsByStatus(String status) {
        return accountRepository.findByStatus(status);
    }

    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        return switch (currentStatus) {
            case "ACTIVE" -> List.of("SUSPENDED", "FROZEN", "CLOSED").contains(newStatus);
            case "SUSPENDED" -> List.of("ACTIVE", "FROZEN", "CLOSED").contains(newStatus);
            case "FROZEN" -> List.of("ACTIVE", "SUSPENDED", "CLOSED").contains(newStatus);
            case "CLOSED" -> false; // Closed accounts cannot be reopened
            default -> false;
        };
    }

}
