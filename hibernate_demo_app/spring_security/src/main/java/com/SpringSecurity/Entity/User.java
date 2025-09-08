package com.SpringSecurity.Entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int userId;

    @Column
    private String username;

    @Column
    private String password;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {
            CascadeType.REFRESH,
            CascadeType.MERGE,
            CascadeType.DETACH
    })
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
}
