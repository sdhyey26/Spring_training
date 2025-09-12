package com.tss.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "card_applications")
public class CardApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore	
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_number", referencedColumnName = "account_number", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Account account;
    
    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    @Column(name = "account_number", insertable = false, updatable = false)
    private String accountNumber;

    @Column(name = "card_type", length = 50, nullable = false)
    private String cardType;

    @Column(name = "status", length = 20, nullable = false)
    private String status; 																																												

    @Column(name = "applied_at", nullable = false)
    private Instant appliedAt;

    @Column(name = "approved_at")
    private Instant approvedAt;
}