package com.petlife.modules.pet.infrastructure.adapter;

import com.petlife.modules.pet.application.port.FindWeightRecordPort;
import com.petlife.modules.pet.entity.WeightRecord;
import com.petlife.modules.pet.infrastructure.persistence.WeightRecordJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FindWeightRecordAdapter implements FindWeightRecordPort {
    private final WeightRecordJpaRepository repository;

    @Override
    public Optional<WeightRecord> findById(UUID id) {
        return repository.findById(id);
    }
}
