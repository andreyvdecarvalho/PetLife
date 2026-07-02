package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.ConsultationRepositoryPort;
import com.petlife.modules.pet.entity.Consultation;
import com.petlife.modules.pet.infrastructure.dto.ConsultationResponse;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeleteConsultationAttachmentUseCase {

    private final ConsultationRepositoryPort consultationRepositoryPort;

    @Transactional
    public ConsultationResponse execute(UUID petId, UUID consultationId, UUID userId, int index) {
        Consultation consultation = consultationRepositoryPort.findById(consultationId)
                .orElseThrow(() -> BusinessException.notFound("CONSULTATION_NOT_FOUND", "Consulta não encontrada."));

        if (!consultation.getPet().getId().equals(petId) || !consultation.getPet().getUser().getId().equals(userId)) {
            throw BusinessException.forbidden("FORBIDDEN_PET_ACCESS", "Este pet não pertence ao usuário autenticado.");
        }

        if (consultation.getAttachments() == null || index < 0 || index >= consultation.getAttachments().size()) {
            throw BusinessException.badRequest("INVALID_ATTACHMENT_INDEX", "Índice de anexo inválido.");
        }

        consultation.getAttachments().remove(index);
        consultation.setUpdatedAt(OffsetDateTime.now());

        Consultation saved = consultationRepositoryPort.save(consultation);
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
