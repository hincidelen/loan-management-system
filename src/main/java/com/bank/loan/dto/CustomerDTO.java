package com.bank.loan.dto;

public class CustomerDTO {

    private Long id;
    private String name;
    private String surname;
    private Double creditLimit;
    private Double usedCreditLimit;

    public CustomerDTO() {}
    public CustomerDTO(Long id, String name, String surname, Double creditLimit, Double usedCreditLimit) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.creditLimit = creditLimit;
        this.usedCreditLimit = usedCreditLimit;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public Double getCreditLimit() { return creditLimit; }
    public void setCreditLimit(Double creditLimit) { this.creditLimit = creditLimit; }

    public Double getUsedCreditLimit() { return usedCreditLimit; }
    public void setUsedCreditLimit(Double usedCreditLimit) { this.usedCreditLimit = usedCreditLimit; }
}
