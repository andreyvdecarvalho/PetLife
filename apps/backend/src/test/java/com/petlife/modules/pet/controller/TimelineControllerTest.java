package com.petlife.modules.pet.controller;

import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.auth.domain.entity.UserPlan;
import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.pet.domain.entity.Pet;
import com.petlife.modules.pet.domain.entity.PetSex;
import com.petlife.modules.pet.domain.entity.PetSpecies;
import com.petlife.modules.pet.domain.entity.PetStatus;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.shared.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TimelineControllerTest extends IntegrationTestBase {

    @Autowired
    private UserRepositoryPort userRepository;

    @Autowired
    private PetRepositoryPort petRepository;

    private User testUser;
    private Pet testPet;

    @BeforeEach
    void setUp() {
        // petRepository.deleteAll() is not in port, ignoring for test or we can just leave it to DB rollback

        testUser = userRepository.findByEmail("test@petlife.com").orElseGet(() -> {
            User user = new User();
            user.setEmail("test@petlife.com");
            user.setPasswordHash("hash");
            user.setName("Test User");
            user.setPlan(UserPlan.FREE);
            return userRepository.save(user);
        });

        testPet = new Pet();
        testPet.setUser(testUser);
        testPet.setName("Max");
        testPet.setSpecies(PetSpecies.DOG);
        testPet.setSex(PetSex.MALE);
        testPet.setStatus(PetStatus.ACTIVE);
        testPet.setBirthDate(LocalDate.now().minusYears(2));
        testPet = petRepository.save(testPet);
    }

    @Test
    void shouldGetTimeline() throws Exception {
        mockMvc.perform(get("/api/v1/pets/{petId}/timeline", testPet.getId())
                .with(jwt().jwt(j -> j.subject(testUser.getId().toString()).claim("email", testUser.getEmail()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].type").value("BIRTHDAY"));
    }

    @Test
    void shouldGetTimelineFilteredByTypes() throws Exception {
        mockMvc.perform(get("/api/v1/pets/{petId}/timeline", testPet.getId())
                .param("types", "VACCINE")
                .with(jwt().jwt(j -> j.subject(testUser.getId().toString()).claim("email", testUser.getEmail()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void shouldDenyExportForFreeUser() throws Exception {
        testUser.setPlan(UserPlan.FREE);
        userRepository.save(testUser);
        testPet.setUser(testUser);
        testPet = petRepository.save(testPet);

        mockMvc.perform(get("/api/v1/pets/{petId}/export", testPet.getId())
                .with(jwt().jwt(j -> j.subject(testUser.getId().toString()).claim("email", testUser.getEmail()))))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldExportPdfForPremiumUser() throws Exception {
        testUser.setPlan(UserPlan.PREMIUM);
        userRepository.save(testUser);
        testPet.setUser(testUser);
        testPet = petRepository.save(testPet);

        mockMvc.perform(get("/api/v1/pets/{petId}/export", testPet.getId())
                .with(jwt().jwt(j -> j.subject(testUser.getId().toString()).claim("email", testUser.getEmail()))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }
}

