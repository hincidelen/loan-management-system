package com.bank.loan.service;

import com.bank.loan.dto.CreateLoanRequest;
import com.bank.loan.model.Customer;
import com.bank.loan.model.Loan;
import com.bank.loan.repository.CustomerRepository;
import com.bank.loan.repository.LoanInstallmentRepository;
import com.bank.loan.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoanServiceTest {

    private LoanRepository loanRepository;
    private CustomerRepository customerRepository;
    private LoanInstallmentRepository installmentRepository;
    private LoanService loanService;

    @BeforeEach
    void setUp() {
        loanRepository = mock(LoanRepository.class);
        customerRepository = mock(CustomerRepository.class);
        installmentRepository = mock(LoanInstallmentRepository.class);
        CustomerService customerService = mock(CustomerService.class);
        loanService = new LoanService(loanRepository, customerRepository, installmentRepository, customerService,
                0.1, 0.5, List.of(6, 9, 12, 24));
    }

    @Test
    void shouldReturnFilteredLoans_whenFiltersProvided() {
        Long customerId = 1L;
        int numInstallments = 12;
        boolean isPaid = false;

        Loan mockLoan = new Loan();
        when(loanRepository.findByCustomerIdAndNumberOfInstallmentsAndIsPaid(customerId, numInstallments, isPaid))
                .thenReturn(List.of(mockLoan));

        List<Loan> result = loanService.getLoansByCustomerWithFilters(customerId, numInstallments, isPaid);

        assertEquals(1, result.size());
        verify(loanRepository).findByCustomerIdAndNumberOfInstallmentsAndIsPaid(customerId, numInstallments, isPaid);
    }

    @Test
    void shouldReturnAllLoans_whenNoFiltersProvided() {
        Long customerId = 1L;

        when(loanRepository.findByCustomerId(customerId)).thenReturn(List.of(new Loan(), new Loan()));

        List<Loan> result = loanService.getLoansByCustomerWithFilters(customerId, null, null);

        assertEquals(2, result.size());
        verify(loanRepository).findByCustomerId(customerId);
    }

    @Test
    void shouldCreateLoanSuccessfully() {
        CreateLoanRequest request = new CreateLoanRequest();
        request.setCustomerId(1L);
        request.setLoanAmount(10000.0);
        request.setInterestRate(0.2);
        request.setNumberOfInstallments(12);

        Customer mockCustomer = new Customer();
        mockCustomer.setId(1L);
        mockCustomer.setCreditLimit(20000.0);
        mockCustomer.setUsedCreditLimit(0.0);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));
        when(loanRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(installmentRepository.saveAll(any())).thenReturn(null);

        loanService.createLoan(request);

        verify(customerRepository).save(any());
        verify(installmentRepository).saveAll(any());
        verify(loanRepository).save(any());
    }

    @Test
    void shouldThrowExceptionForInvalidInstallments() {
        CreateLoanRequest request = new CreateLoanRequest();
        request.setCustomerId(1L);
        request.setLoanAmount(1000.0);
        request.setInterestRate(0.2);
        request.setNumberOfInstallments(5); // Invalid

        Customer mockCustomer = new Customer();
        mockCustomer.setCreditLimit(20000.0);
        mockCustomer.setUsedCreditLimit(0.0);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));

        var ex = assertThrows(RuntimeException.class, () -> loanService.createLoan(request));
        assertTrue(ex.getMessage().contains("Invalid installment count"));
    }

    @Test
    void shouldThrowExceptionWhenNotEnoughCredit() {
        CreateLoanRequest request = new CreateLoanRequest();
        request.setCustomerId(1L);
        request.setLoanAmount(10000.0);
        request.setInterestRate(0.5);
        request.setNumberOfInstallments(12);

        Customer customer = new Customer();
        customer.setCreditLimit(10000.0);
        customer.setUsedCreditLimit(9500.0); // Only 500 left

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        var ex = assertThrows(RuntimeException.class, () -> loanService.createLoan(request));
        assertTrue(ex.getMessage().contains("Not enough credit limit"));
    }
}
