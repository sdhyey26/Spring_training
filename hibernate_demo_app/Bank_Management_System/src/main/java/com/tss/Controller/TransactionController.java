package com.tss.Controller;

import com.tss.Dto.TransferRequestDto;
import com.tss.Entity.Transaction;
import com.tss.Service.TransactionService;
import com.tss.Service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransferService transferService;
    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@RequestBody TransferRequestDto request) {
        transferService.transfer(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<Transaction>> byAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(transactionService.getByAccount(accountNumber));
    }
}


