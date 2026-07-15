package com.petlife.modules.veterinarian.application.port;

import com.petlife.modules.veterinarian.entity.Veterinarian;

import java.util.Optional;
import java.util.UUID;

public interface VeterinarianRepositoryPort {
    Veterinarian save(Veterinarian veterinarian);
    Optional<Veterinarian> findById(UUID id);
    Optional<Veterinarian> findByUserId(UUID userId);
    boolean existsByCrmvNumber(String crmvNumber);
    org.springframework.data.domain.Page<Veterinarian> search(
        com.petlife.modules.veterinarian.infrastructure.dto.request.SearchVeterinariansRequest request
    );
}
