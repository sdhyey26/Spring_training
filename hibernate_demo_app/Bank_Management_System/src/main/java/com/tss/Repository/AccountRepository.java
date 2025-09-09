package com.tss.Repository;

import com.tss.Entity.Account;
import com.tss.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
    List<Account> findByUser(User user);
    Optional<Account> findByAccountNumber(String accountNumber);

    @Query("select coalesce(sum(a.balance),0) from Account a")
    BigDecimal sumAllBalances();
}


