package com.SpringSecurity.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SpringSecurity.Dto.AccountRequestDto;
import com.SpringSecurity.Dto.AccountResponseDto;
import com.SpringSecurity.Service.AccountService;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

	@Autowired
	private AccountService accountService;

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<AccountResponseDto>> getAllAccounts() {
		return ResponseEntity.ok(accountService.getAllAccounts());
	}

	@GetMapping("/{id}")
	public ResponseEntity<AccountResponseDto> getParticularAccount(@PathVariable int id) {
		return ResponseEntity.ok(accountService.getParticularAccount(id));
	}

	@PutMapping("/{id}/disable")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<AccountResponseDto> disableAccount(@PathVariable int id) {
		return ResponseEntity.ok(accountService.disableAccount(id));
	}

	@PostMapping
	public ResponseEntity<AccountResponseDto> create(@RequestBody AccountRequestDto dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(dto));
	}
}