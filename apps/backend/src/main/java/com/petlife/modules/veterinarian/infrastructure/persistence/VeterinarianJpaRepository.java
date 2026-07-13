package com.petlife.modules.veterinarian.infrastructure.persistence;

import com.petlife.modules.veterinarian.entity.Veterinarian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VeterinarianJpaRepository extends JpaRepository<Veterinarian, UUID>, JpaSpecificationExecutor<Veterinarian> {
    Optional<Veterinarian> findByUserId(UUID userId);
    boolean existsByCrmvNumber(String crmvNumber);
}
