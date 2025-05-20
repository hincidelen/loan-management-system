package com.bank.loan.security;

import com.bank.loan.model.Customer;
import com.bank.loan.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    private CustomerService customerService;
    private CustomUserDetailsService userDetailsService;

    private final String ADMIN_USERNAME = "admin";
    private final String ADMIN_PASSWORD = "admin123";

    @BeforeEach
    void setUp() {
        customerService = mock(CustomerService.class);
        userDetailsService = new CustomUserDetailsService(customerService, ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    @Test
    void shouldLoadAdminUser() {
        UserDetails user = userDetailsService.loadUserByUsername("admin");

        assertEquals("admin", user.getUsername());
        assertEquals("{noop}admin123", user.getPassword());
        assertTrue(user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void shouldLoadCustomerUserById() {
        Customer mockCustomer = new Customer();
        mockCustomer.setId(3L);
        mockCustomer.setUuid("uuid-333");

        when(customerService.getCustomerEntityById(3L)).thenReturn(mockCustomer);

        UserDetails user = userDetailsService.loadUserByUsername("customer3");

        assertEquals("customer3", user.getUsername());
        assertEquals("{noop}uuid-333", user.getPassword());
        assertTrue(user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER")));
    }

    @Test
    void shouldThrowWhenCustomerIdInvalidFormat() {
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("customerXYZ");
        });
    }

    @Test
    void shouldThrowWhenCustomerNotFound() {
        when(customerService.getCustomerEntityById(999L)).thenThrow(new RuntimeException("Not found"));

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("customer999");
        });
    }

    @Test
    void shouldExtractCustomerIdCorrectly() {
        Long id = userDetailsService.retrieveCustomerIdFromUsername("customer123");
        assertEquals(123L, id);
    }
}
