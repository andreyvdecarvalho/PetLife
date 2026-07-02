package com.petlife.modules.pet.application.port;

import com.petlife.modules.pet.entity.WeightRecord;
import java.util.List;
import java.util.UUID;

/**
 * Port for fetching weight history of a pet.
 */
public interface GetPetWeightHistoryPort {
    /**
     * Returns the list of weight records for the given pet, ordered by recordedAt descending.
     */
    List<WeightRecord> getWeightHistory(UUID petId);
}
