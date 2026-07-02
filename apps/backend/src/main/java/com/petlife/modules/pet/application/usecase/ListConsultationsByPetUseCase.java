package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.ConsultationRepositoryPort;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.entity.Consultation;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.infrastructure.dto.ConsultationResponse;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ListConsultationsByPetUseCase {

    private final ConsultationRepositoryPort consultationRepositoryPort;
    private final PetRepositoryPort petRepositoryPort;

    @Transactional(readOnly = true)
    public List<ConsultationResponse> execute(UUID petId, UUID userId) {
        Pet pet = petRepositoryPort.findById(petId)
                .orElseThrow(() -> BusinessException.notFound("PET_NOT_FOUND", "Pet não encontrado."));

        if (!pet.getUser().getId().equals(userId)) {
            throw BusinessException.forbidden("FORBIDDEN_PET_ACCESS", "Este pet não pertence ao usuário autenticado.");
        }

        List<Consultation> consultations = consultationRepositoryPort.findAllByPetId(petId);
        return consultations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ConsultationResponse mapToResponse(Consultation consultation) {
        ConsultationResponse response = new ConsultationResponse();
        response.setId(consultation.getId());
        response.setPetId(consultation.getPet().getId());
        response.setDate(consultation.getDate());
        response.setVeterinarian(consultation.getVeterinarian());
        response.setClinic(consultation.getClinic());
        response.setReason(consultation.getReason());
        response.setDiagnosis(consultation.getDiagnosis());
        response.setPrescriptions(consultation.getPrescriptions());
        response.setNotes(consultation.getNotes());
        response.setWeightAtVisit(consultation.getWeightAtVisit());
        response.setFollowUpDate(consultation.getFollowUpDate());
        response.setCost(consultation.getCost());
        response.setAttachments(consultation.getAttachments());
        response.setCreatedAt(consultation.getCreatedAt());
        response.setUpdatedAt(consultation.getUpdatedAt());
        return response;
    }
}
