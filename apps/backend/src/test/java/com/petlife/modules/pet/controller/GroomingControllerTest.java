package com.petlife.modules.pet.controller;

import com.petlife.modules.auth.entity.User;
import com.petlife.modules.auth.repository.UserRepository;
import com.petlife.modules.pet.entity.Grooming;
import com.petlife.modules.pet.entity.GroomingType;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.entity.PetSex;
import com.petlife.modules.pet.entity.PetSpecies;
import com.petlife.modules.pet.entity.PetStatus;
import com.petlife.modules.pet.infrastructure.dto.CreateGroomingRequest;
import com.petlife.modules.pet.infrastructure.dto.UpdateGroomingRequest;
import com.petlife.modules.pet.infrastructure.persistence.PetJpaRepository;
import com.petlife.modules.pet.infrastructure.persistence.JpaGroomingRepository;
import com.petlife.shared.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GroomingControllerTest extends IntegrationTestBase {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetJpaRepository petRepository;

    @Autowired
    private JpaGroomingRepository groomingRepository;

    private User testUser;
    private Pet testPet;

    @BeforeEach
    void setUp() {
        groomingRepository.deleteAll();
        petRepository.deleteAll();

        testUser = userRepository.findByEmail("test@petlife.com").orElseGet(() -> {
            User user = new User();
            user.setEmail("test@petlife.com");
            user.setPasswordHash("hash");
            user.setName("Test User");
            return userRepository.save(user);
        });

        testPet = new Pet();
        testPet.setUser(testUser);
        testPet.setName("Max");
        testPet.setSpecies(PetSpecies.DOG);
        testPet.setSex(PetSex.MALE);
        testPet.setStatus(PetStatus.ACTIVE);
        testPet = ((com.petlife.modules.pet.application.port.PetRepositoryPort) petRepository).save(testPet);
    }

    @Test
    void shouldCreateGrooming() throws Exception {
        CreateGroomingRequest request = new CreateGroomingRequest();
        request.setType(GroomingType.BATH);
        request.setDate(LocalDate.now());
        request.setProvider("Dog Spa");
        request.setCost(BigDecimal.valueOf(80.00));
        request.setFrequencyDays(15);
        request.setNotes("Smells good");

        mockMvc.perform(post("/api/v1/pets/{petId}/groomings", testPet.getId())
                .with(jwt().jwt(j -> j.subject(testUser.getId().toString()).claim("email", testUser.getEmail())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.type").value("BATH"))
                .andExpect(jsonPath("$.data.provider").value("Dog Spa"))
                .andExpect(jsonPath("$.data.nextDate").value(LocalDate.now().plusDays(15).toString()));
    }

    @Test
    void shouldListGroomings() throws Exception {
        Grooming grooming = new Grooming();
        grooming.setPet(testPet);
        grooming.setType(GroomingType.GROOMING);
        grooming.setDate(LocalDate.now());
        grooming.setProvider("Dog Spa");
        groomingRepository.save(grooming);

        mockMvc.perform(get("/api/v1/pets/{petId}/groomings", testPet.getId())
                .with(jwt().jwt(j -> j.subject(testUser.getId().toString()).claim("email", testUser.getEmail()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].type").value("GROOMING"));
    }

    @Test
    void shouldUpdateGrooming() throws Exception {
        Grooming grooming = new Grooming();
        grooming.setPet(testPet);
        grooming.setType(GroomingType.BATH);
        grooming.setDate(LocalDate.now());
        grooming = groomingRepository.save(grooming);

        UpdateGroomingRequest request = new UpdateGroomingRequest();
        request.setType(GroomingType.BATH_AND_GROOMING);
        request.setDate(LocalDate.now());
        request.setProvider("Updated Spa");
        request.setCost(BigDecimal.valueOf(150.00));

        mockMvc.perform(put("/api/v1/pets/{petId}/groomings/{id}", testPet.getId(), grooming.getId())
                .with(jwt().jwt(j -> j.subject(testUser.getId().toString()).claim("email", testUser.getEmail())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("BATH_AND_GROOMING"))
                .andExpect(jsonPath("$.data.provider").value("Updated Spa"));
    }

    @Test
    void shouldUploadPhoto() throws Exception {
        Grooming grooming = new Grooming();
        grooming.setPet(testPet);
        grooming.setType(GroomingType.BATH);
        grooming.setDate(LocalDate.now());
        grooming = groomingRepository.save(grooming);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "some-image-bytes".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/pets/{petId}/groomings/{id}/photos", testPet.getId(), grooming.getId())
                .file(file)
                .queryParam("type", "before")
                .with(jwt().jwt(j -> j.subject(testUser.getId().toString()).claim("email", testUser.getEmail()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.photos[0]").value(org.hamcrest.Matchers.startsWith("data:image/jpeg;base64,")));
    }
}
