package com.bank.loan.service;

import com.bank.loan.dto.PayLoanRequest;
import com.bank.loan.dto.PayLoanResponse;
import com.bank.loan.model.*;
import com.bank.loan.repository.CustomerRepository;
import com.bank.loan.repository.LoanInstallmentRepository;
import com.bank.loan.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    private LoanRepository loanRepository;
    private LoanInstallmentRepository installmentRepository;
    private CustomerRepository customerRepository;
    private PaymentService paymentService;

    private Loan loan;
    private Customer customer;

    @BeforeEach
    void setUp() {
        loanRepository = mock(LoanRepository.class);
        installmentRepository = mock(LoanInstallmentRepository.class);
        customerRepository = mock(CustomerRepository.class);

        paymentService = new PaymentService(loanRepository, installmentRepository, customerRepository, 3);

        customer = new Customer();
        customer.setId(1L);
        customer.setUsedCreditLimit(5000.0);
        customer.setCreditLimit(10000.0);

        loan = new Loan();
        loan.setId(1L);
        loan.setCustomer(customer);
        loan.setIsPaid(false);
    }

    @Test
    void shouldPayTwoInstallmentsWithExactAmount() {
        double installmentAmount = 1000.0;

        LoanInstallment inst1 = createInstallment(installmentAmount, LocalDate.now());
        LoanInstallment inst2 = createInstallment(installmentAmount, LocalDate.now().plusDays(31));
        LoanInstallment inst3 = createInstallment(installmentAmount, LocalDate.now().plusMonths(4)); // beyond limit

        when(installmentRepository.findByLoanAndIsPaidFalseAndDueDateLessThanEqualOrderByDueDateAsc(
                any(), any())).thenReturn(List.of(inst1, inst2));

        when(installmentRepository.findByLoanOrderByDueDateAsc(loan)).thenReturn(List.of(inst1, inst2, inst3));

        PayLoanRequest request = new PayLoanRequest();
        request.setLoanId(loan.getId());
        request.setPaymentAmount(2000.0);

        PayLoanResponse response = paymentService.payLoan(request, loan);

        assertEquals(2, response.getNumberOfInstallmentsPaid());
        Double totalAmountPaid = 2000.0 - 1000.0 * 0.001 * 31; // since one of them will be early payment
        assertEquals(totalAmountPaid, response.getTotalAmountPaid());
        assertFalse(response.getLoanFullyPaid());

        verify(installmentRepository, times(2)).save(any());
        verify(customerRepository).save(customer);
        verify(loanRepository).save(loan);
    }

    @Test
    void shouldApplyDiscountForEarlyPayment() {
        LoanInstallment earlyInst = createInstallment(1000.0, LocalDate.now().plusDays(10));
        when(installmentRepository.findByLoanAndIsPaidFalseAndDueDateLessThanEqualOrderByDueDateAsc(any(), any()))
                .thenReturn(List.of(earlyInst));
        when(installmentRepository.findByLoanOrderByDueDateAsc(any())).thenReturn(List.of(earlyInst));

        PayLoanRequest request = new PayLoanRequest();
        request.setPaymentAmount(990.0); // enough if discount applied

        PayLoanResponse response = paymentService.payLoan(request, loan);

        assertEquals(1, response.getNumberOfInstallmentsPaid());
        assertTrue(earlyInst.getIsPaid());
        assertTrue(earlyInst.getPaidAmount() < 1000.0);
    }

    @Test
    void shouldApplyPenaltyForLatePayment() {
        LoanInstallment lateInst = createInstallment(1000.0, LocalDate.now().minusDays(10));
        when(installmentRepository.findByLoanAndIsPaidFalseAndDueDateLessThanEqualOrderByDueDateAsc(any(), any()))
                .thenReturn(List.of(lateInst));
        when(installmentRepository.findByLoanOrderByDueDateAsc(any())).thenReturn(List.of(lateInst));

        PayLoanRequest request = new PayLoanRequest();
        request.setPaymentAmount(1010.0); // enough if penalty is ~10

        PayLoanResponse response = paymentService.payLoan(request, loan);

        assertEquals(1, response.getNumberOfInstallmentsPaid());
        assertTrue(lateInst.getIsPaid());
        assertTrue(lateInst.getPaidAmount() > 1000.0);
    }

    @Test
    void shouldSkipIfInsufficientForFullInstallment() {
        LoanInstallment inst = createInstallment(1000.0, LocalDate.now());
        when(installmentRepository.findByLoanAndIsPaidFalseAndDueDateLessThanEqualOrderByDueDateAsc(any(), any()))
                .thenReturn(List.of(inst));
        when(installmentRepository.findByLoanOrderByDueDateAsc(any())).thenReturn(List.of(inst));

        PayLoanRequest request = new PayLoanRequest();
        request.setPaymentAmount(500.0); // not enough

        PayLoanResponse response = paymentService.payLoan(request, loan);

        assertEquals(0, response.getNumberOfInstallmentsPaid());
        assertFalse(inst.getIsPaid());
        assertEquals(0.0, inst.getPaidAmount());
    }

    // Helper method
    private LoanInstallment createInstallment(double amount, LocalDate dueDate) {
        LoanInstallment inst = new LoanInstallment();
        inst.setAmount(amount);
        inst.setDueDate(dueDate);
        inst.setIsPaid(false);
        inst.setLoan(loan);
        inst.setPaidAmount(0.0);
        return inst;
    }
}
