package com.devsu.bank.account.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * Configuration properties for account business rules.
 * Values can be overridden in application.yml.
 */
@Configuration
@ConfigurationProperties(prefix = "account.business")
public class AccountBusinessConfig {
    
    /**
     * Maximum daily transaction limit allowed for an account.
     */
    private BigDecimal maxDailyLimit = new BigDecimal("1000.00");
    
    /**
     * Minimum daily transaction limit allowed for an account.
     */
    private BigDecimal minDailyLimit = new BigDecimal("0.01");
    
    /**
     * Maximum number of accounts a customer can have of the same type.
     */
    private Integer maxAccountsPerType = 3;

    // Getters and Setters
    
    public BigDecimal getMaxDailyLimit() {
        return maxDailyLimit;
    }

    public void setMaxDailyLimit(BigDecimal maxDailyLimit) {
        this.maxDailyLimit = maxDailyLimit;
    }

    public BigDecimal getMinDailyLimit() {
        return minDailyLimit;
    }

    public void setMinDailyLimit(BigDecimal minDailyLimit) {
        this.minDailyLimit = minDailyLimit;
    }

    public Integer getMaxAccountsPerType() {
        return maxAccountsPerType;
    }

    public void setMaxAccountsPerType(Integer maxAccountsPerType) {
        this.maxAccountsPerType = maxAccountsPerType;
    }
}
