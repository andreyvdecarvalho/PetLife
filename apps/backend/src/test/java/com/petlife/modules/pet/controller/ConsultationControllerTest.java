package com.petlife.modules.pet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.pet.entity.Consultation;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.entity.PetSex;
import com.petlife.modules.pet.entity.PetSpecies;
import com.petlife.modules.pet.entity.PetStatus;
import com.petlife.modules.pet.infrastructure.dto.CreateConsultationRequest;
import com.petlife.modules.pet.infrastructure.persistence.ConsultationJpaRepository;
import com.petlife.modules.pet.infrastructure.persistence.PetJpaRepository;
import com.petlife.shared.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.OffsetDateTime;
import java.util.ArrayList;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ConsultationControllerTest extends IntegrationTestBase {

    @Autowired
    private UserRepositoryPort userRepository;

    @Autowired
    private PetJpaRepository petRepository;

    @Autowired
    private ConsultationJpaRepository consultationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Pet testPet;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        consultationRepository.deleteAll();
        petRepository.deleteAll();

        testUser = userRepository.findByEmail("test@petlife.com").orElseGet(() -> {
            User user = new User();
            user.setEmail("test@petlife.com");
            user.setPasswordHash("hash");
            user.setName("Test User");
            return userRepository.save(user);
        });

        testPet = new Pet();
        testPet.setUser(com.petlife.modules.auth.infrastructure.persistence.mapper.UserMapper.toJpaEntity(testUser));
        testPet.setName("Max");
        testPet.setSpecies(PetSpecies.DOG);
        testPet.setSex(PetSex.MALE);
        testPet.setStatus(PetStatus.ACTIVE);
        testPet = ((com.petlife.modules.pet.application.port.PetRepositoryPort) petRepository).save(testPet);
    }

    @Test
    @DisplayName("POST /api/v1/pets/{petId}/consultations - Deve criar consulta com sucesso")
    void shouldCreateConsultation() throws Exception {
        CreateConsultationRequest request = new CreateConsultationRequest();
        request.setDate(OffsetDateTime.now());
        request.setReason("Rotina");
        request.setVeterinarian("Dr. House");

        mockMvc.perform(post("/api/v1/pets/{petId}/consultations", testPet.getId())
                .with(jwt().jwt(j -> j.subject(testUser.getId().toString()).claim("email", testUser.getEmail())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.reason").value("Rotina"))
                .andExpect(jsonPath("$.data.veterinarian").value("Dr. House"));
    }

    @Test
    @DisplayName("POST /api/v1/pets/{petId}/consultations - Deve retornar 422 Unprocessable Entity quando reason estiver vazio")
    void shouldReturn422WhenReasonIsBlank() throws Exception {
        CreateConsultationRequest request = new CreateConsultationRequest();
        request.setDate(OffsetDateTime.now());
        request.setReason(""); // Inválido

        mockMvc.perform(post("/api/v1/pets/{petId}/consultations", testPet.getId())
                .with(jwt().jwt(j -> j.subject(testUser.getId().toString()).claim("email", testUser.getEmail())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(422));
    }

    @Test
    @DisplayName("GET /api/v1/pets/{petId}/consultations - Deve listar consultas do pet")
    void shouldListConsultations() throws Exception {
        Consultation consultation = new Consultation();
        consultation.setPet(testPet);
        consultation.setDate(OffsetDateTime.now());
        consultation.setReason("Consulta teste");
        consultation.setCreatedAt(OffsetDateTime.now());
        consultationRepository.save(consultation);

        mockMvc.perform(get("/api/v1/pets/{petId}/consultations", testPet.getId())
                .with(jwt().jwt(j -> j.subject(testUser.getId().toString()).claim("email", testUser.getEmail()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].reason").value("Consulta teste"));
    }

    @Test
    @DisplayName("POST /api/v1/pets/{petId}/consultations/{id}/attachments - Deve fazer upload de anexo com sucesso")
    void shouldUploadAttachment() throws Exception {
        Consultation consultation = new Consultation();
        consultation.setPet(testPet);
        consultation.setDate(OffsetDateTime.now());
        consultation.setReason("Consulta para anexo");
        consultation.setCreatedAt(OffsetDateTime.now());
        consultation = consultationRepository.save(consultation);

        MockMultipartFile file = new MockMultipartFile("files", "exame.pdf", "application/pdf", "pdf content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, "/api/v1/pets/{petId}/consultations/{id}/attachments", testPet.getId(), consultation.getId())
                .file(file)
                .with(jwt().jwt(j -> j.subject(testUser.getId().toString()).claim("email", testUser.getEmail()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.attachments").isArray())
                .andExpect(jsonPath("$.data.attachments[0]").value(org.hamcrest.Matchers.containsString("exame.pdf")));
    }

    @Test
    @DisplayName("DELETE /api/v1/pets/{petId}/consultations/{id}/attachments/{index} - Deve remover anexo da consulta")
    void shouldDeleteAttachment() throws Exception {
        Consultation consultation = new Consultation();
        consultation.setPet(testPet);
        consultation.setDate(OffsetDateTime.now());
        consultation.setReason("Consulta para remover anexo");
        consultation.setAttachments(new ArrayList<>());
        consultation.getAttachments().add("https://s3.amazonaws.com/petlife/consultations/exame.pdf");
        consultation.setCreatedAt(OffsetDateTime.now());
        consultation = consultationRepository.save(consultation);

        mockMvc.perform(delete("/api/v1/pets/{petId}/consultations/{id}/attachments/{index}", testPet.getId(), consultation.getId(), 0)
                .with(jwt().jwt(j -> j.subject(testUser.getId().toString()).claim("email", testUser.getEmail()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.attachments").isEmpty());
    }
}
