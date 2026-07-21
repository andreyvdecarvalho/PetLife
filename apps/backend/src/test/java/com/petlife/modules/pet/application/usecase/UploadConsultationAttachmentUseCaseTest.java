package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.pet.application.port.ConsultationRepositoryPort;
import com.petlife.modules.pet.domain.entity.Consultation;
import com.petlife.modules.pet.domain.entity.Pet;
import com.petlife.modules.pet.infrastructure.dto.ConsultationResponse;
import com.petlife.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UploadConsultationAttachmentUseCaseTest {

    @Mock
    private ConsultationRepositoryPort consultationRepositoryPort;

    @InjectMocks
    private UploadConsultationAttachmentUseCase useCase;

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
        pet.setUser(user);

        consultation = new Consultation();
        consultation.setId(consultationId);
        consultation.setPet(pet);
        consultation.setAttachments(new ArrayList<>());
    }

    @Test
    @DisplayName("Deve anexar arquivos com sucesso à consulta")
    void shouldUploadAttachmentsSuccessfully() {
        // Arrange
        MultipartFile file1 = new MockMultipartFile("files", "doc1.pdf", "application/pdf", new byte[100]);
        MultipartFile file2 = new MockMultipartFile("files", "foto.png", "image/png", new byte[200]);

        when(consultationRepositoryPort.findById(consultationId)).thenReturn(Optional.of(consultation));
        when(consultationRepositoryPort.save(any(Consultation.class))).thenReturn(consultation);

        // Act
        ConsultationResponse response = useCase.execute(petId, consultationId, userId, List.of(file1, file2));

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getAttachments().size());
        assertTrue(response.getAttachments().get(0).contains("doc1.pdf"));
        assertTrue(response.getAttachments().get(1).contains("foto.png"));
        verify(consultationRepositoryPort).save(consultation);
    }

    @Test
    @DisplayName("Deve lançar exceção se um arquivo for maior que 2MB (2 * 1024 * 1024 bytes)")
    void shouldThrowExceptionWhenFileIsTooLarge() {
        // Arrange
        byte[] largeContent = new byte[2 * 1024 * 1024 + 1]; // 2MB + 1 byte
        MultipartFile largeFile = new MockMultipartFile("files", "large.pdf", "application/pdf", largeContent);

        when(consultationRepositoryPort.findById(consultationId)).thenReturn(Optional.of(consultation));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCase.execute(petId, consultationId, userId, List.of(largeFile)));
        assertEquals("FILE_TOO_LARGE", exception.getCode());
    }

    @Test
    @DisplayName("Deve lançar exceção se a extensão do arquivo não for JPEG, PNG ou PDF")
    void shouldThrowExceptionWhenFileTypeIsInvalid() {
        // Arrange
        MultipartFile invalidFile = new MockMultipartFile("files", "notes.txt", "text/plain", new byte[100]);

        when(consultationRepositoryPort.findById(consultationId)).thenReturn(Optional.of(consultation));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCase.execute(petId, consultationId, userId, List.of(invalidFile)));
        assertEquals("INVALID_FILE_TYPE", exception.getCode());
    }

    @Test
    @DisplayName("Deve lançar exceção se o upload exceder o limite de 5 anexos")
    void shouldThrowExceptionWhenAttachmentLimitExceeded() {
        // Arrange
        consultation.getAttachments().addAll(List.of("url1", "url2", "url3", "url4"));
        MultipartFile file1 = new MockMultipartFile("files", "doc1.pdf", "application/pdf", new byte[100]);
        MultipartFile file2 = new MockMultipartFile("files", "doc2.pdf", "application/pdf", new byte[100]);

        when(consultationRepositoryPort.findById(consultationId)).thenReturn(Optional.of(consultation));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCase.execute(petId, consultationId, userId, List.of(file1, file2)));
        assertEquals("ATTACHMENT_LIMIT_EXCEEDED", exception.getCode());
    }

    @Test
    @DisplayName("Deve lançar CONSULTATION_NOT_FOUND se a consulta não existir")
    void shouldThrowExceptionWhenConsultationNotFound() {
        // Arrange
        MultipartFile file1 = new MockMultipartFile("files", "doc1.pdf", "application/pdf", new byte[100]);
        when(consultationRepositoryPort.findById(consultationId)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCase.execute(petId, consultationId, userId, List.of(file1)));
        assertEquals("CONSULTATION_NOT_FOUND", exception.getCode());
    }

    @Test
    @DisplayName("Deve lançar FORBIDDEN_PET_ACCESS se o pet da consulta não pertencer ao usuário")
    void shouldThrowExceptionWhenUserDoesNotOwnPet() {
        // Arrange
        UUID otherUserId = UUID.randomUUID();
        MultipartFile file1 = new MockMultipartFile("files", "doc1.pdf", "application/pdf", new byte[100]);

        when(consultationRepositoryPort.findById(consultationId)).thenReturn(Optional.of(consultation));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCase.execute(petId, consultationId, otherUserId, List.of(file1)));
        assertEquals("FORBIDDEN_PET_ACCESS", exception.getCode());
    }
}

