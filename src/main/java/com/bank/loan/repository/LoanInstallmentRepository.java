package com.bank.loan.repository;

import com.bank.loan.model.LoanInstallment;
import com.bank.loan.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {
    List<LoanInstallment> findByLoanOrderByDueDateAsc(Loan loan);
    List<LoanInstallment> findByLoanAndIsPaidFalseAndDueDateLessThanEqualOrderByDueDateAsc(Loan loan, LocalDate maxPayableDate);
}
