package com.petlife.modules.veterinarian.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petlife.modules.auth.entity.User;
import com.petlife.modules.auth.repository.UserRepository;
import com.petlife.modules.veterinarian.entity.AvailabilityStatus;
import com.petlife.modules.veterinarian.entity.Modality;
import com.petlife.modules.veterinarian.entity.Veterinarian;
import com.petlife.modules.veterinarian.infrastructure.dto.request.AddVetAddressRequest;
import com.petlife.modules.veterinarian.infrastructure.dto.request.CreateVeterinarianRequest;
import com.petlife.modules.veterinarian.infrastructure.dto.request.SetVetScheduleRequest;
import com.petlife.modules.veterinarian.infrastructure.dto.request.UpdateAvailabilityRequest;
import com.petlife.modules.veterinarian.infrastructure.dto.request.UpdateVetAddressRequest;
import com.petlife.modules.veterinarian.infrastructure.persistence.VeterinarianJpaRepository;
import com.petlife.modules.veterinarian.entity.VetAddress;
import com.petlife.modules.veterinarian.entity.VetFavorite;
import com.petlife.modules.veterinarian.infrastructure.persistence.VetAddressJpaRepository;
import com.petlife.modules.veterinarian.infrastructure.persistence.VetFavoriteJpaRepository;
import com.petlife.shared.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.LocalTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class VeterinarianControllerTest extends IntegrationTestBase {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VeterinarianJpaRepository veterinarianRepository;

    @Autowired
    private VetAddressJpaRepository vetAddressRepository;

    @Autowired
    private VetFavoriteJpaRepository vetFavoriteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Veterinarian testVet;

    @BeforeEach
    void setUp() {
        veterinarianRepository.deleteAll();
        
        testUser = userRepository.findByEmail("test_vet@petlife.com").orElseGet(() -> {
            User user = new User();
            user.setEmail("test_vet@petlife.com");
            user.setPasswordHash("hash");
            user.setName("Test Vet");
            return userRepository.save(user);
        });
    }

    private void createVetProfile() {
        testVet = new Veterinarian();
        testVet.setUser(testUser);
        testVet.setFullName("Test Vet");
        testVet.setCrmvState("SP");
        testVet.setCrmvNumber("12345");
        testVet.setModalities(java.util.List.of(Modality.CLINIC));
        testVet.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
        testVet = veterinarianRepository.save(testVet);
    }

    @Test
    void createProfile_ShouldReturnCreated() throws Exception {
        CreateVeterinarianRequest request = new CreateVeterinarianRequest();
        request.setCrmvState("SP");
        request.setCrmvNumber("CRMV-SP-12345");
        request.setFullName("Test Vet");
        request.setModalities(java.util.List.of(Modality.HOME_VISIT));

        mockMvc.perform(post("/api/v1/veterinarians")
                        .with(jwt().jwt(j -> j.subject(testUser.getId().toString()).claim("email", testUser.getEmail())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.crmvNumber").value("CRMV-SP-12345"));
    }

    @Test
    void addAddress_ShouldReturnCreated() throws Exception {
        createVetProfile();
        AddVetAddressRequest request = new AddVetAddressRequest("Clinica", "Rua A", "123", "", "Bairro B", "Cidade", "SP", "00000-000", true);

        mockMvc.perform(post("/api/v1/veterinarians/address")
                        .with(jwt().jwt(j -> j.subject(testUser.getId().toString()).claim("email", testUser.getEmail())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.street").value("Rua A"));
    }

    @Test
    void setSchedule_ShouldReturnCreated() throws Exception {
        createVetProfile();
        SetVetScheduleRequest request = new SetVetScheduleRequest(java.time.DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(18, 0), false);

        mockMvc.perform(post("/api/v1/veterinarians/schedule")
                        .with(jwt().jwt(j -> j.subject(testUser.getId().toString()).claim("email", testUser.getEmail())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.dayOfWeek").value("MONDAY"));
    }

    @Test
    void updateAvailability_ShouldReturnOk() throws Exception {
        createVetProfile();
        UpdateAvailabilityRequest request = new UpdateAvailabilityRequest(AvailabilityStatus.UNAVAILABLE, false);

        mockMvc.perform(patch("/api/v1/veterinarians/availability")
                        .with(jwt().jwt(j -> j.subject(testUser.getId().toString()).claim("email", testUser.getEmail())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.availabilityStatus").value("UNAVAILABLE"));
    }

    @Test
    void toggleFavorite_ShouldReturnOk() throws Exception {
        createVetProfile();

        mockMvc.perform(post("/api/v1/veterinarians/{veterinarianId}/favorite", testVet.getId())
                        .with(jwt().jwt(j -> j.subject(testUser.getId().toString()).claim("email", testUser.getEmail()))))
                .andExpect(status().isOk());
    }

    @Test
    void getProfile_ShouldReturnOk() throws Exception {
        createVetProfile();

        mockMvc.perform(get("/api/v1/veterinarians/{veterinarianId}", testVet.getId())
                        .with(jwt().jwt(j -> j.subject(testUser.getId().toString()).claim("email", testUser.getEmail()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(testVet.getId().toString()));
    }

    @Test
    void search_ShouldReturnOk() throws Exception {
        createVetProfile();

        mockMvc.perform(get("/api/v1/veterinarians/search")
                        .param("lat", "-23.5")
                        .param("lng", "-46.6")
                        .param("modality", "CLINIC")
                        .with(jwt().jwt(j -> j.subject(testUser.getId().toString()).claim("email", testUser.getEmail()))))
                .andExpect(status().isOk());
    }

    @Test
    void getMyProfile_ShouldReturnOk() throws Exception {
        createVetProfile();

        mockMvc.perform(get("/api/v1/veterinarians/me")
                        .with(jwt().jwt(j -> j.subject(testUser.getId().toString()).claim("email", testUser.getEmail()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(testVet.getId().toString()));
    }

    @Test
    void listFavorites_ShouldReturnOk() throws Exception {
        createVetProfile();
        VetFavorite favorite = new VetFavorite();
        favorite.setUser(testUser);
        favorite.setVeterinarian(testVet);
        vetFavoriteRepository.save(favorite);

        mockMvc.perform(get("/api/v1/veterinarians/favorites")
                        .with(jwt().jwt(j -> j.subject(testUser.getId().toString()).claim("email", testUser.getEmail()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(testVet.getId().toString()));
    }

    @Test
    void updateAddress_ShouldReturnOk() throws Exception {
        createVetProfile();
        VetAddress address = new VetAddress();
        address.setVeterinarian(testVet);
        address.setLabel("Clinica Old");
        address.setStreet("Rua Velha");
        address.setCity("Old City");
        address.setPostalCode("00000-000");
        address.setPrimary(false);
        address = vetAddressRepository.saveAndFlush(address);

        UpdateVetAddressRequest request = UpdateVetAddressRequest.builder()
                .label("Clinica Nova")
                .street("Rua Nova")
                .number("100")
                .neighborhood("Centro")
                .city("Nova")
                .state("SP")
                .postalCode("00000-000")
                .isPrimary(true)
                .build();

        mockMvc.perform(put("/api/v1/veterinarians/address/{id}", address.getId())
                        .with(jwt().jwt(j -> j.subject(testUser.getId().toString()).claim("email", testUser.getEmail())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.street").value("Rua Nova"));
    }

    @Test
    void deleteAddress_ShouldReturnNoContent() throws Exception {
        createVetProfile();
        VetAddress address = new VetAddress();
        address.setVeterinarian(testVet);
        address.setLabel("Deletar");
        address.setStreet("Rua para deletar");
        address.setCity("Delete City");
        address.setPostalCode("11111-111");
        address.setPrimary(false);
        address = vetAddressRepository.saveAndFlush(address);

        mockMvc.perform(delete("/api/v1/veterinarians/address/{id}", address.getId())
                        .with(jwt().jwt(j -> j.subject(testUser.getId().toString()).claim("email", testUser.getEmail()))))
                .andExpect(status().isOk());
    }
}
