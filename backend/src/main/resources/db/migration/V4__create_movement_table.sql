-- Create movement table
CREATE TABLE movement (
    id CHAR(36) NOT NULL,
    movement_date DATETIME(6) NOT NULL,
    movement_type VARCHAR(20),
    amount DECIMAL(19, 4) NOT NULL,
    balance DECIMAL(19, 4) NOT NULL,
    available_balance DECIMAL(19, 4) NOT NULL,
    account_id CHAR(36) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    deleted_at DATETIME(6),
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100),
    deleted_by VARCHAR(100),
    is_deleted BIT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    INDEX idx_movement_date (movement_date),
    INDEX idx_movement_type (movement_type),
    INDEX idx_account_id (account_id),
    CONSTRAINT fk_movement_account FOREIGN KEY (account_id) REFERENCES account(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

