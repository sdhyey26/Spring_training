package com.tss.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthLoginRequestDto {
    private String username;
    private String password;
    private String role;
    
    public AuthLoginRequestDto(String username , String oldpassword) {
    	this.username = username;
    	this.password = oldpassword;
    }
}


