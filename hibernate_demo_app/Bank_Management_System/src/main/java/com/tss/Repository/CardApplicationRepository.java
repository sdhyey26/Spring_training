package com.tss.Repository;

import com.tss.Entity.CardApplication;
import com.tss.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardApplicationRepository extends JpaRepository<CardApplication, Long> {
    List<CardApplication> findByUser(User user);
    List<CardApplication> findByStatus(String status);
}


