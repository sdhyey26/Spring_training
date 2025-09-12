package com.tss.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "loan_payments")
public class LoanPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Loan loan;

    @Column(name = "loan_id", insertable = false, updatable = false)
    private Long loanId;

    @Column(name = "payment_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal paymentAmount;

    @Column(name = "principal_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal principalAmount;

    @Column(name = "interest_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal interestAmount;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Column(name = "payment_timestamp", nullable = false)
    private Instant paymentTimestamp;

    @Column(name = "payment_method", length = 50, nullable = false)
    private String paymentMethod; // EMI, PARTIAL, FULL

    @Column(name = "status", length = 20, nullable = false)
    @Builder.Default
    private String status = "COMPLETED"; // COMPLETED, PENDING, FAILED

    @Column(name = "transaction_reference", length = 100)
    private String transactionReference;

    @Column(name = "notes", length = 255)
    private String notes;

    @Column(name = "remaining_balance", precision = 15, scale = 2, nullable = false)
    private BigDecimal remainingBalance;
}
