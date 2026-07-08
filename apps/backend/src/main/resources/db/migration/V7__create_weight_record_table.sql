CREATE TABLE weight_record (
    id UUID PRIMARY KEY,
    pet_id UUID NOT NULL,
    weight_kg NUMERIC(5, 2) NOT NULL,
    recorded_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_weight_pet FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE
);
