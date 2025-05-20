package com.bank.loan.service;

import com.bank.loan.dto.PayLoanRequest;
import com.bank.loan.dto.PayLoanResponse;
import com.bank.loan.model.*;
import com.bank.loan.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class PaymentService {

    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository installmentRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public PaymentService(LoanRepository loanRepository,
                          LoanInstallmentRepository installmentRepository,
                          CustomerRepository customerRepository) {
        this.loanRepository = loanRepository;
        this.installmentRepository = installmentRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public PayLoanResponse payLoan(PayLoanRequest request) {
        Loan loan = loanRepository.findById(request.getLoanId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found"));

        Customer customer = loan.getCustomer();
        double remainingPayment = request.getPaymentAmount();
        int paidInstallmentCount = 0;
        double totalPaid = 0.0;

        // Limit: 3 calendar months from today
        LocalDate maxDue = getMaxDue();

        List<LoanInstallment> eligibleInstallments = installmentRepository
                .findByLoanAndIsPaidFalseAndDueDateLessThanEqualOrderByDueDateAsc(loan, maxDue);

        for (LoanInstallment inst : eligibleInstallments) {
            if (inst.getIsPaid()) continue;

            double finalAmount = inst.getAmount();

            finalAmount = getFinalAmountOfEarlyAndLatePaymentAdjustment(inst, finalAmount);

            // Can we fully pay this installment?
            if (remainingPayment >= finalAmount) {
                inst.setPaidAmount(finalAmount);
                inst.setIsPaid(true);
                inst.setPaymentDate(LocalDate.now());
                installmentRepository.save(inst);

                remainingPayment -= finalAmount;
                totalPaid += finalAmount;
                paidInstallmentCount++;
            } else {
                break;
            }
        }

        boolean loanFullyPaid = installmentRepository.findByLoanOrderByDueDateAsc(loan)
                .stream().allMatch(LoanInstallment::getIsPaid);
        loan.setIsPaid(loanFullyPaid);
        loanRepository.save(loan);

        updateUserCreditLimit(customer, totalPaid);
        customerRepository.save(customer);

        PayLoanResponse response = new PayLoanResponse();
        response.setNumberOfInstallmentsPaid(paidInstallmentCount);
        response.setTotalAmountPaid(totalPaid);
        response.setLoanFullyPaid(loanFullyPaid);
        return response;
    }

    private static void updateUserCreditLimit(Customer customer, double totalPaid) {
        customer.setUsedCreditLimit(customer.getUsedCreditLimit() - totalPaid);
    }

    private static LocalDate getMaxDue() {
        return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).plusMonths(3);
    }

    private static double getFinalAmountOfEarlyAndLatePaymentAdjustment(LoanInstallment inst, double finalAmount) {
        long daysDiff = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), inst.getDueDate());
        if (daysDiff > 0) {
            finalAmount -= getDiscountAmount(inst, daysDiff);
        } else if (daysDiff < 0) {
            finalAmount += getPenaltyAmount(inst, daysDiff);
        }
        return finalAmount;
    }

    private static double getPenaltyAmount(LoanInstallment inst, long daysDiff) {
        return inst.getAmount() * 0.001 * Math.abs(daysDiff);
    }

    private static double getDiscountAmount(LoanInstallment inst, long daysDiff) {
        return inst.getAmount() * 0.001 * daysDiff;
    }
}
