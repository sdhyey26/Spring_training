package com.tss.Repository;

import com.tss.Entity.Account;
import com.tss.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
    List<Account> findByUser(User user);
    Optional<Account> findByAccountNumber(String accountNumber);
}


