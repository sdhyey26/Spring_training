package com.SpringSecurity.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SpringSecurity.Dto.RegistrationDto;
import com.SpringSecurity.Dto.UserResponseDto;
import com.SpringSecurity.Service.AuthService;

@RestController
@RequestMapping("/api/register")
public class RegisterController {

	@Autowired
	private AuthService authService;

	@PostMapping
	public ResponseEntity<UserResponseDto> register(@RequestBody RegistrationDto registrationDto) {
		UserResponseDto created = authService.register(registrationDto);
		return ResponseEntity.ok(created);
	}
}


