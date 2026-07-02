CREATE TABLE consultation (
    id UUID PRIMARY KEY,
    pet_id UUID NOT NULL,
    date TIMESTAMP WITH TIME ZONE NOT NULL,
    veterinarian VARCHAR(200),
    clinic VARCHAR(200),
    reason VARCHAR(500) NOT NULL,
    diagnosis TEXT,
    prescriptions TEXT,
    notes TEXT,
    weight_at_visit DECIMAL(5,2),
    follow_up_date DATE,
    cost DECIMAL(10,2),
    attachments JSONB NOT NULL DEFAULT '[]'::jsonb,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_consultation_pet FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE
);
