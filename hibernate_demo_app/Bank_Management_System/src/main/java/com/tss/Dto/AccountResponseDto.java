package com.tss.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponseDto {
    private String accountNumber;
    private String name;
    private String mobile;
    private String email;
    private String aadhar;
    private String accountType;
    private BigDecimal balance;
}


