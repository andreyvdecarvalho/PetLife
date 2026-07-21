package com.petlife.modules.pet.application.port;

import com.petlife.modules.pet.domain.entity.WeightRecord;
import java.util.Optional;
import java.util.UUID;

public interface FindWeightRecordPort {
    Optional<WeightRecord> findById(UUID id);
}
