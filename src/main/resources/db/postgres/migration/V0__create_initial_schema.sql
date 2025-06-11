CREATE TABLE users(
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    email VARCHAR(50) NOT NULL UNIQUE,
    first_name VARCHAR(25) NOT NULL,
    last_name VARCHAR(25) NOT NULL,
    password TEXT NOT NULL,
    is_account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    is_account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    is_credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE authorities(
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    authority_type VARCHAR(25) NOT NULL DEFAULT 'USER',
    PRIMARY KEY (id)
);

CREATE TABLE users_authorities(
    user_id UUID NOT NULL,
    authority_id UUID NOT NULL,
    PRIMARY KEY (user_id, authority_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (authority_id) REFERENCES authorities (id)
);