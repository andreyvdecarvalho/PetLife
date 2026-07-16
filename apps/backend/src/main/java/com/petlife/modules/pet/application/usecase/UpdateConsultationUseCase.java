package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.ConsultationRepositoryPort;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.entity.Consultation;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.infrastructure.dto.ConsultationResponse;
import com.petlife.modules.pet.infrastructure.dto.UpdateConsultationRequest;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateConsultationUseCase {

    private final ConsultationRepositoryPort consultationRepositoryPort;
    private final PetRepositoryPort petRepositoryPort;

    @Transactional
    public ConsultationResponse execute(UUID petId, UUID consultationId, UUID userId, UpdateConsultationRequest request) {
        Pet pet = petRepositoryPort.findById(petId)
                .orElseThrow(() -> BusinessException.notFound("PET_NOT_FOUND", "Pet não encontrado."));

        if (!pet.getUser().getId().equals(userId)) {
            throw BusinessException.forbidden("FORBIDDEN_PET_ACCESS", "Este pet não pertence ao usuário autenticado.");
        }

        Consultation consultation = consultationRepositoryPort.findById(consultationId)
                .orElseThrow(() -> BusinessException.notFound("CONSULTATION_NOT_FOUND", "Consulta não encontrada."));

        if (!consultation.getPet().getId().equals(petId)) {
            throw BusinessException.badRequest("CONSULTATION_PET_MISMATCH", "Esta consulta não pertence a este pet.");
        }

        if (request.getReason() != null && !request.getReason().isBlank()) {
            consultation.setReason(request.getReason());
        }
        if (request.getDiagnosis() != null) {
            consultation.setDiagnosis(request.getDiagnosis());
        }
        if (request.getPrescriptions() != null) {
            consultation.setPrescriptions(request.getPrescriptions());
        }
        if (request.getNotes() != null) {
            consultation.setNotes(request.getNotes());
        }
        if (request.getWeightAtVisit() != null) {
            consultation.setWeightAtVisit(request.getWeightAtVisit());
        }
        if (request.getFollowUpDate() != null) {
            consultation.setFollowUpDate(request.getFollowUpDate());
        }

        Consultation saved = consultationRepositoryPort.save(consultation);
        log.info("Consulta {} atualizada com sucesso para o pet {}", consultationId, petId);
        
        return mapToResponse(saved);
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
