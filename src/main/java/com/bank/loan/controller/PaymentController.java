package com.bank.loan.controller;

import com.bank.loan.dto.PayLoanRequest;
import com.bank.loan.dto.PayLoanResponse;
import com.bank.loan.model.Loan;
import com.bank.loan.security.SecurityConfig;
import com.bank.loan.service.LoanService;
import com.bank.loan.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final LoanService loanService;
    private final SecurityConfig securityConfig;

    @Autowired
    public PaymentController(PaymentService paymentService, LoanService loanService, SecurityConfig securityConfig) {
        this.paymentService = paymentService;
        this.loanService = loanService;
        this.securityConfig = securityConfig;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @PostMapping
    public PayLoanResponse payLoan(@RequestBody PayLoanRequest request) {
        Loan loan = loanService.getLoanById(request.getLoanId());
        securityConfig.checkCustomerAccessControl(loan.getCustomer());
        return paymentService.payLoan(request, loan);
    }
}
