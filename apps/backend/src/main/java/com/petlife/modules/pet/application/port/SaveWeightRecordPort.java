package com.petlife.modules.pet.application.port;

import com.petlife.modules.pet.entity.WeightRecord;

public interface SaveWeightRecordPort {
    WeightRecord save(WeightRecord weightRecord);
}
