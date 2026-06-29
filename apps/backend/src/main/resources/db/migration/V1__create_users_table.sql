CREATE TYPE user_plan AS ENUM ('FREE', 'PREMIUM', 'FAMILY');

CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(200)  NOT NULL,
    email           VARCHAR(255)  NOT NULL UNIQUE,
    password_hash   VARCHAR(60),
    avatar_url      VARCHAR(500),
    timezone        VARCHAR(50)   NOT NULL DEFAULT 'America/Sao_Paulo',
    plan            user_plan     NOT NULL DEFAULT 'FREE',
    email_verified  BOOLEAN       NOT NULL DEFAULT FALSE,
    lgpd_accepted_at TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_email ON users (email);
