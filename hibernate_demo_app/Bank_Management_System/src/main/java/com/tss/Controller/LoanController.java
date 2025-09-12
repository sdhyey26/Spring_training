package com.tss.Controller;

import com.tss.Dto.*;
import com.tss.Service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    // Customer endpoints
    @PostMapping("/apply")
    public ResponseEntity<LoanResponseDto> applyForLoan(@Valid @RequestBody LoanApplicationRequestDto request,
                                                       Authentication authentication) {
        String username = (String) authentication.getPrincipal();
        LoanResponseDto response = loanService.applyForLoan(request, username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-loans")
    public ResponseEntity<List<LoanResponseDto>> getMyLoans(Authentication authentication) {
        String username = (String) authentication.getPrincipal();
        List<LoanResponseDto> loans = loanService.getUserLoans(username);
        return ResponseEntity.ok(loans);
    }

    @PostMapping("/payment")
    public ResponseEntity<LoanPaymentResponseDto> makePayment(@Valid @RequestBody LoanPaymentRequestDto request,
                                                             Authentication authentication) {
        String username = (String) authentication.getPrincipal();
        LoanPaymentResponseDto response = loanService.makePayment(request, username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{loanId}/payments")
    public ResponseEntity<List<LoanPaymentResponseDto>> getLoanPayments(@PathVariable Long loanId) {
        List<LoanPaymentResponseDto> payments = loanService.getLoanPayments(loanId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{loanId}")
    public ResponseEntity<LoanResponseDto> getLoanById(@PathVariable Long loanId) {
        LoanResponseDto loan = loanService.getLoanById(loanId);
        return ResponseEntity.ok(loan);
    }

    // Admin endpoints
    @GetMapping
    public ResponseEntity<List<LoanResponseDto>> getAllLoans() {
        List<LoanResponseDto> loans = loanService.getAllLoans();
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<LoanResponseDto>> getLoansByStatus(@PathVariable String status) {
        List<LoanResponseDto> loans = loanService.getLoansByStatus(status);
        return ResponseEntity.ok(loans);
    }

    @PostMapping("/approve")
    public ResponseEntity<LoanResponseDto> approveLoan(@Valid @RequestBody LoanApprovalRequestDto request) {
        LoanResponseDto response = loanService.approveLoan(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{loanId}/disburse")
    public ResponseEntity<LoanResponseDto> disburseLoan(@PathVariable Long loanId) {
        LoanResponseDto response = loanService.disburseLoan(loanId);
        return ResponseEntity.ok(response);
    }
}
