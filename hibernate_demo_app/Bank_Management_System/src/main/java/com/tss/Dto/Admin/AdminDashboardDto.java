package com.tss.Dto.Admin;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AdminDashboardDto {
    private long totalUsers;
    private long totalAccounts;
    private BigDecimal totalDeposits;
    private long totalTransactions;
    private long pendingCardApplications;
}


