package com.tss.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_account", length = 32, nullable = false)
    private String fromAccount;

    @Column(name = "to_account", length = 32, nullable = false)
    private String toAccount;

    @Column(name = "amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "type", length = 10, nullable = false)
    private String type; // DEBIT | CREDIT

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;
}


