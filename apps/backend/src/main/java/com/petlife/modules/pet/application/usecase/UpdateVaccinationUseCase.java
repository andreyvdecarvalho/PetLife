package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.VaccinationPort;
import com.petlife.modules.pet.entity.Vaccination;
import com.petlife.modules.pet.infrastructure.dto.UpdateVaccinationRequest;
import com.petlife.modules.pet.infrastructure.dto.VaccinationResponse;
import com.petlife.shared.exception.BusinessException;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UpdateVaccinationUseCase {

    private final VaccinationPort vaccinationPort;

    public UpdateVaccinationUseCase(VaccinationPort vaccinationPort) {
        this.vaccinationPort = vaccinationPort;
    }

    public VaccinationResponse execute(UUID petId, UUID vaccinationId, UUID userId, UpdateVaccinationRequest request) {
        Vaccination vaccination = vaccinationPort.findById(vaccinationId)
                .orElseThrow(() -> BusinessException.notFound("VACCINE_NOT_FOUND", "Vaccination not found"));

        if (!vaccination.getPet().getId().equals(petId)) {
            throw BusinessException.forbidden("VACCINE_NOT_BELONG", "Vaccination does not belong to this pet");
        }

        if (!vaccination.getPet().getUser().getId().equals(userId)) {
            throw BusinessException.forbidden("VACCINE_FORBIDDEN", 
                    "You don't have permission to update this vaccination");
        }

        vaccination.setVaccineName(request.getVaccineName());
        vaccination.setDateAdministered(request.getDateAdministered());
        
        if (request.getNextDoseDate() != null && request.getNextDoseDate().isBefore(request.getDateAdministered())) {
            throw BusinessException.badRequest("INVALID_DATE", "Next dose date cannot be before administered date");
        }
        
        vaccination.setNextDoseDate(request.getNextDoseDate());
        vaccination.setVeterinarian(request.getVeterinarian());
        vaccination.setClinic(request.getClinic());
        vaccination.setBatchNumber(request.getBatchNumber());
        vaccination.setManufacturer(request.getManufacturer());
        vaccination.setProofUrl(request.getProofUrl());
        vaccination.setNotes(request.getNotes());
        
        if (request.getReminderActive() != null) {
            vaccination.setReminderActive(request.getReminderActive());
        }
        
        vaccination.setUpdatedAt(OffsetDateTime.now());

        Vaccination saved = vaccinationPort.save(vaccination);
        return mapToResponse(saved);
    }

    private VaccinationResponse mapToResponse(Vaccination vaccination) {
        VaccinationResponse response = new VaccinationResponse();
        response.setId(vaccination.getId());
        response.setPetId(vaccination.getPet().getId());
        response.setVaccineName(vaccination.getVaccineName());
        response.setDateAdministered(vaccination.getDateAdministered());
        response.setNextDoseDate(vaccination.getNextDoseDate());
        response.setVeterinarian(vaccination.getVeterinarian());
        response.setClinic(vaccination.getClinic());
        response.setBatchNumber(vaccination.getBatchNumber());
        response.setManufacturer(vaccination.getManufacturer());
        response.setProofUrl(vaccination.getProofUrl());
        response.setNotes(vaccination.getNotes());
        response.setReminderActive(vaccination.isReminderActive());
        response.setCreatedAt(vaccination.getCreatedAt());
        return response;
    }
}
