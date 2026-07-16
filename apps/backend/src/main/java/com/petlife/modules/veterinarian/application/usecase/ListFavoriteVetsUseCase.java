package com.petlife.modules.veterinarian.application.usecase;

import com.petlife.modules.veterinarian.application.port.VetFavoriteRepositoryPort;
import com.petlife.modules.veterinarian.infrastructure.dto.response.VeterinarianResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListFavoriteVetsUseCase {
    private final VetFavoriteRepositoryPort vetFavoriteRepository;

    public List<VeterinarianResponse> execute(UUID userId) {
        return vetFavoriteRepository.findByUserId(userId).stream()
                .map(favorite -> VeterinarianResponse.fromEntity(favorite.getVeterinarian()))
                .collect(Collectors.toList());
    }
}
