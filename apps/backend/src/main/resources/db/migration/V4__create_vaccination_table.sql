CREATE TABLE vaccination (
    id UUID PRIMARY KEY,
    pet_id UUID NOT NULL,
    vaccine_name VARCHAR(200) NOT NULL,
    date_administered DATE NOT NULL,
    next_dose_date DATE,
    veterinarian VARCHAR(200),
    clinic VARCHAR(200),
    batch_number VARCHAR(100),
    manufacturer VARCHAR(100),
    proof_url VARCHAR(500),
    notes TEXT,
    reminder_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_vaccination_pet FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE
);
