package com.SpringSecurity.Service;

import com.SpringSecurity.Dto.LoginDto;
import com.SpringSecurity.Dto.RegistrationDto;
import com.SpringSecurity.Dto.UserResponseDto;

public interface AuthService {

	UserResponseDto register(RegistrationDto registration);
	
	String login(LoginDto loginDto);
}
