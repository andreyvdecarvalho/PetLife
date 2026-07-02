package com.petlife.modules.pet.infrastructure.persistence;

import com.petlife.modules.pet.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConsultationJpaRepository extends JpaRepository<Consultation, UUID> {
    List<Consultation> findByPetIdOrderByDateDesc(UUID petId);
}
