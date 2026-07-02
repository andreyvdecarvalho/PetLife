package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.ConsultationRepositoryPort;
import com.petlife.modules.pet.entity.Consultation;
import com.petlife.modules.pet.infrastructure.dto.ConsultationResponse;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UploadConsultationAttachmentUseCase {

    private final ConsultationRepositoryPort consultationRepositoryPort;

    @Transactional
    public ConsultationResponse execute(UUID petId, UUID consultationId, UUID userId, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw BusinessException.badRequest("FILE_EMPTY", "Nenhum arquivo enviado.");
        }

        Consultation consultation = consultationRepositoryPort.findById(consultationId)
                .orElseThrow(() -> BusinessException.notFound("CONSULTATION_NOT_FOUND", "Consulta não encontrada."));

        if (!consultation.getPet().getId().equals(petId) || !consultation.getPet().getUser().getId().equals(userId)) {
            throw BusinessException.forbidden("FORBIDDEN_PET_ACCESS", "Este pet não pertence ao usuário autenticado.");
        }

        if (consultation.getAttachments() == null) {
            consultation.setAttachments(new ArrayList<>());
        }

        if (consultation.getAttachments().size() + files.size() > 5) {
            throw BusinessException.badRequest(
                    "ATTACHMENT_LIMIT_EXCEEDED",
                    "Limite de 5 anexos por consulta excedido."
            );
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw BusinessException.badRequest("FILE_EMPTY", "Arquivo vazio.");
            }

            // Validar tamanho máximo de 2MB
            if (file.getSize() > 2 * 1024 * 1024) {
                throw BusinessException.badRequest("FILE_TOO_LARGE", "O arquivo deve ter no máximo 2MB.");
            }

            // Validar tipo de arquivo
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                throw BusinessException.badRequest("INVALID_FILE_TYPE", "Nome de arquivo inválido.");
            }
            String lowerName = originalFilename.toLowerCase();
            boolean isValidType = lowerName.endsWith(".jpg")
                    || lowerName.endsWith(".jpeg")
                    || lowerName.endsWith(".png")
                    || lowerName.endsWith(".pdf");
            if (!isValidType) {
                throw BusinessException.badRequest(
                        "INVALID_FILE_TYPE",
                        "Tipo de arquivo não permitido. Apenas JPEG, PNG e PDF são aceitos."
                );
            }

            // Simular upload
            String fakeUrl = "https://s3.amazonaws.com/petlife/consultations/"
                    + consultationId + "_" + originalFilename;
            consultation.getAttachments().add(fakeUrl);
        }

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
