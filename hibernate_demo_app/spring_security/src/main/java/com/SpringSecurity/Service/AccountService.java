package com.SpringSecurity.Service;

import java.util.List;

import com.SpringSecurity.Dto.AccountRequestDto;
import com.SpringSecurity.Dto.AccountResponseDto;

public interface AccountService {
	List<AccountResponseDto> getAllAccounts();
	AccountResponseDto getParticularAccount(int id);
	AccountResponseDto disableAccount(int id);
	AccountResponseDto createAccount(AccountRequestDto dto);

}
