package com.bank.loan.controller;

import com.bank.loan.dto.PayLoanRequest;
import com.bank.loan.dto.PayLoanResponse;
import com.bank.loan.model.Loan;
import com.bank.loan.security.SecurityUtil;
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

    @Autowired
    public PaymentController(PaymentService paymentService, LoanService loanService) {
        this.paymentService = paymentService;
        this.loanService = loanService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @PostMapping
    public PayLoanResponse payLoan(@RequestBody PayLoanRequest request) {
        Loan loan = loanService.getLoanById(request.getLoanId());
        SecurityUtil.checkCustomerAccessControl(loan.getCustomer());
        return paymentService.payLoan(request, loan);
    }
}
