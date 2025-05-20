package com.bank.loan.controller;

import com.bank.loan.dto.PayLoanRequest;
import com.bank.loan.dto.PayLoanResponse;
import com.bank.loan.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @PostMapping
    public PayLoanResponse payLoan(@RequestBody PayLoanRequest request) {
        return paymentService.payLoan(request);
    }
}
