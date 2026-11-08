CREATE TABLE IF NOT EXISTS customers (
    id CHAR(36) PRIMARY KEY,

    user_id CHAR(36) NOT NULL,

    name VARCHAR(255) NOT NULL,
    cpf VARCHAR(20) NOT NULL UNIQUE,
    birth_date DATE,
    phone_number VARCHAR(20),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP DEFAULT NULL,

    CONSTRAINT fk_customers_user FOREIGN KEY (user_id) REFERENCES users(id),

    active_cpf VARCHAR(20)
    GENERATED ALWAYS AS (
        CASE
        WHEN deleted_at IS NULL THEN cpf
        ELSE NULL
        END
    ) STORED,

    UNIQUE (active_cpf)
) ENGINE=InnoDB;