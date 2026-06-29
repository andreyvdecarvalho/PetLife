CREATE TYPE pet_species AS ENUM ('DOG', 'CAT', 'BIRD', 'FISH', 'RODENT', 'REPTILE', 'OTHER');
CREATE TYPE pet_sex     AS ENUM ('MALE', 'FEMALE', 'UNKNOWN');
CREATE TYPE pet_size    AS ENUM ('MINI', 'SMALL', 'MEDIUM', 'LARGE', 'GIANT');
CREATE TYPE pet_status  AS ENUM ('ACTIVE', 'ARCHIVED', 'DECEASED');

CREATE TABLE pets (
    id           UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name         VARCHAR(100) NOT NULL,
    species      pet_species  NOT NULL,
    breed        VARCHAR(100),
    sex          pet_sex      NOT NULL,
    birth_date   DATE,
    weight_kg    NUMERIC(5,2),
    size         pet_size,
    neutered     BOOLEAN      NOT NULL DEFAULT FALSE,
    microchip_id VARCHAR(50),
    allergies    TEXT,
    notes        VARCHAR(2000),
    photo_url    VARCHAR(500),
    status       pet_status   NOT NULL DEFAULT 'ACTIVE',
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_pets_user_id        ON pets (user_id);
CREATE INDEX idx_pets_user_id_status ON pets (user_id, status);
