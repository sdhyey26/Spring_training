package com.SpringSecurity.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDto {
	private int id;
	private String name;
	private String accountNumber;
	private long balance;
}