CREATE TABLE vet_schedules (
    id UUID PRIMARY KEY,
    veterinarian_id UUID NOT NULL,
    day_of_week VARCHAR(20) NOT NULL,
    open_time TIME NOT NULL,
    close_time TIME NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_vet_schedule_veterinarian FOREIGN KEY (veterinarian_id) REFERENCES veterinarians (id) ON DELETE CASCADE
);
