package com.bank.loan.dto;

import java.io.Serial;

public class CreateCustomerDTO extends CustomerDTO {

    private String uuid;

    public CreateCustomerDTO(CustomerDTO customerDTO, String uuid) {
        super(
                customerDTO.getId(),
                customerDTO.getName(),
                customerDTO.getSurname(),
                customerDTO.getCreditLimit(),
                customerDTO.getUsedCreditLimit());
        this.uuid = uuid;
    }

    public CreateCustomerDTO(Long id, String name, String surname, Double creditLimit, Double usedCreditLimit) {
        super(id, name, surname, creditLimit, usedCreditLimit);
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
