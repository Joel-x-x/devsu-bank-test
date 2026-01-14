-- Create customer table (inherits from person with JOINED strategy)
-- Only contains customer-specific fields, person fields are in person table
CREATE TABLE customer (
    id CHAR(36) NOT NULL,
    customer_code VARCHAR(10) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    status BIT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    FOREIGN KEY (id) REFERENCES person(id) ON DELETE CASCADE,
    INDEX idx_customer_code (customer_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

