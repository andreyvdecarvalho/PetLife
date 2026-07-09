CREATE TYPE grooming_type AS ENUM ('BATH', 'GROOMING', 'BATH_AND_GROOMING');

CREATE TABLE groomings (
    id UUID PRIMARY KEY,
    pet_id UUID NOT NULL,
    type grooming_type NOT NULL,
    date DATE NOT NULL,
    provider VARCHAR(200),
    cost NUMERIC(10, 2),
    frequency_days INTEGER,
    next_date DATE,
    notes TEXT,
    photos JSONB DEFAULT '[]'::jsonb,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_grooming_pet FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE
);

CREATE INDEX idx_groomings_pet_id ON groomings (pet_id);
