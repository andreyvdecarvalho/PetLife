package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.pet.application.port.ConsultationRepositoryPort;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteConsultationAttachmentUseCaseTest {

    @Mock
    private ConsultationRepositoryPort consultationRepositoryPort;

    @InjectMocks
    private DeleteConsultationAttachmentUseCase useCase;

    private UUID userId;
    private UUID petId;
    private UUID consultationId;
    private User user;
    private Pet pet;
    private Consultation consultation;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        petId = UUID.randomUUID();
        consultationId = UUID.randomUUID();

        user = new User();
        user.setId(userId);

        pet = new Pet();
        pet.setId(petId);
        pet.setUser(com.petlife.modules.auth.infrastructure.persistence.mapper.UserMapper.toJpaEntity(user));

        consultation = new Consultation();
        consultation.setId(consultationId);
        consultation.setPet(pet);
        consultation.setAttachments(new ArrayList<>(List.of("url1", "url2", "url3")));
    }

    @Test
    @DisplayName("Deve deletar anexo por index com sucesso")
    void shouldDeleteAttachmentSuccessfully() {
        // Arrange
        when(consultationRepositoryPort.findById(consultationId)).thenReturn(Optional.of(consultation));
        when(consultationRepositoryPort.save(any(Consultation.class))).thenReturn(consultation);

        // Act
        ConsultationResponse response = useCase.execute(petId, consultationId, userId, 1); // remove "url2"

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getAttachments().size());
        assertEquals("url1", response.getAttachments().get(0));
        assertEquals("url3", response.getAttachments().get(1));
        verify(consultationRepositoryPort).save(consultation);
    }

    @Test
    @DisplayName("Deve lançar exceção se o index estiver fora do limite")
    void shouldThrowExceptionWhenIndexIsOutOfBounds() {
        // Arrange
        when(consultationRepositoryPort.findById(consultationId)).thenReturn(Optional.of(consultation));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCase.execute(petId, consultationId, userId, 5));
        assertEquals("INVALID_ATTACHMENT_INDEX", exception.getCode());
    }

    @Test
    @DisplayName("Deve lançar exceção se o index for negativo")
    void shouldThrowExceptionWhenIndexIsNegative() {
        // Arrange
        when(consultationRepositoryPort.findById(consultationId)).thenReturn(Optional.of(consultation));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCase.execute(petId, consultationId, userId, -1));
        assertEquals("INVALID_ATTACHMENT_INDEX", exception.getCode());
    }

    @Test
    @DisplayName("Deve lançar CONSULTATION_NOT_FOUND se a consulta não existir")
    void shouldThrowExceptionWhenConsultationNotFound() {
        // Arrange
        when(consultationRepositoryPort.findById(consultationId)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCase.execute(petId, consultationId, userId, 0));
        assertEquals("CONSULTATION_NOT_FOUND", exception.getCode());
    }

    @Test
    @DisplayName("Deve lançar FORBIDDEN_PET_ACCESS se o pet da consulta não pertencer ao usuário")
    void shouldThrowExceptionWhenUserDoesNotOwnPet() {
        // Arrange
        UUID otherUserId = UUID.randomUUID();
        when(consultationRepositoryPort.findById(consultationId)).thenReturn(Optional.of(consultation));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCase.execute(petId, consultationId, otherUserId, 0));
        assertEquals("FORBIDDEN_PET_ACCESS", exception.getCode());
    }
}
