CREATE TABLE users(
    id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    email VARCHAR(50) NOT NULL UNIQUE,
    first_name VARCHAR(25) NOT NULL,
    last_name VARCHAR(25) NOT NULL,
    password_hash TEXT NOT NULL,
    is_email_verified BOOLEAN NOT NULL DEFAULT FALSE,
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

CREATE TABLE email_verifications(
    user_id UUID NOT NULL UNIQUE,
    verification_code VARCHAR(6) NOT NULL,
    expires_in TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP + INTERVAL '300'),
    PRIMARY KEY (user_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);