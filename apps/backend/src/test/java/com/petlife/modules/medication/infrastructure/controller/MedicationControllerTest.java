package com.petlife.modules.medication.infrastructure.controller;

import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.domain.entity.Pet;
import com.petlife.modules.medication.application.port.MedicationRepositoryPort;
import com.petlife.modules.medication.application.port.MedicationAdministrationRepositoryPort;
import com.petlife.modules.medication.domain.entity.*;
import com.petlife.modules.medication.infrastructure.dto.CreateMedicationRequest;
import com.petlife.modules.medication.infrastructure.dto.UpdateAdministrationRequest;
import com.petlife.modules.medication.domain.entity.MedicationType;
import com.petlife.shared.IntegrationTestBase;
import com.petlife.shared.factories.MedicationFactory;
import com.petlife.shared.factories.PetFactory;
import com.petlife.shared.factories.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("MedicationController Integration Tests")
class MedicationControllerTest extends IntegrationTestBase {

    @Autowired
    private UserRepositoryPort userRepository;

    @Autowired
    private PetRepositoryPort petRepository;

    @Autowired
    private MedicationRepositoryPort medicationRepository;

    @Autowired
    private MedicationAdministrationRepositoryPort administrationRepository;


    private User user;
    private Pet pet;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        user = UserFactory.make();
        user = userRepository.save(user);
        pet = PetFactory.make(p -> p.setUser(user));
        pet = petRepository.save(pet);
    }

    @Test
    @DisplayName("POST /api/v1/pets/{petId}/medications - Deve cadastrar medicamento")
    void shouldCreateMedication() throws Exception {
        CreateMedicationRequest request = new CreateMedicationRequest(
                "Ibuprofeno", "1 comprimido", MedicationFrequency.DAILY, MedicationType.MEDICINE, null,
                LocalDate.now(), LocalDate.now().plusDays(2), List.of("10:00")
        );

        mockMvc.perform(post("/api/v1/pets/{petId}/medications", pet.getId())
                        .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Ibuprofeno"))
                .andExpect(jsonPath("$.data.dosage").value("1 comprimido"))
                .andExpect(jsonPath("$.data.frequency").value("DAILY"));
    }

    @Test
    @DisplayName("GET /api/v1/pets/{petId}/medications - Deve listar medicamentos")
    void shouldListMedications() throws Exception {
        Medication med = MedicationFactory.makeMedication(pet);
        med = medicationRepository.save(med);

        mockMvc.perform(get("/api/v1/pets/{petId}/medications", pet.getId())
                        .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value(med.getName()));
    }

    @Test
    @DisplayName("PATCH /api/v1/medications/doses/{doseId} - Deve atualizar status da dose")
    void shouldUpdateDoseStatus() throws Exception {
        Medication med = MedicationFactory.makeMedication(pet);
        med = medicationRepository.save(med);

        MedicationAdministration dose = MedicationFactory.makeAdministration(med);
        dose = administrationRepository.save(dose);

        UpdateAdministrationRequest request = new UpdateAdministrationRequest(MedicationAdministrationStatus.TAKEN, null);

        mockMvc.perform(patch("/api/v1/medications/doses/{doseId}", dose.getId())
                        .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("TAKEN"))
                .andExpect(jsonPath("$.data.administeredAt").isNotEmpty());
    }

    @Test
    @DisplayName("PATCH /api/v1/medications/{id}/stop - Deve parar tratamento")
    void shouldStopMedication() throws Exception {
        Medication med = MedicationFactory.makeMedication(pet);
        med = medicationRepository.save(med);

        MedicationAdministration dose = MedicationFactory.makeAdministration(med, d -> {
            d.setStatus(MedicationAdministrationStatus.PENDING);
            d.setScheduledTime(OffsetDateTime.now(ZoneOffset.UTC).plusDays(1));
        });
        administrationRepository.save(dose);

        mockMvc.perform(patch("/api/v1/medications/{id}/stop", med.getId())
                        .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));
    }

    @Test
    @DisplayName("GET /api/v1/pets/{petId}/medications/adherence - Deve retornar aderência")
    void shouldGetAdherence() throws Exception {
        Medication med = MedicationFactory.makeMedication(pet);
        med = medicationRepository.save(med);

        MedicationAdministration dose = MedicationFactory.makeAdministration(med, d -> {
            d.setStatus(MedicationAdministrationStatus.TAKEN);
            d.setScheduledTime(OffsetDateTime.now(ZoneOffset.UTC).minusHours(1));
        });
        administrationRepository.save(dose);

        mockMvc.perform(get("/api/v1/pets/{petId}/medications/adherence", pet.getId())
                        .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.adherenceRate").value(100.0))
                .andExpect(jsonPath("$.data.takenDoses").value(1));
    }
}

