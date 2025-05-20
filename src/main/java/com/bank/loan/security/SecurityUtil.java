package com.bank.loan.security;

import com.bank.loan.model.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

public class SecurityUtil {
    public static String getLoggedInUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }

    public static boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    public static boolean isCustomerAuthorized(Long id) {
        String username = getLoggedInUsername();
        return retrieveCustomerIdFromUsername(username).equals(id);
    }
    public static void checkCustomerAccessControl(Long customerId) {
        if (!isAdmin() && !isCustomerAuthorized(customerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }
    public static void checkCustomerAccessControl(Customer customer) {
        if (customer != null) checkCustomerAccessControl(customer.getId());
    }

    public static Long retrieveCustomerIdFromUsername(String username) {
        // String username  customer232 will return 232
        username = username.replace("customer", "");
        return Long.parseLong(username);
    }
}