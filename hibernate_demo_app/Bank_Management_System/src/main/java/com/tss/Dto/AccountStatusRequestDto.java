package com.tss.Dto;

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
public class AccountStatusRequestDto {
    private String accountNumber;
    private String status; // ACTIVE, SUSPENDED, CLOSED, FROZEN
    private String reason;
}
