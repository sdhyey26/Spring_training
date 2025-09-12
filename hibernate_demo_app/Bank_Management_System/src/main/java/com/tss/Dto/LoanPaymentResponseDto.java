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
public class LoanPaymentResponseDto {

    private Long id;
    private Long loanId;
    private BigDecimal paymentAmount;
    private BigDecimal principalAmount;
    private BigDecimal interestAmount;
    private LocalDate paymentDate;
    private Instant paymentTimestamp;
    private String paymentMethod;
    private String status;
    private String transactionReference;
    private String notes;
    private BigDecimal remainingBalance;
}
