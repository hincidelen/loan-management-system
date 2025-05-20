package com.bank.loan.service;

import com.bank.loan.dto.CreateLoanRequest;
import com.bank.loan.dto.CustomerDTO;
import com.bank.loan.dto.LoanDTO;
import com.bank.loan.dto.LoanInstallmentDTO;
import com.bank.loan.model.*;
import com.bank.loan.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final LoanInstallmentRepository installmentRepository;
    private final CustomerService customerService;

    @Autowired
    public LoanService(LoanRepository loanRepository, CustomerRepository customerRepository,
                       LoanInstallmentRepository installmentRepository, CustomerService customerService) {
        this.loanRepository = loanRepository;
        this.customerRepository = customerRepository;
        this.installmentRepository = installmentRepository;
        this.customerService = customerService;
    }

    @Transactional
    public LoanDTO createLoan(CreateLoanRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));


        double totalWithInterest = request.getLoanAmount() * (1 + request.getInterestRate());
        validateCreateLoanRequest(request, customer, totalWithInterest);

        Loan loan = createLoan(request, customer);
        loan = loanRepository.save(loan);

        List<LoanInstallment> installments = createInstallments(request, totalWithInterest, loan);
        installmentRepository.saveAll(installments);

        customer.setUsedCreditLimit(customer.getUsedCreditLimit() + totalWithInterest);
        customerRepository.save(customer);
        return toLoanDTO(loan);
    }

    private static void validateCreateLoanRequest(CreateLoanRequest request, Customer customer, double totalWithInterest) {
        if (!List.of(6, 9, 12, 24).contains(request.getNumberOfInstallments())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid installment count");
        }

        if (request.getInterestRate() < 0.1 || request.getInterestRate() > 0.5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid interest rate");
        }

        if (customer.getCreditLimit() - customer.getUsedCreditLimit() < totalWithInterest) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough credit limit");
        }
    }

    private static Loan createLoan(CreateLoanRequest request, Customer customer) {
        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setLoanAmount(request.getLoanAmount());
        loan.setInterestRate(request.getInterestRate());
        loan.setNumberOfInstallments(request.getNumberOfInstallments());
        loan.setCreateDate(LocalDate.now());
        loan.setIsPaid(false);
        return loan;
    }

    private static List<LoanInstallment> createInstallments(CreateLoanRequest request, double totalWithInterest, Loan loan) {
        List<LoanInstallment> installments = new ArrayList<>();
        double installmentAmount = totalWithInterest / request.getNumberOfInstallments();
        LocalDate dueDate = LocalDate.now().plusMonths(1).with(TemporalAdjusters.firstDayOfMonth());

        for (int i = 0; i < request.getNumberOfInstallments(); i++) {
            LoanInstallment inst = new LoanInstallment();
            inst.setLoan(loan);
            inst.setAmount(installmentAmount);
            inst.setPaidAmount(0.0);
            inst.setDueDate(dueDate.plusMonths(i));
            inst.setIsPaid(false);
            installments.add(inst);
        }
        return installments;
    }

    public List<LoanDTO> getLoansByCustomerId(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        List<Loan> customerLoans = loanRepository.findByCustomer(customer);
        return customerLoans.stream().map(this::toLoanDTO).collect(Collectors.toList());
    }

    public List<LoanInstallmentDTO> getInstallmentsForLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found"));
        List<LoanInstallment> loanInstallments = installmentRepository.findByLoanOrderByDueDateAsc(loan);
        return loanInstallments.stream().map(this::toLoanInstallmentDTO).collect(Collectors.toList());
    }

    public LoanDTO toLoanDTO(Loan loan) {
        LoanDTO loanDTO = new LoanDTO();
        loanDTO.setId(loan.getId());
        loanDTO.setLoanAmount(loan.getLoanAmount());
        loanDTO.setNumberOfInstallments(loan.getNumberOfInstallments());
        loanDTO.setInterestRate(loan.getInterestRate());
        loanDTO.setCreateDate(loan.getCreateDate());
        loanDTO.setPaid(loan.getIsPaid());
        loanDTO.setCustomer(customerService.toDTO(loan.getCustomer()));
        return loanDTO;
    }

    public LoanInstallmentDTO toLoanInstallmentDTO(LoanInstallment loanInstallment) {
        LoanInstallmentDTO loanInstallmentDTO = new LoanInstallmentDTO();
        loanInstallmentDTO.setId(loanInstallment.getId());
        loanInstallmentDTO.setAmount(loanInstallment.getAmount());
        loanInstallmentDTO.setPaidAmount(loanInstallment.getPaidAmount());
        loanInstallmentDTO.setDueDate(loanInstallment.getDueDate());
        loanInstallmentDTO.setPaymentDate(loanInstallment.getPaymentDate());
        loanInstallmentDTO.setIsPaid(loanInstallment.getIsPaid());
        loanInstallmentDTO.setLoan(toLoanDTO(loanInstallment.getLoan()));
        return loanInstallmentDTO;
    }
}
