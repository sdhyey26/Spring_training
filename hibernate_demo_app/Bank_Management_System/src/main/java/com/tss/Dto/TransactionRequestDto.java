package com.tss.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class TransactionRequestDto {
    private String fromAccountNumber;
    private String toAccountNumber; // Optional if beneficiaryId is used
    private Long beneficiaryId; // Optional, for using a saved beneficiary
    private Double amount;
    private String description;
    // getters and setters
}
