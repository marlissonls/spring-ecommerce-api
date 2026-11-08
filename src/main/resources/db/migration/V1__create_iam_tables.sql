CREATE TABLE IF NOT EXISTS users (
    id CHAR(36) PRIMARY KEY,

    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,

    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP DEFAULT NULL,

    active_email VARCHAR(255)
    GENERATED ALWAYS AS (
        CASE
        WHEN deleted_at IS NULL THEN email
        ELSE NULL
        END
    ) STORED,

    UNIQUE (active_email)
) ENGINE=InnoDB;

--
CREATE TABLE IF NOT EXISTS roles (
    id CHAR(36) PRIMARY KEY,
    title VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255) DEFAULT NULL
) ENGINE=InnoDB;

--
CREATE TABLE IF NOT EXISTS user_roles (
    user_id CHAR(36) NOT NULL,
    role_id CHAR(36) NOT NULL,

    PRIMARY KEY(user_id, role_id),

    KEY idx_user_roles_role_id (role_id),

    CONSTRAINT fk_user_roles_user
    FOREIGN KEY (user_id) REFERENCES users (id)
    ON DELETE CASCADE,

    CONSTRAINT fk_user_roles_role
    FOREIGN KEY (role_id) REFERENCES roles (id)
    ON DELETE CASCADE
) ENGINE=InnoDB;
