package com.bank.loan.repository;

import com.bank.loan.model.Loan;
import com.bank.loan.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByCustomer(Customer customer);
}
