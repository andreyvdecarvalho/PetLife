CREATE TABLE vet_addresses (
    id UUID PRIMARY KEY,
    veterinarian_id UUID NOT NULL,
    label VARCHAR(100) NOT NULL,
    street VARCHAR(255),
    number VARCHAR(50),
    complement VARCHAR(255),
    neighborhood VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(2),
    postal_code VARCHAR(10) NOT NULL,
    latitude DECIMAL(10,8),
    longitude DECIMAL(11,8),
    is_primary BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_vet_address_veterinarian FOREIGN KEY (veterinarian_id) REFERENCES veterinarians (id) ON DELETE CASCADE
);
