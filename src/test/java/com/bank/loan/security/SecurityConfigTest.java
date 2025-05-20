package com.bank.loan.security;

import com.bank.loan.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SecurityConfigTest {

    private SecurityConfig securityConfig;
    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userDetailsService = Mockito.mock(CustomUserDetailsService.class);
        securityConfig = new SecurityConfig(userDetailsService);
    }

    private void mockAuthentication(String username, List<String> roles) {
        var authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        var auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void shouldReturnLoggedInUsername() {
        mockAuthentication("customer123", List.of("ROLE_CUSTOMER"));
        assertEquals("customer123", securityConfig.getLoggedInUsername());
    }

    @Test
    void shouldDetectAdminRole() {
        mockAuthentication("admin", List.of("ROLE_ADMIN"));
        assertTrue(securityConfig.isAdmin());
    }

    @Test
    void shouldReturnTrueForMatchingCustomerId() {
        mockAuthentication("customer5", List.of("ROLE_CUSTOMER"));
        Mockito.when(userDetailsService.retrieveCustomerIdFromUsername("customer5"))
                .thenReturn(5L);

        assertTrue(securityConfig.isCustomerAuthorized(5L));
    }

    @Test
    void shouldThrow403IfUnauthorizedCustomerAccess() {
        mockAuthentication("customer9", List.of("ROLE_CUSTOMER"));
        Mockito.when(userDetailsService.retrieveCustomerIdFromUsername("customer9"))
                .thenReturn(9L);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            securityConfig.checkCustomerAccessControl(10L);
        });

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void shouldAllowAccessIfAdmin() {
        mockAuthentication("admin", List.of("ROLE_ADMIN"));

        // No exception should be thrown
        assertDoesNotThrow(() -> securityConfig.checkCustomerAccessControl(999L));
    }

    @Test
    void shouldAllowAccessForMatchingCustomerEntity() {
        mockAuthentication("customer3", List.of("ROLE_CUSTOMER"));
        Mockito.when(userDetailsService.retrieveCustomerIdFromUsername("customer3"))
                .thenReturn(3L);

        Customer customer = new Customer();
        customer.setId(3L);

        assertDoesNotThrow(() -> securityConfig.checkCustomerAccessControl(customer));
    }
}
