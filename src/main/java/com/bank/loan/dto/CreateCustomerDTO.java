package com.bank.loan.dto;

public class CreateCustomerDTO extends CustomerDTO {

    private final String uuid;

    public CreateCustomerDTO(CustomerDTO customerDTO, String uuid) {
        super(
                customerDTO.getId(),
                customerDTO.getName(),
                customerDTO.getSurname(),
                customerDTO.getCreditLimit(),
                customerDTO.getUsedCreditLimit());
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

}
