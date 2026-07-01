package com.petlife.modules.pet.infrastructure.adapter;

import com.petlife.modules.pet.application.port.GetPetWeightHistoryPort;
import com.petlife.modules.pet.infrastructure.persistence.WeightRecordJpaRepository;
import com.petlife.modules.pet.entity.WeightRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.UUID;

/**
 * Adapter implementation of {@link GetPetWeightHistoryPort} using JPA repository.
 */
@Component
@RequiredArgsConstructor
public class GetPetWeightHistoryAdapter implements GetPetWeightHistoryPort {

    private final WeightRecordJpaRepository weightRecordJpaRepository;

    @Override
    public List<WeightRecord> getWeightHistory(UUID petId) {
        // Fetch weight records ordered by recordedAt desc.
        return weightRecordJpaRepository.findByPetIdOrderByRecordedAtDesc(petId);
    }
}
