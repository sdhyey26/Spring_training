package com.SpringSecurity.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SpringSecurity.Dto.JwtAuthResponse;
import com.SpringSecurity.Dto.LoginDto;
import com.SpringSecurity.Service.AuthService;

@RestController
@RequestMapping("/api/login")
public class LoginController {

	@Autowired
	private AuthService authService;

	@PostMapping
	public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginDto loginDto) {
		String token = authService.login(loginDto);
		JwtAuthResponse response = new JwtAuthResponse(token, "Bearer");
		return ResponseEntity.ok(response);
	}
}
