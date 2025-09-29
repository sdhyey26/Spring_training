package com.tss.Dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanPaymentRequestDto {

    private Long loanId;

    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "0.01", message = "Payment amount must be greater than 0")
    private BigDecimal paymentAmount;

    @NotBlank(message = "Payment method is required")
    @Pattern(regexp = "^(EMI|PARTIAL|FULL)$", 
             message = "Payment method must be one of: EMI, PARTIAL, FULL")
    private String paymentMethod;

    @Size(max = 255, message = "Notes cannot exceed 255 characters")
    private String notes;

    private LocalDate paymentDate;
}
