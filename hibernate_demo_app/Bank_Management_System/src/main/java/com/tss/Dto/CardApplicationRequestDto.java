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
public class CardApplicationRequestDto {
    // Optional for customers; inferred from authenticated user if absent
    private Long userId;
    // Optional for customers; inferred as first account if absent
    private String accountNumber;
    private String cardType;
}


