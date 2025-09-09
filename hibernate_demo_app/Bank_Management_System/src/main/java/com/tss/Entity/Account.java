package com.tss.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "accounts")
public class Account {

    @Id
    @Column(name = "account_number", length = 32)
    private String accountNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "mobile", length = 15, nullable = false)
    private String mobile;

    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @Column(name = "aadhar", length = 12, nullable = false)
    private String aadhar;

    @Column(name = "account_type", length = 20, nullable = false)
    private String accountType; 

    @Column(name = "balance", precision = 15, scale = 2, nullable = false)
    private BigDecimal balance;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}


