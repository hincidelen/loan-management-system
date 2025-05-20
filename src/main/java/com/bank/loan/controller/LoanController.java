package com.bank.loan.controller;

import com.bank.loan.dto.CreateLoanRequest;
import com.bank.loan.dto.LoanDTO;
import com.bank.loan.dto.LoanInstallmentDTO;
import com.bank.loan.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    @Autowired
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    public LoanDTO createLoan(@RequestBody CreateLoanRequest request) {
        return loanService.createLoan(request);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @GetMapping("/customer/{customerId}")
    public List<LoanDTO> getLoansByCustomer(@PathVariable Long customerId) {
        return loanService.getLoansByCustomerId(customerId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @GetMapping("/{loanId}/installments")
    public List<LoanInstallmentDTO> getInstallments(@PathVariable Long loanId) {
        return loanService.getInstallmentsForLoan(loanId);
    }
}
