package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.application.port.VaccinationPort;
import com.petlife.modules.pet.domain.entity.Pet;
import com.petlife.modules.pet.domain.entity.Vaccination;
import com.petlife.modules.pet.infrastructure.dto.VaccinationResponse;
import com.petlife.shared.exception.BusinessException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ListVaccinationsByPetUseCase {

    private final VaccinationPort vaccinationPort;
    private final PetRepositoryPort petPort;

    public ListVaccinationsByPetUseCase(VaccinationPort vaccinationPort, PetRepositoryPort petPort) {
        this.vaccinationPort = vaccinationPort;
        this.petPort = petPort;
    }

    public List<VaccinationResponse> execute(UUID petId, UUID userId) {
        Pet pet = petPort.findById(petId)
                .orElseThrow(() -> BusinessException.notFound("PET_NOT_FOUND", "Pet not found"));

        if (!pet.getUser().getId().equals(userId)) {
            throw BusinessException.forbidden("PET_FORBIDDEN", 
                    "You don't have permission to view vaccinations for this pet");
        }

        List<Vaccination> vaccinations = vaccinationPort.findByPetId(petId);

        return vaccinations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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
