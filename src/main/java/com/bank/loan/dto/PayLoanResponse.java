package com.bank.loan.dto;

public class PayLoanResponse {
    private int numberOfInstallmentsPaid;
    private Double totalAmountPaid;
    private Boolean loanFullyPaid;

    public int getNumberOfInstallmentsPaid() {
        return numberOfInstallmentsPaid;
    }

    public void setNumberOfInstallmentsPaid(int numberOfInstallmentsPaid) {
        this.numberOfInstallmentsPaid = numberOfInstallmentsPaid;
    }

    public Double getTotalAmountPaid() {
        return totalAmountPaid;
    }

    public void setTotalAmountPaid(Double totalAmountPaid) {
        this.totalAmountPaid = totalAmountPaid;
    }

    public Boolean getLoanFullyPaid() {
        return loanFullyPaid;
    }

    public void setLoanFullyPaid(Boolean loanFullyPaid) {
        this.loanFullyPaid = loanFullyPaid;
    }
}
