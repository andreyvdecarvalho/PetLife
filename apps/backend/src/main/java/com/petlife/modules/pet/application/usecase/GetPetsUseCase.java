package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.entity.PetStatus;
import com.petlife.modules.pet.infrastructure.dto.PetResponse;
import com.petlife.shared.response.ApiResponse;
import com.petlife.shared.response.PageMeta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetPetsUseCase {

    private final PetRepositoryPort petRepository;

    @Transactional(readOnly = true)
    public ApiResponse<List<PetResponse>> execute(UUID userId, PetStatus status, Pageable pageable) {
        Page<Pet> page;
        if (status != null) {
            page = petRepository.findByUserIdAndStatus(userId, status, pageable);
        } else {
            page = petRepository.findByUserIdAndStatusNot(userId, PetStatus.ARCHIVED, pageable);
        }

        List<PetResponse> content = page.getContent().stream()
                .map(PetResponse::fromEntity)
                .toList();

        PageMeta meta = new PageMeta(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        return ApiResponse.paged(content, meta);
    }
}
