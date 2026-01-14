-- Create person table (base table for Customer)
CREATE TABLE person (
    id CHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    genre VARCHAR(20),
    birth_date VARCHAR(10) NOT NULL,
    identification VARCHAR(20) NOT NULL UNIQUE,
    address VARCHAR(150) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    deleted_at DATETIME(6),
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100),
    deleted_by VARCHAR(100),
    is_deleted BIT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    INDEX idx_identification (identification)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

