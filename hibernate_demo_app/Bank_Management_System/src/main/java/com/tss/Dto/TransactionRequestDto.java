package com.tss.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class TransactionRequestDto {
    private String fromAccountNumber;
    private String toAccountNumber; 
    private Long beneficiaryId; 
    private Double amount;
    private String description;
}
