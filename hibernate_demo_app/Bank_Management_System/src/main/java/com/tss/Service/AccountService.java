package com.tss.Service;

import com.tss.Dto.AccountResponseDto;
import com.tss.Entity.Account;
import com.tss.Exception.ResourceNotFoundException;
import com.tss.Repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public List<AccountResponseDto> getAllAccounts() {
        return accountRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public AccountResponseDto getAccount(String accountNumber) {
        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        return toDto(account);
    }

    private AccountResponseDto toDto(Account a) {
        return AccountResponseDto.builder()
                .accountNumber(a.getAccountNumber())
                .name(a.getName())
                .mobile(a.getMobile())
                .email(a.getEmail())
                .aadhar(a.getAadhar())
                .accountType(a.getAccountType())
                .balance(a.getBalance())
                .build();
    }
}


