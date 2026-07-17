package com.petlife.modules.pet.application.port;

import com.petlife.modules.pet.entity.WeightRecord;

public interface DeleteWeightRecordPort {
    void delete(WeightRecord weightRecord);
}
