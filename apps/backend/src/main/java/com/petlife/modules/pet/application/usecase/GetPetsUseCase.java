package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.domain.entity.Pet;
import com.petlife.modules.pet.domain.entity.PetStatus;
import com.petlife.modules.pet.infrastructure.dto.PetResponse;
import com.petlife.shared.domain.PageResult;

import com.petlife.shared.response.PageMeta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

import com.petlife.modules.notification.application.usecase.PagedResult;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetPetsUseCase {

    private final PetRepositoryPort petRepository;

    @Transactional(readOnly = true)
    public PagedResult<PetResponse> execute(UUID userId, PetStatus status, int page, int size) {
        PageResult<Pet> pageResult;
        if (status != null) {
            pageResult = petRepository.findByUserIdAndStatus(userId, status, page, size);
        } else {
            pageResult = petRepository.findByUserIdAndStatusNot(userId, PetStatus.ARCHIVED, page, size);
        }

        List<PetResponse> content = pageResult.getContent().stream()
                .map(PetResponse::fromEntity)
                .toList();

        PageMeta meta = new PageMeta(
                pageResult.getPageNumber(),
                pageResult.getPageSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );

        return new PagedResult<>(content, meta);
    }
}
