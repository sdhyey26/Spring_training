package com.tss.Dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApprovalRequestDto {

    @NotNull(message = "Loan ID is required")
    private Long loanId;

    @NotBlank(message = "Action is required")
    @Pattern(regexp = "^(APPROVE|REJECT)$", message = "Action must be APPROVE or REJECT")
    private String action;

    @DecimalMin(value = "0.00", message = "Interest rate cannot be negative")
    @DecimalMax(value = "50.00", message = "Interest rate cannot exceed 50%")
    private BigDecimal interestRate;

    @Size(max = 500, message = "Admin notes cannot exceed 500 characters")
    private String adminNotes;

    @Size(max = 500, message = "Rejection reason cannot exceed 500 characters")
    private String rejectionReason;
}
