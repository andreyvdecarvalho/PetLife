package com.petlife.modules.pet.infrastructure.adapter;

import com.petlife.modules.pet.application.port.SaveWeightRecordPort;
import com.petlife.modules.pet.domain.entity.WeightRecord;
import com.petlife.modules.pet.infrastructure.persistence.WeightRecordJpaRepository;
import com.petlife.modules.pet.infrastructure.persistence.mapper.WeightRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaveWeightRecordAdapter implements SaveWeightRecordPort {

    private final WeightRecordJpaRepository repository;
    private final WeightRecordMapper mapper;

    @Override
    public WeightRecord save(WeightRecord weightRecord) {
        return mapper.toDomain(repository.save(mapper.toJpaEntity(weightRecord)));
    }
}
