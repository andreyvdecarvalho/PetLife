package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.auth.entity.User;
import com.petlife.modules.pet.application.port.ConsultationRepositoryPort;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.entity.Consultation;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.infrastructure.dto.ConsultationResponse;
import com.petlife.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListConsultationsByPetUseCaseTest {

    @Mock
    private ConsultationRepositoryPort consultationRepositoryPort;

    @Mock
    private PetRepositoryPort petRepositoryPort;

    @InjectMocks
    private ListConsultationsByPetUseCase useCase;

    private UUID userId;
    private UUID petId;
    private User user;
    private Pet pet;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        petId = UUID.randomUUID();

        user = new User();
        user.setId(userId);

        pet = new Pet();
        pet.setId(petId);
        pet.setUser(user);
    }

    @Test
    @DisplayName("Deve listar consultas de um pet com sucesso")
    void shouldListConsultationsSuccessfully() {
        // Arrange
        Consultation c1 = new Consultation();
        c1.setId(UUID.randomUUID());
        c1.setPet(pet);
        c1.setDate(OffsetDateTime.now());
        c1.setReason("Consulta 1");
        c1.setCreatedAt(OffsetDateTime.now());

        Consultation c2 = new Consultation();
        c2.setId(UUID.randomUUID());
        c2.setPet(pet);
        c2.setDate(OffsetDateTime.now().minusDays(1));
        c2.setReason("Consulta 2");
        c2.setCreatedAt(OffsetDateTime.now());

        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));
        when(consultationRepositoryPort.findAllByPetId(petId)).thenReturn(List.of(c1, c2));

        // Act
        List<ConsultationResponse> result = useCase.execute(petId, userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Consulta 1", result.get(0).getReason());
        assertEquals("Consulta 2", result.get(1).getReason());
    }

    @Test
    @DisplayName("Deve retornar lista vazia se pet não tem consultas")
    void shouldReturnEmptyListWhenPetHasNoConsultations() {
        // Arrange
        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));
        when(consultationRepositoryPort.findAllByPetId(petId)).thenReturn(new ArrayList<>());

        // Act
        List<ConsultationResponse> result = useCase.execute(petId, userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Deve lançar PET_NOT_FOUND se o pet não existir")
    void shouldThrowExceptionWhenPetNotFound() {
        // Arrange
        when(petRepositoryPort.findById(petId)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCase.execute(petId, userId));
        assertEquals("PET_NOT_FOUND", exception.getCode());
    }

    @Test
    @DisplayName("Deve lançar FORBIDDEN_PET_ACCESS se o pet não pertencer ao tutor")
    void shouldThrowExceptionWhenPetDoesNotBelongToUser() {
        // Arrange
        UUID anotherUserId = UUID.randomUUID();
        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCase.execute(petId, anotherUserId));
        assertEquals("FORBIDDEN_PET_ACCESS", exception.getCode());
    }
}
