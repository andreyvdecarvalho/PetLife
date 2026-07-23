package com.petlife.modules.pet.infrastructure.adapter;

import com.petlife.modules.pet.application.port.DeleteWeightRecordPort;
import com.petlife.modules.pet.domain.entity.WeightRecord;
import com.petlife.modules.pet.infrastructure.persistence.WeightRecordJpaRepository;
import com.petlife.modules.pet.infrastructure.persistence.mapper.WeightRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteWeightRecordAdapter implements DeleteWeightRecordPort {

    private final WeightRecordJpaRepository repository;
    private final WeightRecordMapper mapper;

    @Override
    public void delete(WeightRecord weightRecord) {
        repository.delete(mapper.toJpaEntity(weightRecord));
    }
}
