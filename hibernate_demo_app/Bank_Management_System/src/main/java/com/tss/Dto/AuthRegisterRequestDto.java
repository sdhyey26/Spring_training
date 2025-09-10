package com.tss.Dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRegisterRequestDto {
    private String username;
    private String password;
    private String fullName;
    private String mobile;
    private String email;
    private String aadhar;
    private String accountType;
    private BigDecimal balance;
    private String role;
}


