package com.bank.loan.service;

import com.bank.loan.dto.CreateLoanRequest;
import com.bank.loan.dto.LoanDTO;
import com.bank.loan.model.Customer;
import com.bank.loan.model.Loan;
import com.bank.loan.repository.CustomerRepository;
import com.bank.loan.repository.LoanInstallmentRepository;
import com.bank.loan.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LoanInstallmentRepository installmentRepository;

    @InjectMocks
    private LoanService loanService;

    @Test
    public void testCreateLoan_shouldCreateLoanWhenValid() {
        CreateLoanRequest req = new CreateLoanRequest();
        req.setCustomerId(1L);
        req.setLoanAmount(1000.0);
        req.setInterestRate(0.2);
        req.setNumberOfInstallments(6);

        Customer mockCustomer = new Customer();
        mockCustomer.setId(1L);
        mockCustomer.setCreditLimit(5000.0);
        mockCustomer.setUsedCreditLimit(1000.0);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));
        when(loanRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        LoanDTO loan = loanService.createLoan(req);

        assertEquals(1000.0, loan.getLoanAmount());
        verify(installmentRepository, times(1)).saveAll(any());
        verify(customerRepository, times(1)).save(any());
    }
}