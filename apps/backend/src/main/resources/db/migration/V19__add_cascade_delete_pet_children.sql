-- LGPD Art. 18 VI — Garantir eliminação em cascata de todos os dados vinculados ao pet
-- As constraints já possuem ON DELETE CASCADE originalmente, mas esta migration garante a conformidade

DO $$
BEGIN
    -- vaccination
    IF EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_vaccination_pet'
    ) THEN
        ALTER TABLE vaccination DROP CONSTRAINT fk_vaccination_pet;
    END IF;
    ALTER TABLE vaccination
        ADD CONSTRAINT fk_vaccination_pet
        FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE;

    -- consultation
    IF EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_consultation_pet'
    ) THEN
        ALTER TABLE consultation DROP CONSTRAINT fk_consultation_pet;
    END IF;
    ALTER TABLE consultation
        ADD CONSTRAINT fk_consultation_pet
        FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE;

    -- medication
    IF EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_medication_pet'
    ) THEN
        ALTER TABLE medication DROP CONSTRAINT fk_medication_pet;
    END IF;
    ALTER TABLE medication
        ADD CONSTRAINT fk_medication_pet
        FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE;

    -- groomings
    IF EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_grooming_pet'
    ) THEN
        ALTER TABLE groomings DROP CONSTRAINT fk_grooming_pet;
    END IF;
    ALTER TABLE groomings
        ADD CONSTRAINT fk_grooming_pet
        FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE;

    -- weight_record
    IF EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_weight_pet'
    ) THEN
        ALTER TABLE weight_record DROP CONSTRAINT fk_weight_pet;
    END IF;
    ALTER TABLE weight_record
        ADD CONSTRAINT fk_weight_pet
        FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE;
END $$;
