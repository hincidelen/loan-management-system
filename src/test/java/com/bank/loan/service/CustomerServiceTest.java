package com.bank.loan.service;

import com.bank.loan.dto.CreateCustomerDTO;
import com.bank.loan.dto.CustomerDTO;
import com.bank.loan.model.Customer;
import com.bank.loan.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    private CustomerRepository customerRepository;
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        customerService = new CustomerService(customerRepository);
    }

    @Test
    void createCustomer_shouldReturnCreateCustomerDTO() {
        CustomerDTO dto = new CustomerDTO();
        dto.setName("Ali");
        dto.setSurname("Demir");
        dto.setCreditLimit(10000.0);

        Customer savedCustomer = new Customer();
        savedCustomer.setId(1L);
        savedCustomer.setName("Ali");
        savedCustomer.setSurname("Demir");
        savedCustomer.setCreditLimit(10000.0);
        savedCustomer.setUsedCreditLimit(0.0);
        savedCustomer.setUuid("abc-123-uuid");

        when(customerRepository.save(any())).thenReturn(savedCustomer);

        CreateCustomerDTO result = customerService.createCustomer(dto);

        assertNotNull(result);
        assertEquals("Ali", result.getName());
        assertEquals("abc-123-uuid", result.getUuid());
    }

    @Test
    void getAllCustomers_shouldReturnMappedList() {
        Customer customer1 = new Customer();
        customer1.setId(1L);
        customer1.setName("Ali");
        customer1.setSurname("Demir");
        customer1.setCreditLimit(10000.0);
        customer1.setUsedCreditLimit(1000.0);

        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setName("Bob");
        customer2.setSurname("Jones");
        customer2.setCreditLimit(8000.0);
        customer2.setUsedCreditLimit(0.0);

        when(customerRepository.findAll()).thenReturn(List.of(customer1, customer2));

        List<CustomerDTO> result = customerService.getAllCustomers();

        assertEquals(2, result.size());
        assertEquals("Ali", result.get(0).getName());
        assertEquals("Bob", result.get(1).getName());
    }

    @Test
    void getCustomerById_shouldReturnCustomerDTO() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Charlie");
        customer.setSurname("Brown");
        customer.setCreditLimit(15000.0);
        customer.setUsedCreditLimit(1000.0);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerDTO result = customerService.getCustomerById(1L);

        assertNotNull(result);
        assertEquals("Charlie", result.getName());
    }

    @Test
    void getCustomerEntityById_shouldThrowIfNotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> customerService.getCustomerEntityById(99L));

        assertEquals("Customer not found", ex.getReason());
        assertEquals(404, ex.getStatusCode().value());
    }
}
