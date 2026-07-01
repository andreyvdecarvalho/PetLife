package com.petlife.modules.pet.controller;

import com.petlife.modules.auth.entity.User;
import com.petlife.modules.auth.entity.UserPlan;
import com.petlife.modules.auth.repository.UserRepository;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.entity.PetSex;
import com.petlife.modules.pet.entity.PetSpecies;
import com.petlife.modules.pet.infrastructure.dto.CreatePetRequest;
import com.petlife.shared.IntegrationTestBase;
import com.petlife.shared.factories.PetFactory;
import com.petlife.shared.factories.UserFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("PetController Integration Tests")
class PetControllerTest extends IntegrationTestBase {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetRepositoryPort petRepository;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    @Nested
    @DisplayName("POST /api/v1/pets")
    class CreatePet {

        @Test
        @DisplayName("Deve cadastrar pet com sucesso para usuário autenticado")
        void shouldCreatePetWithValidData() throws Exception {
            User user = UserFactory.make();
            userRepository.save(user);

            var request = new CreatePetRequest(
                    "Bolinha",
                    PetSpecies.DOG,
                    "Poodle",
                    PetSex.MALE,
                    LocalDate.of(2022, 5, 10),
                    BigDecimal.valueOf(5.4),
                    null,
                    false,
                    null,
                    null,
                    null
            );

            mockMvc.perform(post("/api/v1/pets")
                            .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.name").value("Bolinha"))
                    .andExpect(jsonPath("$.data.species").value("DOG"))
                    .andExpect(jsonPath("$.data.breed").value("Poodle"))
                    .andExpect(jsonPath("$.data.sex").value("MALE"));
        }

        @Test
        @DisplayName("Deve retornar 422 ao cadastrar pet sem nome")
        void shouldReturn422WhenNameIsMissing() throws Exception {
            User user = UserFactory.make();
            userRepository.save(user);

            var request = new CreatePetRequest(
                    "",
                    PetSpecies.DOG,
                    "Poodle",
                    PetSex.MALE,
                    LocalDate.of(2022, 5, 10),
                    null,
                    null,
                    false,
                    null,
                    null,
                    null
            );

            mockMvc.perform(post("/api/v1/pets")
                            .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.error.details[0].field").value("name"));
        }

        @Test
        @DisplayName("Deve retornar 400 se usuário Free tentar cadastrar o 3º pet")
        void shouldReturn400WhenFreePlanLimitExceeded() throws Exception {
            User user = UserFactory.make(u -> u.setPlan(UserPlan.FREE));
            userRepository.save(user);

            Pet pet1 = PetFactory.make(p -> p.setUser(user));
            Pet pet2 = PetFactory.make(p -> p.setUser(user));
            petRepository.save(pet1);
            petRepository.save(pet2);

            var request = new CreatePetRequest(
                    "Tercio",
                    PetSpecies.CAT,
                    "SRD",
                    PetSex.FEMALE,
                    LocalDate.of(2023, 1, 1),
                    null,
                    null,
                    false,
                    null,
                    null,
                    null
            );

            mockMvc.perform(post("/api/v1/pets")
                            .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error.code").value("PET_LIMIT_REACHED"));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/pets/{id}/photo")
    class UploadPhoto {

        @Test
        @DisplayName("Deve fazer upload de foto com sucesso")
        void shouldUploadPhotoSuccessfully() throws Exception {
            User user = UserFactory.make();
            userRepository.save(user);

            Pet pet = PetFactory.make(p -> p.setUser(user));
            petRepository.save(pet);

            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "pet.jpg",
                    MediaType.IMAGE_JPEG_VALUE,
                    "fake-image-bytes".getBytes()
            );

            mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/pets/{id}/photo", pet.getId())
                            .file(file)
                            .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail()))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.photoUrl").exists())
                    .andExpect(jsonPath("$.data.photoUrl").value(org.hamcrest.Matchers.containsString("pets")));
        }

        @Test
        @DisplayName("Deve retornar 403 se tentar atualizar foto de pet de outro usuário")
        void shouldReturn403WhenPetBelongsToOtherUser() throws Exception {
            User user = UserFactory.make();
            User otherUser = UserFactory.make();
            userRepository.save(user);
            userRepository.save(otherUser);

            Pet otherPet = PetFactory.make(p -> p.setUser(otherUser));
            petRepository.save(otherPet);

            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "pet.jpg",
                    MediaType.IMAGE_JPEG_VALUE,
                    "fake-image-bytes".getBytes()
            );

            mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/pets/{id}/photo", otherPet.getId())
                            .file(file)
                            .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail()))))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error.code").value("FORBIDDEN_PET_ACCESS"));
        }

        @Test
        @DisplayName("Deve retornar 400 se a foto exceder 500KB")
        void shouldReturn400WhenPhotoIsTooLarge() throws Exception {
            User user = UserFactory.make();
            userRepository.save(user);

            Pet pet = PetFactory.make(p -> p.setUser(user));
            petRepository.save(pet);

            byte[] largeBytes = new byte[501 * 1024]; // 501KB
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "pet.jpg",
                    MediaType.IMAGE_JPEG_VALUE,
                    largeBytes
            );

            mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/pets/{id}/photo", pet.getId())
                            .file(file)
                            .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail()))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error.code").value("FILE_TOO_LARGE"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/pets")
    class ListPets {

        @Test
        @DisplayName("Deve listar pets do usuário logado de forma paginada")
        void shouldListUserPets() throws Exception {
            User user = UserFactory.make();
            userRepository.save(user);

            Pet pet1 = PetFactory.make(p -> { p.setUser(user); p.setName("Pluto"); });
            Pet pet2 = PetFactory.make(p -> { p.setUser(user); p.setName("Mimi"); });
            petRepository.save(pet1);
            petRepository.save(pet2);

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/pets")
                            .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail()))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(2))
                    .andExpect(jsonPath("$.meta.page").value(0))
                    .andExpect(jsonPath("$.meta.total").value(2));
        }

        @Test
        @DisplayName("Deve filtrar pets arquivados na listagem")
        void shouldExcludeArchivedPets() throws Exception {
            User user = UserFactory.make();
            userRepository.save(user);

            Pet petActive = PetFactory.make(p -> { p.setUser(user); p.setName("Ativo"); p.setStatus(com.petlife.modules.pet.entity.PetStatus.ACTIVE); });
            Pet petArchived = PetFactory.make(p -> { p.setUser(user); p.setName("Arquivado"); p.setStatus(com.petlife.modules.pet.entity.PetStatus.ARCHIVED); });
            petRepository.save(petActive);
            petRepository.save(petArchived);

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/pets")
                            .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail()))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.length()").value(1))
                    .andExpect(jsonPath("$.data[0].name").value("Ativo"));
        }

        @Test
        @DisplayName("Deve retornar apenas pets arquivados quando filtrado por status ARCHIVED")
        void shouldListOnlyArchivedPets() throws Exception {
            User user = UserFactory.make();
            userRepository.save(user);

            Pet petActive = PetFactory.make(p -> { 
                p.setUser(user); 
                p.setName("Ativo"); 
                p.setStatus(com.petlife.modules.pet.entity.PetStatus.ACTIVE); 
            });
            Pet petArchived = PetFactory.make(p -> { 
                p.setUser(user); 
                p.setName("Arquivado"); 
                p.setStatus(com.petlife.modules.pet.entity.PetStatus.ARCHIVED); 
            });
            petRepository.save(petActive);
            petRepository.save(petArchived);

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/pets")
                            .param("status", "ARCHIVED")
                            .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail()))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.length()").value(1))
                    .andExpect(jsonPath("$.data[0].name").value("Arquivado"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/pets/{id}")
    class GetPetById {

        @Test
        @DisplayName("Deve obter detalhes do pet com sucesso")
        void shouldGetPetById() throws Exception {
            User user = UserFactory.make();
            userRepository.save(user);

            Pet pet = PetFactory.make(p -> { p.setUser(user); p.setName("Bidu"); });
            petRepository.save(pet);

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/pets/{id}", pet.getId())
                            .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail()))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.name").value("Bidu"));
        }

        @Test
        @DisplayName("Deve retornar 404 se o pet não existir")
        void shouldReturn404IfPetNotFound() throws Exception {
            User user = UserFactory.make();
            userRepository.save(user);

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/pets/{id}", UUID.randomUUID())
                            .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail()))))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error.code").value("PET_NOT_FOUND"));
        }

        @Test
        @DisplayName("Deve retornar 403 se o pet pertencer a outro usuário")
        void shouldReturn403IfPetBelongsToOtherUser() throws Exception {
            User user = UserFactory.make();
            User otherUser = UserFactory.make();
            userRepository.save(user);
            userRepository.save(otherUser);

            Pet otherPet = PetFactory.make(p -> p.setUser(otherUser));
            petRepository.save(otherPet);

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/pets/{id}", otherPet.getId())
                            .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail()))))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error.code").value("FORBIDDEN_PET_ACCESS"));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/pets/{id}")
    class UpdatePet {

        @Test
        @DisplayName("Deve atualizar dados do pet com sucesso")
        void shouldUpdatePet() throws Exception {
            User user = UserFactory.make();
            userRepository.save(user);

            Pet pet = PetFactory.make(p -> { p.setUser(user); p.setName("Rex"); });
            petRepository.save(pet);

            var request = new com.petlife.modules.pet.infrastructure.dto.UpdatePetRequest(
                    "Bob",
                    com.petlife.modules.pet.entity.PetSpecies.DOG,
                    "Labrador",
                    com.petlife.modules.pet.entity.PetSex.MALE,
                    java.time.LocalDate.now().minusYears(1),
                    new java.math.BigDecimal("20.0"),
                    com.petlife.modules.pet.entity.PetSize.LARGE,
                    true,
                    "981023",
                    "Poeira",
                    "Nenhuma"
            );

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/pets/{id}", pet.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail()))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.name").value("Bob"))
                    .andExpect(jsonPath("$.data.breed").value("Labrador"))
                    .andExpect(jsonPath("$.data.weightKg").value(20.0));
        }

        @Test
        @DisplayName("Deve retornar 404 se o pet nao existir")
        void shouldReturn404IfPetNotFound() throws Exception {
            User user = UserFactory.make();
            userRepository.save(user);

            var request = new com.petlife.modules.pet.infrastructure.dto.UpdatePetRequest(
                    "Bob",
                    com.petlife.modules.pet.entity.PetSpecies.DOG,
                    "Labrador",
                    com.petlife.modules.pet.entity.PetSex.MALE,
                    java.time.LocalDate.now().minusYears(1),
                    new java.math.BigDecimal("20.0"),
                    com.petlife.modules.pet.entity.PetSize.LARGE,
                    true,
                    "981023",
                    "Poeira",
                    "Nenhuma"
            );

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/pets/{id}", UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail()))))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error.code").value("PET_NOT_FOUND"));
        }

        @Test
        @DisplayName("Deve retornar 403 se o pet pertencer a outro tutor")
        void shouldReturn403IfPetBelongsToOtherUser() throws Exception {
            User user = UserFactory.make();
            User otherUser = UserFactory.make();
            userRepository.save(user);
            userRepository.save(otherUser);

            Pet otherPet = PetFactory.make(p -> p.setUser(otherUser));
            petRepository.save(otherPet);

            var request = new com.petlife.modules.pet.infrastructure.dto.UpdatePetRequest(
                    "Bob",
                    com.petlife.modules.pet.entity.PetSpecies.DOG,
                    "Labrador",
                    com.petlife.modules.pet.entity.PetSex.MALE,
                    java.time.LocalDate.now().minusYears(1),
                    new java.math.BigDecimal("20.0"),
                    com.petlife.modules.pet.entity.PetSize.LARGE,
                    true,
                    "981023",
                    "Poeira",
                    "Nenhuma"
            );

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/pets/{id}", otherPet.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail()))))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error.code").value("FORBIDDEN_PET_ACCESS"));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/pets/{id}/status")
    class UpdatePetStatus {

        @Test
        @DisplayName("Deve alterar status do pet com sucesso")
        void shouldUpdatePetStatus() throws Exception {
            User user = UserFactory.make();
            userRepository.save(user);

            Pet pet = PetFactory.make(p -> { 
                p.setUser(user); 
                p.setStatus(com.petlife.modules.pet.entity.PetStatus.ACTIVE); 
            });
            petRepository.save(pet);

            var request = new com.petlife.modules.pet.infrastructure.dto.UpdatePetStatusRequest(
                    com.petlife.modules.pet.entity.PetStatus.ARCHIVED
            );

            mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/pets/{id}/status", pet.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail()))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.status").value("ARCHIVED"));
        }

        @Test
        @DisplayName("Deve retornar 404 se o pet nao existir")
        void shouldReturn404IfPetNotFound() throws Exception {
            User user = UserFactory.make();
            userRepository.save(user);

            var request = new com.petlife.modules.pet.infrastructure.dto.UpdatePetStatusRequest(
                    com.petlife.modules.pet.entity.PetStatus.ARCHIVED
            );

            mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/pets/{id}/status", UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail()))))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error.code").value("PET_NOT_FOUND"));
        }

        @Test
        @DisplayName("Deve retornar 403 se o pet pertencer a outro tutor")
        void shouldReturn403IfPetBelongsToOtherUser() throws Exception {
            User user = UserFactory.make();
            User otherUser = UserFactory.make();
            userRepository.save(user);
            userRepository.save(otherUser);

            Pet otherPet = PetFactory.make(p -> p.setUser(otherUser));
            petRepository.save(otherPet);

            var request = new com.petlife.modules.pet.infrastructure.dto.UpdatePetStatusRequest(
                    com.petlife.modules.pet.entity.PetStatus.ARCHIVED
            );

            mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/pets/{id}/status", otherPet.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail()))))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error.code").value("FORBIDDEN_PET_ACCESS"));
        }
    }
}
