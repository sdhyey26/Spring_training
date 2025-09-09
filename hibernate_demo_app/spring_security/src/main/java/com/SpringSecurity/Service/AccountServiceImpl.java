package com.SpringSecurity.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.SpringSecurity.Dto.AccountRequestDto;
import com.SpringSecurity.Dto.AccountResponseDto;
import com.SpringSecurity.Entity.Account;
import com.SpringSecurity.Exception.UserApiException;
import com.SpringSecurity.Repository.AccountRepo;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountRepo accountRepo;

	@Override
	public List<AccountResponseDto> getAllAccounts() {
		return accountRepo.findAll()
			.stream()
			.map(this::toResponse)
			.collect(Collectors.toList());
	}

	@Override
	public AccountResponseDto getParticularAccount(int id) {
		Account acc = accountRepo.findById(id)
			.orElseThrow(() -> new UserApiException(HttpStatus.NOT_FOUND, "Account not found"));
		return toResponse(acc);
	}

	@Override
	public AccountResponseDto disableAccount(int id) {
		Account acc = accountRepo.findById(id)
			.orElseThrow(() -> new UserApiException(HttpStatus.NOT_FOUND, "Account not found"));
		AccountResponseDto resp = toResponse(acc);
		accountRepo.delete(acc); 
		return resp;
	}

	private AccountResponseDto toResponse(Account a) {
		AccountResponseDto dto = new AccountResponseDto();
		dto.setId(a.getId());
		dto.setName(a.getName());
		dto.setAccountNumber(a.getAccount_number());
		dto.setBalance(a.getBalance());
		return dto;
	}

	@SuppressWarnings("unused")
	private Account toEntity(AccountRequestDto dto) {
		Account a = new Account();
		a.setName(dto.getName());
		a.setAccount_number(dto.getAccountNumber());
		a.setBalance(dto.getBalance());
		return a;
	}

	@Override
	public AccountResponseDto createAccount(AccountRequestDto dto) {
		Account entity = toEntity(dto);
		Account saved = accountRepo.save(entity);
		return toResponse(saved);
	}
}