package com.tss.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tss.Dto.CardApplicationRequestDto;
import com.tss.Entity.CardApplication;
import com.tss.Service.CardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping("/apply")
    public ResponseEntity<CardApplication> apply(@RequestBody CardApplicationRequestDto request, Authentication authentication) {
        if (request.getUserId() == null || request.getAccountNumber() == null || request.getAccountNumber().isBlank()) {
            String username = (String) authentication.getPrincipal();
            return ResponseEntity.ok(cardService.applyForAuthenticatedUser(request, username));
        }
        return ResponseEntity.ok(cardService.apply(request));
    }

    @GetMapping("/user/{userId}")
	@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CardApplication>> byUser(@PathVariable Long userId) {
        return ResponseEntity.ok(cardService.getByUser(userId));
    }

    @GetMapping
	@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CardApplication>> byStatus(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(cardService.getByStatus(status));
    }
}


