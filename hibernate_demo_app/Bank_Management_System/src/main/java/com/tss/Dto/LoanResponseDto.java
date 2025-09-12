package com.tss.Dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanResponseDto {

    private Long id;
    private Long userId;
    private String accountNumber;
    private String loanType;
    private BigDecimal loanAmount;
    private BigDecimal interestRate;
    private Integer loanTenureMonths;
    private BigDecimal monthlyEmi;
    private BigDecimal totalAmount;
    private String status;
    private String purpose;
    private String employmentType;
    private BigDecimal monthlyIncome;
    private Instant appliedAt;
    private Instant approvedAt;
    private Instant disbursedAt;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate nextPaymentDate;
    private BigDecimal remainingAmount;
    private BigDecimal paidAmount;
    private String adminNotes;
    private String rejectionReason;
}
