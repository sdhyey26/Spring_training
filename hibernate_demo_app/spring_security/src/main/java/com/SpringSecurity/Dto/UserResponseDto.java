package com.SpringSecurity.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class UserResponseDto {

	private int userid;
	private String username;
	
}
