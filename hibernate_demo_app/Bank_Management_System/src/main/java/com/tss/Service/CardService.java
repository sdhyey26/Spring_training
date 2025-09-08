package com.tss.Service;

import com.tss.Dto.CardApplicationRequestDto;
import com.tss.Entity.Account;
import com.tss.Entity.CardApplication;
import com.tss.Entity.User;
import com.tss.Exception.ResourceNotFoundException;
import com.tss.Repository.AccountRepository;
import com.tss.Repository.CardApplicationRepository;
import com.tss.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final CardApplicationRepository cardApplicationRepository;

    public CardApplication apply(CardApplicationRequestDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Account account = accountRepository.findById(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        CardApplication cardApplication = CardApplication.builder()
                .user(user)
                .account(account)
                .cardType(request.getCardType())
                .status("Pending")
                .appliedAt(Instant.now())
                .build();
        return cardApplicationRepository.save(cardApplication);
    }

    public List<CardApplication> getByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return cardApplicationRepository.findByUser(user);
    }

    public List<CardApplication> getByStatus(String status) {
        if (status == null) {
            return cardApplicationRepository.findAll();
        }
        return cardApplicationRepository.findByStatus(status);
    }
}


