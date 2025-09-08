package com.tss.Service;

import com.tss.Dto.Admin.AdminCardActionRequestDto;
import com.tss.Dto.Admin.AdminUpdateAccountRequestDto;
import com.tss.Dto.Admin.AdminUpdateUserRequestDto;
import com.tss.Entity.Account;
import com.tss.Entity.CardApplication;
import com.tss.Entity.User;
import com.tss.Exception.BadRequestException;
import com.tss.Exception.ResourceNotFoundException;
import com.tss.Repository.AccountRepository;
import com.tss.Repository.CardApplicationRepository;
import com.tss.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final CardApplicationRepository cardApplicationRepository;

    public List<User> listUsers() {
        return userRepository.findAll();
    }

    public List<Account> listAccounts() {
        return accountRepository.findAll();
    }

    @Transactional
    public User updateUser(AdminUpdateUserRequestDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setFullName(request.getFullName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Transactional
    public Account updateAccount(AdminUpdateAccountRequestDto request) {
        Account account = accountRepository.findById(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        account.setAccountType(request.getAccountType());
        if (request.getBalance() == null) {
            throw new BadRequestException("Balance required");
        }
        account.setBalance(request.getBalance());
        return accountRepository.save(account);
    }

    @Transactional
    public void deleteAccount(String accountNumber) {
        accountRepository.deleteById(accountNumber);
    }

    @Transactional
    public CardApplication cardAction(AdminCardActionRequestDto request) {
        CardApplication app = cardApplicationRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Card application not found"));
        String action = request.getAction();
        if ("approve".equalsIgnoreCase(action)) {
            app.setStatus("Approved");
            app.setApprovedAt(Instant.now());
        } else if ("reject".equalsIgnoreCase(action)) {
            app.setStatus("Rejected");
            app.setApprovedAt(null);
        } else {
            throw new BadRequestException("Invalid action");
        }
        return cardApplicationRepository.save(app);
    }
}


