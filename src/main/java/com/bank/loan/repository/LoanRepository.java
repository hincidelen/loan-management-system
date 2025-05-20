package com.bank.loan.repository;

import com.bank.loan.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByCustomerIdAndNumberOfInstallmentsAndIsPaid(
            Long customerId,
            Integer numberOfInstallments,
            Boolean isPaid
    );
    List<Loan> findByCustomerIdAndNumberOfInstallments(Long customerId, Integer numberOfInstallments);
    List<Loan> findByCustomerIdAndIsPaid(Long customerId, Boolean isPaid);
    List<Loan> findByCustomerId(Long customerId);
}
