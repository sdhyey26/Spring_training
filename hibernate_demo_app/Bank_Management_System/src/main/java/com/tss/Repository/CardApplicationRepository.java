package com.tss.Repository;

import com.tss.Entity.CardApplication;
import com.tss.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardApplicationRepository extends JpaRepository<CardApplication, Long> {
    List<CardApplication> findByUser(User user);
    List<CardApplication> findByStatus(String status);
    long countByStatus(String status);
    
    @Query("SELECT ca FROM CardApplication ca WHERE ca.account.accountNumber = :accountNumber")
    List<CardApplication> findByAccountNumber(@Param("accountNumber") String accountNumber);
    
    @Modifying
    @Query("DELETE FROM CardApplication ca WHERE ca.account.accountNumber = :accountNumber")
    void deleteByAccountNumber(@Param("accountNumber") String accountNumber);
    
    @Modifying
    @Query(value = "DELETE FROM card_applications WHERE account_number = :accountNumber", nativeQuery = true)
    void deleteByAccountNumberNative(@Param("accountNumber") String accountNumber);
}


