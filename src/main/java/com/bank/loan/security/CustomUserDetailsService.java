package com.bank.loan.security;

import com.bank.loan.model.Customer;
import com.bank.loan.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerService customerService;
    private final String adminUsername;
    private final String adminPassword;

    @Autowired
    public CustomUserDetailsService(
            CustomerService customerService,
            @Value("${admin.username:admin}") String adminUsername,
            @Value("${admin.password}")String adminPassword) {
        this.customerService = customerService;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (adminUsername.equals(username)) {
            return User.builder()
                    .username(adminUsername)
                    .password("{noop}" + adminPassword)
                    .roles("ADMIN")
                    .build();
        }
        try {
            Long customerId = retrieveCustomerIdFromUsername(username);
            Customer customer = customerService.getCustomerEntityById(customerId);
            return User.builder()
                    .username(username)
                    .password("{noop}" + customer.getUuid())
                    .roles("CUSTOMER")
                    .build();
        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found");
        }
    }

    public Long retrieveCustomerIdFromUsername(String username) {
        username = username.replace("customer", "");
        return Long.parseLong(username);
    }
}
