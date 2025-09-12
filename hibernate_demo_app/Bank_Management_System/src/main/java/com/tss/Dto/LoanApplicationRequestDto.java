package com.tss.Dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplicationRequestDto {

    // Optional: if null, server will pick first account of logged-in user
    private String accountNumber;

    @NotBlank(message = "Loan type is required")
    @Pattern(regexp = "^(PERSONAL|HOME|CAR|BUSINESS|EDUCATION)$", 
             message = "Loan type must be one of: PERSONAL, HOME, CAR, BUSINESS, EDUCATION")
    private String loanType;

    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "1000.00", message = "Minimum loan amount is 1000")
    @DecimalMax(value = "10000000.00", message = "Maximum loan amount is 10,000,000")
    private BigDecimal loanAmount;

    @NotNull(message = "Loan tenure is required")
    @Min(value = 6, message = "Minimum tenure is 6 months")
    @Max(value = 360, message = "Maximum tenure is 360 months")
    private Integer loanTenureMonths;

    @NotBlank(message = "Purpose is required")
    @Size(max = 255, message = "Purpose cannot exceed 255 characters")
    private String purpose;

    @NotBlank(message = "Employment type is required")
    @Pattern(regexp = "^(SALARIED|SELF_EMPLOYED|BUSINESS|RETIRED|STUDENT)$", 
             message = "Employment type must be one of: SALARIED, SELF_EMPLOYED, BUSINESS, RETIRED, STUDENT")
    private String employmentType;

    @NotNull(message = "Monthly income is required")
    @DecimalMin(value = "0.00", message = "Monthly income cannot be negative")
    private BigDecimal monthlyIncome;
}
