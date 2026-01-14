package com.devsu.bank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Configuration for JPA Auditing.
 * Provides the current auditor for @CreatedBy and @LastModifiedBy annotations.
 */
@Configuration
public class AuditorAwareConfig {
    
    /**
     * Bean that provides the current auditor.
     * Currently returns "SYSTEM" for all operations since authentication is not implemented.
     * 
     * TODO: When authentication is implemented, extract username from SecurityContext:
     * - For JWT: Extract from JWT token claims
     * - For Basic Auth: Use authentication.getName()
     * 
     * @return AuditorAware implementation
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            // If no authentication or anonymous user, return SYSTEM
            if (authentication == null || !authentication.isAuthenticated() 
                    || "anonymousUser".equals(authentication.getPrincipal())) {
                return Optional.of("SYSTEM");
            }
            
            // When authentication is implemented, return actual username
            // return Optional.ofNullable(authentication.getName());
            
            return Optional.of("SYSTEM");
        };
    }
}
