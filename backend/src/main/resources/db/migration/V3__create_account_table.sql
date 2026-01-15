-- Create account table
CREATE TABLE account (
    id CHAR(36) NOT NULL,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    account_type VARCHAR(20),
    initial_balance DECIMAL(19, 4) NOT NULL,
    daily_limit DECIMAL(19, 4) NOT NULL,
    customer_id CHAR(36) NOT NULL,
    status BIT(1) NOT NULL DEFAULT 1,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    deleted_at DATETIME(6),
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100),
    deleted_by VARCHAR(100),
    is_deleted BIT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    INDEX idx_account_number (account_number),
    INDEX idx_customer_id (customer_id),
    CONSTRAINT fk_account_customer FOREIGN KEY (customer_id) REFERENCES customer(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

