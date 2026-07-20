package com.petlife.modules.pet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.entity.PetSex;
import com.petlife.modules.pet.entity.PetSpecies;
import com.petlife.modules.pet.entity.PetStatus;
import com.petlife.modules.pet.infrastructure.dto.CreateVaccinationRequest;
import com.petlife.modules.pet.infrastructure.persistence.PetJpaRepository;
import com.petlife.modules.pet.infrastructure.persistence.VaccinationRepository;
import com.petlife.shared.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class VaccinationControllerTest extends IntegrationTestBase {

    @Autowired
    private UserRepositoryPort userRepository;

    @Autowired
    private PetJpaRepository petRepository;

    @Autowired
    private VaccinationRepository vaccinationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Pet testPet;

    @BeforeEach
    void setUp() {
        vaccinationRepository.deleteAll();
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
    void shouldAddVaccination() throws Exception {
        CreateVaccinationRequest request = new CreateVaccinationRequest();
        request.setVaccineName("V10");
        request.setDateAdministered(LocalDate.now());

        mockMvc.perform(post("/api/v1/pets/{petId}/vaccines", testPet.getId())
                .with(jwt().jwt(j -> j.subject(testUser.getId().toString()).claim("email", testUser.getEmail())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.vaccineName").value("V10"));
    }

    @Test
    void shouldListVaccinations() throws Exception {
        mockMvc.perform(get("/api/v1/pets/{petId}/vaccines", testPet.getId())
                .with(jwt().jwt(j -> j.subject(testUser.getId().toString()).claim("email", testUser.getEmail()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void shouldGetVaccineSuggestions() throws Exception {
        mockMvc.perform(get("/api/v1/vaccines/suggestions").param("species", "DOG")
                .with(jwt().jwt(j -> j.subject(testUser.getId().toString()).claim("email", testUser.getEmail()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }
}
