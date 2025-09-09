package com.tss.Service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.tss.Dto.Admin.AdminDashboardDto;
import com.tss.Repository.AccountRepository;
import com.tss.Repository.CardApplicationRepository;
import com.tss.Repository.TransactionRepository;
import com.tss.Repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CardApplicationRepository cardApplicationRepository;

    public AdminDashboardDto getDashboard() {
        long users = userRepository.count();
        long accounts = accountRepository.count();
        long transactions = transactionRepository.count();
        long pendingCards = cardApplicationRepository.countByStatus("APPLIED");
        BigDecimal totalDeposits = java.util.Optional.ofNullable(accountRepository.sumAllBalances()).orElse(BigDecimal.ZERO);

        return AdminDashboardDto.builder()
                .totalUsers(users)
                .totalAccounts(accounts)
                .totalTransactions(transactions)
                .pendingCardApplications(pendingCards)
                .totalDeposits(totalDeposits)
                .build();
    }
}


