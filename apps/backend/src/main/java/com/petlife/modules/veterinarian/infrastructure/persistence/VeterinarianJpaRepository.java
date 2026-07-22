package com.petlife.modules.veterinarian.infrastructure.persistence;

import com.petlife.modules.veterinarian.infrastructure.persistence.entity.VeterinarianJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VeterinarianJpaRepository extends JpaRepository<VeterinarianJpaEntity, UUID>,
        JpaSpecificationExecutor<VeterinarianJpaEntity> {
    Optional<VeterinarianJpaEntity> findByUserId(UUID userId);
    boolean existsByCrmvNumber(String crmvNumber);

    @Query(value = """
        SELECT v.* FROM veterinarians v
        LEFT JOIN vet_addresses a ON v.id = a.veterinarian_id AND a.is_primary = true
        WHERE v.crmv_status = 'APPROVED'
          AND v.availability_status = 'AVAILABLE'
          AND (:emergency IS NULL OR v.emergency_on_duty = :emergency)
          AND (:modality IS NULL OR CAST(v.modalities AS text) ILIKE CONCAT('%', :modality, '%'))
          AND (:specialty IS NULL OR CAST(v.specialties AS text) ILIKE CONCAT('%', :specialty, '%'))
          AND (
            :latitude IS NULL OR :longitude IS NULL OR a.latitude IS NULL OR a.longitude IS NULL OR
            (6371 * acos(
              cos(radians(CAST(:latitude AS double precision))) * cos(radians(CAST(a.latitude AS double precision))) *
              cos(radians(CAST(a.longitude AS double precision)) - radians(CAST(:longitude AS double precision))) +
              sin(radians(CAST(:latitude AS double precision))) * sin(radians(CAST(a.latitude AS double precision)))
            )) <= COALESCE(:radiusKm, 10.0)
          )
    """, nativeQuery = true)
    Page<VeterinarianJpaEntity> searchVeterinarians(
        @Param("latitude") BigDecimal latitude,
        @Param("longitude") BigDecimal longitude,
        @Param("radiusKm") Double radiusKm,
        @Param("modality") String modality,
        @Param("specialty") String specialty,
        @Param("emergency") Boolean emergency,
        Pageable pageable
    );
}
