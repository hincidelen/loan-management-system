package com.bank.loan.controller;

import com.bank.loan.dto.CreateCustomerDTO;
import com.bank.loan.dto.CustomerDTO;
import com.bank.loan.security.SecurityConfig;
import com.bank.loan.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final SecurityConfig securityConfig;

    @Autowired
    public CustomerController(CustomerService customerService, SecurityConfig securityConfig) {
        this.customerService = customerService;
        this.securityConfig = securityConfig;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public CreateCustomerDTO createCustomer(@RequestBody CustomerDTO customerDTO) {
        return customerService.createCustomer(customerDTO);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @GetMapping("/{id}")
    public CustomerDTO getCustomerById(@PathVariable Long id) {
        securityConfig.checkCustomerAccessControl(id);
        return customerService.getCustomerById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<CustomerDTO> getAllCustomers() {
        return customerService.getAllCustomers();
    }

}
