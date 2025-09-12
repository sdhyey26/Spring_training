package com.tss.Repository;

import com.tss.Entity.LoanPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanPaymentRepository extends JpaRepository<LoanPayment, Long> {

    List<LoanPayment> findByLoanId(Long loanId);
    
    List<LoanPayment> findByLoanIdOrderByPaymentDateDesc(Long loanId);
    
    @Query("SELECT lp FROM LoanPayment lp WHERE lp.loanId = :loanId AND lp.status = :status")
    List<LoanPayment> findByLoanIdAndStatus(@Param("loanId") Long loanId, @Param("status") String status);
    
    @Query("SELECT SUM(lp.paymentAmount) FROM LoanPayment lp WHERE lp.loanId = :loanId AND lp.status = 'COMPLETED'")
    java.math.BigDecimal getTotalPaidAmountByLoanId(@Param("loanId") Long loanId);
    
    @Query("SELECT lp FROM LoanPayment lp WHERE lp.loanId = :loanId AND lp.paymentDate BETWEEN :startDate AND :endDate")
    List<LoanPayment> findByLoanIdAndPaymentDateBetween(@Param("loanId") Long loanId, 
                                                       @Param("startDate") LocalDate startDate, 
                                                       @Param("endDate") LocalDate endDate);
}
