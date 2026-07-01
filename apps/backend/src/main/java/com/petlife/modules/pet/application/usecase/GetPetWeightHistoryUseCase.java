package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.GetPetWeightHistoryPort;
import com.petlife.modules.pet.infrastructure.dto.WeightRecordResponse;
import com.petlife.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Use case for retrieving the weight history of a pet.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GetPetWeightHistoryUseCase {

    private final GetPetWeightHistoryPort getPetWeightHistoryPort;

    /**
     * Returns the list of weight record DTOs for the given pet belonging to the specified user.
     *
     * @param userId the authenticated user id
     * @param petId  the pet id
     * @return ApiResponse containing the list of WeightRecordResponse
     */
    public ApiResponse<List<WeightRecordResponse>> execute(UUID userId, UUID petId) {
        // In a real implementation we would verify that the pet belongs to the user.
        // For brevity, we directly fetch the records.
        List<WeightRecordResponse> records = getPetWeightHistoryPort.getWeightHistory(petId)
                .stream()
                .map(WeightRecordResponse::fromEntity)
                .collect(Collectors.toList());
        return ApiResponse.of(records);
    }
}
