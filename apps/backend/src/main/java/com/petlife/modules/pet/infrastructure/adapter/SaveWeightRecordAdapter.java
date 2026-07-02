package com.petlife.modules.pet.infrastructure.adapter;

import com.petlife.modules.pet.application.port.SaveWeightRecordPort;
import com.petlife.modules.pet.entity.WeightRecord;
import com.petlife.modules.pet.infrastructure.persistence.WeightRecordJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaveWeightRecordAdapter implements SaveWeightRecordPort {

    private final WeightRecordJpaRepository repository;

    @Override
    public WeightRecord save(WeightRecord weightRecord) {
        return repository.save(weightRecord);
    }
}
