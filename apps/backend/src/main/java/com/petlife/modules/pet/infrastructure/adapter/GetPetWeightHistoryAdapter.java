package com.petlife.modules.pet.infrastructure.adapter;

import com.petlife.modules.pet.application.port.GetPetWeightHistoryPort;
import com.petlife.modules.pet.infrastructure.persistence.WeightRecordJpaRepository;
import com.petlife.modules.pet.domain.entity.WeightRecord;
import com.petlife.modules.pet.infrastructure.persistence.mapper.WeightRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter implementation of {@link GetPetWeightHistoryPort} using JPA repository.
 */
@Component
@RequiredArgsConstructor
public class GetPetWeightHistoryAdapter implements GetPetWeightHistoryPort {

    private final WeightRecordJpaRepository weightRecordJpaRepository;
    private final WeightRecordMapper mapper;

    @Override
    public List<WeightRecord> getWeightHistory(UUID petId) {
        return weightRecordJpaRepository.findByPetIdOrderByRecordedAtDesc(petId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
