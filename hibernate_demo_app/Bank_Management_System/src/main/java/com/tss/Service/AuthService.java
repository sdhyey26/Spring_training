package com.tss.Service;

import com.tss.Dto.AuthLoginRequestDto;
import com.tss.Dto.AuthRegisterRequestDto;
import com.tss.Dto.AuthResponseDto;
import com.tss.Entity.Account;
import com.tss.Entity.User;
import com.tss.Exception.BadRequestException;
import com.tss.Repository.AccountRepository;
import com.tss.Repository.UserRepository;
import com.tss.Config.JwtTokenProvider;
import com.tss.Dto.ChangePasswordRequestDto;
import com.tss.Dto.ForgotPasswordRequestDto;
import com.tss.Dto.ResetPasswordRequestDto;
import com.tss.Entity.PasswordResetToken;
import com.tss.Repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Transactional
    public AuthResponseDto register(AuthRegisterRequestDto request) {
        userRepository.findByUsername(request.getUsername()).ifPresent(u -> {
            throw new BadRequestException("Username already exists");
        });

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())) 
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

        String token = jwtTokenProvider.generateToken(user.getUsername(), java.util.List.of(user.getRole()));
        return AuthResponseDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .role(user.getRole())
                .token(token)
                .build();
    }

    public AuthResponseDto login(AuthLoginRequestDto request) {
        User user = userRepository.findByUsername(request.getUsername())
                .filter(u -> passwordEncoder.matches(request.getPassword(), u.getPassword()))
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));
        String token = jwtTokenProvider.generateToken(user.getUsername(), java.util.List.of(user.getRole()));
        return AuthResponseDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .role(user.getRole())
                .token(token)
                .build();
    }

    public void changePassword(Long userId, ChangePasswordRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public void forgotPassword(ForgotPasswordRequestDto request) {
        User user = userRepository.findByUsername(request.getUsernameOrEmail())
                .or(() -> userRepository.findByEmail(request.getUsernameOrEmail()))
                .orElseThrow(() -> new BadRequestException("User not found"));
        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setToken(java.util.UUID.randomUUID().toString());
        token.setExpiresAt(java.time.Instant.now().plus(java.time.Duration.ofMinutes(15)));
        passwordResetTokenRepository.save(token);
        // TODO: send token via email/SMS (out of scope here)
    }

    public void resetPassword(ResetPasswordRequestDto request) {
        PasswordResetToken token = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new BadRequestException("Invalid token"));
        if (token.isUsed() || token.getExpiresAt().isBefore(java.time.Instant.now())) {
            throw new BadRequestException("Token expired or used");
        }
        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        token.setUsed(true);
        passwordResetTokenRepository.save(token);
    }
}


