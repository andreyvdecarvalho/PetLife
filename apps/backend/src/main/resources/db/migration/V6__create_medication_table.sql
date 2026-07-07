CREATE TABLE medication (
    id UUID PRIMARY KEY,
    pet_id UUID NOT NULL,
    name VARCHAR(200) NOT NULL,
    dosage VARCHAR(100) NOT NULL,
    frequency VARCHAR(50) NOT NULL,
    custom_frequency_hours INTEGER,
    start_date DATE NOT NULL,
    end_date DATE,
    times_of_day JSONB NOT NULL DEFAULT '[]'::jsonb,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_medication_pet FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE
);

CREATE TABLE medication_administration (
    id UUID PRIMARY KEY,
    medication_id UUID NOT NULL,
    scheduled_time TIMESTAMP WITH TIME ZONE NOT NULL,
    administered_at TIMESTAMP WITH TIME ZONE,
    status VARCHAR(50) NOT NULL,
    skipped_reason VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_medication_administration_medication FOREIGN KEY (medication_id) REFERENCES medication(id) ON DELETE CASCADE
);

CREATE INDEX idx_medication_pet_id ON medication (pet_id);
CREATE INDEX idx_medication_administration_medication_id ON medication_administration (medication_id);
CREATE INDEX idx_medication_administration_status ON medication_administration (status);
