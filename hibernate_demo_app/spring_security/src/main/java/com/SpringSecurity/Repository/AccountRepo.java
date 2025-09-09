package com.SpringSecurity.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SpringSecurity.Entity.Account;

public interface AccountRepo extends JpaRepository<Account, Integer> {
}