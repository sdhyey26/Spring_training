package com.tss.Service;

import com.tss.Dto.AuthLoginRequestDto;
import com.tss.Dto.AuthRegisterRequestDto;
import com.tss.Dto.AuthResponseDto;
import com.tss.Entity.Account;
import com.tss.Entity.User;
import com.tss.Exception.BadRequestException;
import com.tss.Repository.AccountRepository;
import com.tss.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public AuthResponseDto register(AuthRegisterRequestDto request) {
        userRepository.findByUsername(request.getUsername()).ifPresent(u -> {
            throw new BadRequestException("Username already exists");
        });

        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword()) 
                .role("Customer")
                .fullName(request.getFullName())
                .mobile(request.getMobile())
                .email(request.getEmail())
                .aadhar(request.getAadhar())
                .build();
        user = userRepository.save(user);

        String accountNumber = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        Account account = Account.builder()
                .accountNumber(accountNumber)
                .user(user)
                .name(request.getFullName())
                .mobile(request.getMobile())
                .email(request.getEmail())
                .aadhar(request.getAadhar())
                .accountType(request.getAccountType())
                .balance(new BigDecimal(request.getInitialDeposit() == null ? "0" : request.getInitialDeposit()))
                .createdAt(Instant.now())
                .build();
        accountRepository.save(account);

        return AuthResponseDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    public AuthResponseDto login(AuthLoginRequestDto request) {
        User user = userRepository.findByUsername(request.getUsername())
                .filter(u -> u.getPassword().equals(request.getPassword()))
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));
        return AuthResponseDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}


