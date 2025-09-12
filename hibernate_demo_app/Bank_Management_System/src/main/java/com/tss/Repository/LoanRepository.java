package com.tss.Repository;

import com.tss.Entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByUserId(Long userId);
    
    List<Loan> findByAccountNumber(String accountNumber);
    
    List<Loan> findByStatus(String status);
    
    List<Loan> findByUserIdAndStatus(Long userId, String status);
    
    Page<Loan> findByStatus(String status, Pageable pageable);
    
    @Query("SELECT l FROM Loan l WHERE l.userId = :userId ORDER BY l.appliedAt DESC")
    List<Loan> findByUserIdOrderByAppliedAtDesc(@Param("userId") Long userId);
    
    @Query("SELECT l FROM Loan l WHERE l.status = :status ORDER BY l.appliedAt ASC")
    List<Loan> findByStatusOrderByAppliedAtAsc(@Param("status") String status);
    
    @Query("SELECT l FROM Loan l WHERE l.loanType = :loanType AND l.status = :status")
    List<Loan> findByLoanTypeAndStatus(@Param("loanType") String loanType, @Param("status") String status);
    
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.status = :status")
    Long countByStatus(@Param("status") String status);
    
    @Query("SELECT l FROM Loan l WHERE l.userId = :userId AND l.status IN ('ACTIVE', 'DEFAULTED')")
    List<Loan> findActiveLoansByUserId(@Param("userId") Long userId);
    
    @Query("SELECT l FROM Loan l WHERE l.nextPaymentDate <= :currentDate AND l.status = 'ACTIVE'")
    List<Loan> findLoansDueForPayment(@Param("currentDate") java.time.LocalDate currentDate);
    
    @Query("SELECT SUM(l.remainingAmount) FROM Loan l WHERE l.userId = :userId AND l.status = 'ACTIVE'")
    Optional<java.math.BigDecimal> getTotalOutstandingAmountByUserId(@Param("userId") Long userId);
}
