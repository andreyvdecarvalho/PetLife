package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.ConsultationRepositoryPort;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.application.port.SaveWeightRecordPort;
import com.petlife.modules.pet.entity.Consultation;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.entity.WeightRecord;
import com.petlife.modules.pet.infrastructure.dto.CreateConsultationRequest;
import com.petlife.modules.pet.infrastructure.dto.ConsultationResponse;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CreateConsultationUseCase {

    private final ConsultationRepositoryPort consultationRepositoryPort;
    private final PetRepositoryPort petRepositoryPort;
    private final SaveWeightRecordPort saveWeightRecordPort;

    @Transactional
    public ConsultationResponse execute(UUID petId, UUID userId, CreateConsultationRequest request) {
        Pet pet = petRepositoryPort.findById(petId)
                .orElseThrow(() -> BusinessException.notFound("PET_NOT_FOUND", "Pet não encontrado."));

        if (!pet.getUser().getId().equals(userId)) {
            throw BusinessException.forbidden("FORBIDDEN_PET_ACCESS", "Este pet não pertence ao usuário autenticado.");
        }

        if (request.getFollowUpDate() != null
                && request.getFollowUpDate().isBefore(request.getDate().toLocalDate())) {
            throw BusinessException.badRequest(
                    "INVALID_DATE",
                    "A data de retorno não pode ser anterior à data da consulta."
            );
        }

        Consultation consultation = new Consultation();
        consultation.setPet(pet);
        consultation.setDate(request.getDate());
        consultation.setVeterinarian(request.getVeterinarian());
        consultation.setClinic(request.getClinic());
        consultation.setReason(request.getReason());
        consultation.setDiagnosis(request.getDiagnosis());
        consultation.setPrescriptions(request.getPrescriptions());
        consultation.setNotes(request.getNotes());
        consultation.setWeightAtVisit(request.getWeightAtVisit());
        consultation.setFollowUpDate(request.getFollowUpDate());
        consultation.setCost(request.getCost());
        consultation.setCreatedAt(OffsetDateTime.now());

        if (request.getWeightAtVisit() != null) {
            // Salva registro de peso histórico
            WeightRecord weightRecord = new WeightRecord();
            weightRecord.setPet(pet);
            weightRecord.setWeightKg(request.getWeightAtVisit());
            weightRecord.setRecordedAt(request.getDate());
            saveWeightRecordPort.save(weightRecord);

            // Verifica se esta consulta é a mais recente
            List<Consultation> consultations = consultationRepositoryPort.findAllByPetId(petId);
            boolean isLatest = consultations.isEmpty() || consultations.stream()
                    .allMatch(c -> c.getDate().isBefore(request.getDate()) || c.getDate().isEqual(request.getDate()));

            if (isLatest) {
                pet.setWeightKg(request.getWeightAtVisit());
                petRepositoryPort.save(pet);
            }
        }

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
