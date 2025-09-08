package com.tss.Controller;

import com.tss.Dto.CardApplicationRequestDto;
import com.tss.Entity.CardApplication;
import com.tss.Service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping("/apply")
    public ResponseEntity<CardApplication> apply(@RequestBody CardApplicationRequestDto request) {
        return ResponseEntity.ok(cardService.apply(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CardApplication>> byUser(@PathVariable Long userId) {
        return ResponseEntity.ok(cardService.getByUser(userId));
    }

    @GetMapping
    public ResponseEntity<List<CardApplication>> byStatus(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(cardService.getByStatus(status));
    }
}


