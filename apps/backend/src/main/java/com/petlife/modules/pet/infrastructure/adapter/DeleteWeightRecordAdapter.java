package com.petlife.modules.pet.infrastructure.adapter;

import com.petlife.modules.pet.application.port.DeleteWeightRecordPort;
import com.petlife.modules.pet.entity.WeightRecord;
import com.petlife.modules.pet.infrastructure.persistence.WeightRecordJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteWeightRecordAdapter implements DeleteWeightRecordPort {
    private final WeightRecordJpaRepository repository;

    @Override
    public void delete(WeightRecord weightRecord) {
        repository.delete(weightRecord);
    }
}
