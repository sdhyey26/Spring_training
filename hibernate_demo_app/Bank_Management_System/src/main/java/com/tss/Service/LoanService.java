package com.tss.Service;

import com.tss.Dto.*;
import com.tss.Entity.*;
import com.tss.Exception.BadRequestException;
import com.tss.Exception.ResourceNotFoundException;
import com.tss.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final LoanPaymentRepository loanPaymentRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public LoanResponseDto applyForLoan(LoanApplicationRequestDto request, String username) {
        // Get user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Get account: if not provided, pick first user's account
        Account account;
        if (request.getAccountNumber() == null || request.getAccountNumber().isBlank()) {
            account = accountRepository.findFirstByUser(user)
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found for user"));
        } else {
            account = accountRepository.findById(request.getAccountNumber())
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        }

        // Validate account belongs to user
        if (!account.getUser().getUserId().equals(user.getUserId())) {
            throw new BadRequestException("Account does not belong to the user");
        }

        // Check if user has any active loans
        List<Loan> activeLoans = loanRepository.findActiveLoansByUserId(user.getUserId());
        if (!activeLoans.isEmpty()) {
            throw new BadRequestException("User already has active loans. Cannot apply for new loan.");
        }

        // Calculate EMI and total amount
        BigDecimal interestRate = getInterestRateForLoanType(request.getLoanType());
        BigDecimal monthlyEmi = calculateEMI(request.getLoanAmount(), interestRate, request.getLoanTenureMonths());
        BigDecimal totalAmount = monthlyEmi.multiply(BigDecimal.valueOf(request.getLoanTenureMonths()));

        // Create loan application
        Loan loan = Loan.builder()
                .user(user)
                .account(account)
                .loanType(request.getLoanType())
                .loanAmount(request.getLoanAmount())
                .interestRate(interestRate)
                .loanTenureMonths(request.getLoanTenureMonths())
                .monthlyEmi(monthlyEmi)
                .totalAmount(totalAmount)
                .status("PENDING")
                .purpose(request.getPurpose())
                .employmentType(request.getEmploymentType())
                .monthlyIncome(request.getMonthlyIncome())
                .appliedAt(Instant.now())
                .remainingAmount(totalAmount)
                .paidAmount(BigDecimal.ZERO)
                .build();

        Loan savedLoan = loanRepository.save(loan);
        return convertToResponseDto(savedLoan);
    }

    @Transactional
    public LoanResponseDto approveLoan(LoanApprovalRequestDto request) {
        Loan loan = loanRepository.findById(request.getLoanId())
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));

        if (!"PENDING".equals(loan.getStatus())) {
            throw new BadRequestException("Loan is not in pending status");
        }

        if ("APPROVE".equals(request.getAction())) {
            // Update loan with approval details
            loan.setStatus("APPROVED");
            loan.setApprovedAt(Instant.now());
            loan.setAdminNotes(request.getAdminNotes());
            
            if (request.getInterestRate() != null) {
                loan.setInterestRate(request.getInterestRate());
                // Recalculate EMI and total amount with new interest rate
                BigDecimal monthlyEmi = calculateEMI(loan.getLoanAmount(), request.getInterestRate(), loan.getLoanTenureMonths());
                BigDecimal totalAmount = monthlyEmi.multiply(BigDecimal.valueOf(loan.getLoanTenureMonths()));
                loan.setMonthlyEmi(monthlyEmi);
                loan.setTotalAmount(totalAmount);
                loan.setRemainingAmount(totalAmount);
            }
            
            // Set loan dates
            loan.setStartDate(LocalDate.now());
            loan.setEndDate(LocalDate.now().plusMonths(loan.getLoanTenureMonths()));
            loan.setNextPaymentDate(LocalDate.now().plusMonths(1));
            
        } else if ("REJECT".equals(request.getAction())) {
            loan.setStatus("REJECTED");
            loan.setRejectionReason(request.getRejectionReason());
            loan.setAdminNotes(request.getAdminNotes());
        }

        Loan updatedLoan = loanRepository.save(loan);
        return convertToResponseDto(updatedLoan);
    }

    @Transactional
    public LoanResponseDto disburseLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));

        if (!"APPROVED".equals(loan.getStatus())) {
            throw new BadRequestException("Loan is not approved");
        }

        // Check if account has sufficient balance (for internal disbursement)
        Account account = accountRepository.findById(loan.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        // Add loan amount to account balance
        account.setBalance(account.getBalance().add(loan.getLoanAmount()));
        accountRepository.save(account);

        // Update loan status
        loan.setStatus("ACTIVE");
        loan.setDisbursedAt(Instant.now());
        loan.setStartDate(LocalDate.now());
        loan.setNextPaymentDate(LocalDate.now().plusMonths(1));

        Loan updatedLoan = loanRepository.save(loan);

        // Create transaction record
        Transaction transaction = Transaction.builder()
                .fromAccount("BANK_LOAN")
                .toAccount(loan.getAccountNumber())
                .amount(loan.getLoanAmount())
                .type("LOAN_DISBURSEMENT")
                .category("LOAN")
                .description("Loan disbursement - " + loan.getLoanType())
                .referenceNumber("LOAN_" + loan.getId())
                .timestamp(Instant.now())
                .build();
        transactionRepository.save(transaction);

        return convertToResponseDto(updatedLoan);
    }

    @Transactional
    public LoanPaymentResponseDto makePayment(LoanPaymentRequestDto request, String username) {
        // Get user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Get loan: if id missing, auto-pick single active loan for user
        Loan loan;
        if (request.getLoanId()== null) {
            List<Loan> activeLoans = loanRepository.findActiveLoansByUserId(user.getUserId());
            if (activeLoans.isEmpty()) {
                throw new BadRequestException("No active loans found for user");
            }
            if (activeLoans.size() > 1) {
                throw new BadRequestException("Multiple active loans found. Please specify loanId.");
            }
            loan = activeLoans.get(0);
        } else {
            loan = loanRepository.findById(request.getLoanId())
                    .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        }

        // Validate loan belongs to user
        if (!loan.getUser().getUserId().equals(user.getUserId())) {
            throw new BadRequestException("Loan does not belong to the user");
        }

        if (!"ACTIVE".equals(loan.getStatus())) {
            throw new BadRequestException("Loan is not active");
        }

        // Get account
        Account account = accountRepository.findById(loan.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        // Check sufficient balance
        if (account.getBalance().compareTo(request.getPaymentAmount()) < 0) {
            throw new BadRequestException("Insufficient account balance");
        }

        // Calculate payment breakdown
        BigDecimal interestAmount = calculateInterestAmount(loan);
        BigDecimal principalAmount = request.getPaymentAmount().subtract(interestAmount);

        // Ensure principal amount is not negative
        if (principalAmount.compareTo(BigDecimal.ZERO) < 0) {
            principalAmount = request.getPaymentAmount();
            interestAmount = BigDecimal.ZERO;
        }

        // Deduct payment from account
        account.setBalance(account.getBalance().subtract(request.getPaymentAmount()));
        accountRepository.save(account);

        // Update loan
        loan.setPaidAmount(loan.getPaidAmount().add(request.getPaymentAmount()));
        loan.setRemainingAmount(loan.getRemainingAmount().subtract(principalAmount));

        // Check if loan is fully paid
        if (loan.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
            loan.setStatus("COMPLETED");
            loan.setRemainingAmount(BigDecimal.ZERO);
        } else {
            // Update next payment date
            loan.setNextPaymentDate(loan.getNextPaymentDate().plusMonths(1));
        }

        loanRepository.save(loan);

        // Create loan payment record
        LoanPayment payment = LoanPayment.builder()
                .loan(loan)
                .paymentAmount(request.getPaymentAmount())
                .principalAmount(principalAmount)
                .interestAmount(interestAmount)
                .paymentDate(request.getPaymentDate() != null ? request.getPaymentDate() : LocalDate.now())
                .paymentTimestamp(Instant.now())
                .paymentMethod(request.getPaymentMethod())
                .status("COMPLETED")
                .transactionReference("PAY_" + System.currentTimeMillis())
                .notes(request.getNotes())
                .remainingBalance(loan.getRemainingAmount())
                .build();

        LoanPayment savedPayment = loanPaymentRepository.save(payment);

        // Create transaction record
        Transaction transaction = Transaction.builder()
                .fromAccount(loan.getAccountNumber())
                .toAccount("BANK_LOAN")
                .amount(request.getPaymentAmount())
                .type("LOAN_PAYMENT")
                .category("LOAN")
                .description("Loan payment - " + loan.getLoanType())
                .referenceNumber(savedPayment.getTransactionReference())
                .timestamp(Instant.now())
                .build();
        transactionRepository.save(transaction);

        return convertToPaymentResponseDto(savedPayment);
    }

    public List<LoanResponseDto> getUserLoans(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Loan> loans = loanRepository.findByUserIdOrderByAppliedAtDesc(user.getUserId());
        return loans.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public List<LoanResponseDto> getAllLoans() {
        List<Loan> loans = loanRepository.findAll();
        return loans.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public List<LoanResponseDto> getLoansByStatus(String status) {
        List<Loan> loans = loanRepository.findByStatus(status);
        return loans.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public List<LoanPaymentResponseDto> getLoanPayments(Long loanId) {
        List<LoanPayment> payments = loanPaymentRepository.findByLoanIdOrderByPaymentDateDesc(loanId);
        return payments.stream()
                .map(this::convertToPaymentResponseDto)
                .collect(Collectors.toList());
    }

    public LoanResponseDto getLoanById(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        return convertToResponseDto(loan);
    }

    // Helper methods
    private BigDecimal getInterestRateForLoanType(String loanType) {
        return switch (loanType) {
            case "PERSONAL" -> new BigDecimal("12.0");
            case "HOME" -> new BigDecimal("8.5");
            case "CAR" -> new BigDecimal("10.0");
            case "BUSINESS" -> new BigDecimal("15.0");
            case "EDUCATION" -> new BigDecimal("9.0");
            default -> new BigDecimal("12.0");
        };
    }

    private BigDecimal calculateEMI(BigDecimal principal, BigDecimal annualRate, int months) {
        if (annualRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);
        }

        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(1200), 6, RoundingMode.HALF_UP);
        BigDecimal emi = principal.multiply(monthlyRate)
                .multiply(BigDecimal.ONE.add(monthlyRate).pow(months))
                .divide(BigDecimal.ONE.add(monthlyRate).pow(months).subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);

        return emi;
    }

    private BigDecimal calculateInterestAmount(Loan loan) {
        // Simple interest calculation for current month
        BigDecimal monthlyRate = loan.getInterestRate().divide(BigDecimal.valueOf(1200), 6, RoundingMode.HALF_UP);
        return loan.getRemainingAmount().multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
    }

    private LoanResponseDto convertToResponseDto(Loan loan) {
        return LoanResponseDto.builder()
                .id(loan.getId())
                .userId(loan.getUserId())
                .accountNumber(loan.getAccountNumber())
                .loanType(loan.getLoanType())
                .loanAmount(loan.getLoanAmount())
                .interestRate(loan.getInterestRate())
                .loanTenureMonths(loan.getLoanTenureMonths())
                .monthlyEmi(loan.getMonthlyEmi())
                .totalAmount(loan.getTotalAmount())
                .status(loan.getStatus())
                .purpose(loan.getPurpose())
                .employmentType(loan.getEmploymentType())
                .monthlyIncome(loan.getMonthlyIncome())
                .appliedAt(loan.getAppliedAt())
                .approvedAt(loan.getApprovedAt())
                .disbursedAt(loan.getDisbursedAt())
                .startDate(loan.getStartDate())
                .endDate(loan.getEndDate())
                .nextPaymentDate(loan.getNextPaymentDate())
                .remainingAmount(loan.getRemainingAmount())
                .paidAmount(loan.getPaidAmount())
                .adminNotes(loan.getAdminNotes())
                .rejectionReason(loan.getRejectionReason())
                .build();
    }

    private LoanPaymentResponseDto convertToPaymentResponseDto(LoanPayment payment) {
        return LoanPaymentResponseDto.builder()
                .id(payment.getId())
                .loanId(payment.getLoanId())
                .paymentAmount(payment.getPaymentAmount())
                .principalAmount(payment.getPrincipalAmount())
                .interestAmount(payment.getInterestAmount())
                .paymentDate(payment.getPaymentDate())
                .paymentTimestamp(payment.getPaymentTimestamp())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .transactionReference(payment.getTransactionReference())
                .notes(payment.getNotes())
                .remainingBalance(payment.getRemainingBalance())
                .build();
    }
}
