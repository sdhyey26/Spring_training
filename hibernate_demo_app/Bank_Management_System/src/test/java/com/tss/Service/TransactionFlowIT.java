package com.tss.Service;

import com.tss.Dto.DepositRequestDto;
import com.tss.Dto.TransferRequestDto;
import com.tss.Dto.WithdrawalRequestDto;
import com.tss.Entity.Account;
import com.tss.Repository.AccountRepository;
import com.tss.Entity.User;
import com.tss.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TransactionFlowIT {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private DepositWithdrawalService depositWithdrawalService;

    @Autowired
    private TransferService transferService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    void deposit_withdraw_transfer_flow() {
        // Arrange: create two accounts
        User userA = User.builder()
                .username("ituserA_" + UUID.randomUUID().toString().substring(0, 8))
                .password("password")
                .role("Customer")
                .email("ita_" + UUID.randomUUID().toString().substring(0, 6) + "@ex.com")
                .build();
        User userB = User.builder()
                .username("ituserB_" + UUID.randomUUID().toString().substring(0, 8))
                .password("password")
                .role("Customer")
                .email("itb_" + UUID.randomUUID().toString().substring(0, 6) + "@ex.com")
                .build();
        userRepository.save(userA);
        userRepository.save(userB);

        Account a1 = Account.builder()
                .accountNumber(UUID.randomUUID().toString().replace("-", "").substring(0, 16))
                .name("Test A")
                .mobile("1111111111")
                .email("accta_" + UUID.randomUUID().toString().substring(0, 6) + "@ex.com")
                .aadhar("111122223333")
                .accountType("SAVINGS")
                .balance(new BigDecimal("1000.00"))
                .createdAt(Instant.now())
                .status("ACTIVE")
                .user(userA)
                .build();
        Account a2 = Account.builder()
                .accountNumber(UUID.randomUUID().toString().replace("-", "").substring(0, 16))
                .name("Test B")
                .mobile("2222222222")
                .email("acctb_" + UUID.randomUUID().toString().substring(0, 6) + "@ex.com")
                .aadhar("444455556666")
                .accountType("SAVINGS")
                .balance(new BigDecimal("500.00"))
                .createdAt(Instant.now())
                .status("ACTIVE")
                .user(userB)
                .build();
        accountRepository.save(a1);
        accountRepository.save(a2);

        // Act: deposit 100 to a1
        depositWithdrawalService.deposit(DepositRequestDto.builder()
                .accountNumber(a1.getAccountNumber())
                .amount(new BigDecimal("100.00"))
                .description("IT deposit")
                .build());

        // Act: withdraw 50 from a1
        depositWithdrawalService.withdrawal(WithdrawalRequestDto.builder()
                .accountNumber(a1.getAccountNumber())
                .amount(new BigDecimal("50.00"))
                .description("IT withdraw")
                .build());

        // Act: transfer 25 from a1 to a2
        transferService.transfer(TransferRequestDto.builder()
                .fromAccount(a1.getAccountNumber())
                .toAccount(a2.getAccountNumber())
                .amount(new BigDecimal("25.00"))
                .build());

        // Assert balances: a1: 1000 +100 -50 -25 = 1025; a2: 500 +25 = 525
        Account a1After = accountRepository.findById(a1.getAccountNumber()).orElseThrow();
        Account a2After = accountRepository.findById(a2.getAccountNumber()).orElseThrow();
        assertThat(a1After.getBalance()).isEqualByComparingTo(new BigDecimal("1025.00"));
        assertThat(a2After.getBalance()).isEqualByComparingTo(new BigDecimal("525.00"));
    }
}


