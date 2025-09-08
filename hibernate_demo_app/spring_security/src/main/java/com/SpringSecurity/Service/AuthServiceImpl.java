package com.SpringSecurity.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.SpringSecurity.Dto.LoginDto;
import com.SpringSecurity.Dto.RegistrationDto;
import com.SpringSecurity.Dto.UserResponseDto;
import com.SpringSecurity.Entity.Role;
import com.SpringSecurity.Entity.User;
import com.SpringSecurity.Exception.UserApiException;
import com.SpringSecurity.Repository.RoleRepo;
import com.SpringSecurity.Repository.UserRepo;
import com.SpringSecurity.Security.JwtTokenProvider;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private RoleRepo roleRepo;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtTokenProvider tokenProvider;
	// Removed unused field: registrationDto

	@Override
	public UserResponseDto register(RegistrationDto registration) {
		if (userRepo.existsByUsername(registration.getUsername()))
			throw new UserApiException(HttpStatus.BAD_REQUEST, "User already exists");

		User user = new User();
		user.setUsername(registration.getUsername());
		user.setPassword(passwordEncoder.encode(registration.getPassword()));

		Role userRole = roleRepo.findByRoleName(registration.getRole()).get();
		userRole.getUsers().add(user);
		user.setRole(userRole);

		user = userRepo.save(user);

		UserResponseDto dto = new UserResponseDto();
		dto.setUserid(user.getUserId());
		dto.setUsername(user.getUsername());

		return dto;
	}

	@Override
	public String login(LoginDto loginDto) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			String token = tokenProvider.generateToken(authentication);

			return token;
		} catch (BadCredentialsException e) {
			throw new UserApiException(HttpStatus.NOT_FOUND, "Username or Password is incorrect");
		}
	}

}
