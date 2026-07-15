CREATE TABLE vet_favorites (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    veterinarian_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_vet_favorite_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_vet_favorite_veterinarian FOREIGN KEY (veterinarian_id) REFERENCES veterinarians (id) ON DELETE CASCADE,
    CONSTRAINT uk_vet_favorite_user_vet UNIQUE (user_id, veterinarian_id)
);
