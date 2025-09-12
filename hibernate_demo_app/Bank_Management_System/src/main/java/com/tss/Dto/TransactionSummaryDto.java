package com.tss.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionSummaryDto {
    private Long id;
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private String type;
    private String category;
    private String description;
    private String referenceNumber;
    private Instant timestamp;
    private BigDecimal balanceAfter;
}
