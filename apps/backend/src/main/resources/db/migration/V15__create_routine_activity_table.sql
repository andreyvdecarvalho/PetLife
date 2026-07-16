CREATE TYPE routine_activity_type AS ENUM ('WALK', 'FEEDING', 'GENERIC');
CREATE TYPE routine_activity_status AS ENUM ('PENDING', 'COMPLETED', 'SCHEDULED');

CREATE TABLE routine_activities (
    id UUID PRIMARY KEY,
    pet_id UUID NOT NULL REFERENCES pets(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    activity_date DATE NOT NULL,
    activity_time TIME,
    type routine_activity_type NOT NULL,
    status routine_activity_status NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_routine_activities_pet_id ON routine_activities(pet_id);
CREATE INDEX idx_routine_activities_date ON routine_activities(activity_date);
