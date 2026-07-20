package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.pet.application.port.ConsultationRepositoryPort;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.application.port.SaveWeightRecordPort;
import com.petlife.modules.pet.entity.Consultation;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.entity.WeightRecord;
import com.petlife.modules.pet.infrastructure.dto.CreateConsultationRequest;
import com.petlife.modules.pet.infrastructure.dto.ConsultationResponse;
import com.petlife.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateConsultationUseCaseTest {

    @Mock
    private ConsultationRepositoryPort consultationRepositoryPort;

    @Mock
    private PetRepositoryPort petRepositoryPort;

    @Mock
    private SaveWeightRecordPort saveWeightRecordPort;

    @InjectMocks
    private CreateConsultationUseCase useCase;

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
        pet.setUser(com.petlife.modules.auth.infrastructure.persistence.mapper.UserMapper.toJpaEntity(user));
        pet.setName("Rex");
        pet.setWeightKg(BigDecimal.valueOf(10.0));
    }

    @Test
    @DisplayName("Deve criar consulta com sucesso e retornar os dados salvos")
    void shouldCreateConsultationSuccessfully() {
        // Arrange
        CreateConsultationRequest request = new CreateConsultationRequest();
        request.setDate(OffsetDateTime.now());
        request.setReason("Rotina");
        request.setVeterinarian("Dr. Silva");
        request.setClinic("Clinipet");
        request.setDiagnosis("Saudável");
        request.setPrescriptions("Nenhuma");
        request.setNotes("Tudo certo");
        request.setCost(BigDecimal.valueOf(150.0));

        Consultation savedConsultation = new Consultation();
        savedConsultation.setId(UUID.randomUUID());
        savedConsultation.setPet(pet);
        savedConsultation.setDate(request.getDate());
        savedConsultation.setReason(request.getReason());
        savedConsultation.setVeterinarian(request.getVeterinarian());
        savedConsultation.setClinic(request.getClinic());
        savedConsultation.setDiagnosis(request.getDiagnosis());
        savedConsultation.setPrescriptions(request.getPrescriptions());
        savedConsultation.setNotes(request.getNotes());
        savedConsultation.setCost(request.getCost());
        savedConsultation.setCreatedAt(OffsetDateTime.now());

        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));
        when(consultationRepositoryPort.save(any(Consultation.class))).thenReturn(savedConsultation);

        // Act
        ConsultationResponse response = useCase.execute(petId, userId, request);

        // Assert
        assertNotNull(response);
        assertEquals(savedConsultation.getId(), response.getId());
        assertEquals(petId, response.getPetId());
        assertEquals("Rotina", response.getReason());
        assertEquals("Dr. Silva", response.getVeterinarian());
        verify(consultationRepositoryPort).save(any(Consultation.class));
    }

    @Test
    @DisplayName("Deve atualizar o peso do pet e salvar log de peso quando weightAtVisit for informado na consulta mais recente")
    void shouldUpdatePetWeightWhenWeightAtVisitIsProvided() {
        // Arrange
        CreateConsultationRequest request = new CreateConsultationRequest();
        request.setDate(OffsetDateTime.now());
        request.setReason("Rotina");
        request.setWeightAtVisit(BigDecimal.valueOf(12.5));

        Consultation savedConsultation = new Consultation();
        savedConsultation.setId(UUID.randomUUID());
        savedConsultation.setPet(pet);
        savedConsultation.setDate(request.getDate());
        savedConsultation.setReason(request.getReason());
        savedConsultation.setWeightAtVisit(request.getWeightAtVisit());
        savedConsultation.setCreatedAt(OffsetDateTime.now());

        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));
        // Para simular que é a consulta mais recente, retornamos uma lista vazia de consultas anteriores
        when(consultationRepositoryPort.findAllByPetId(petId)).thenReturn(Collections.emptyList());
        when(consultationRepositoryPort.save(any(Consultation.class))).thenReturn(savedConsultation);

        // Act
        useCase.execute(petId, userId, request);

        // Assert
        assertEquals(BigDecimal.valueOf(12.5), pet.getWeightKg());
        verify(petRepositoryPort).save(pet);

        ArgumentCaptor<WeightRecord> weightRecordCaptor = ArgumentCaptor.forClass(WeightRecord.class);
        verify(saveWeightRecordPort).save(weightRecordCaptor.capture());
        WeightRecord weightRecord = weightRecordCaptor.getValue();
        assertEquals(pet, weightRecord.getPet());
        assertEquals(BigDecimal.valueOf(12.5), weightRecord.getWeightKg());
    }

    @Test
    @DisplayName("NÃO deve atualizar o peso atual do pet se a consulta cadastrada for mais antiga que a consulta mais recente do pet")
    void shouldNotUpdatePetWeightWhenConsultationIsOlderThanLatest() {
        // Arrange
        OffsetDateTime now = OffsetDateTime.now();
        CreateConsultationRequest request = new CreateConsultationRequest();
        request.setDate(now.minusDays(5)); // Consulta de 5 dias atrás
        request.setReason("Rotina");
        request.setWeightAtVisit(BigDecimal.valueOf(15.0));

        // Consulta mais recente já cadastrada (de hoje)
        Consultation latestConsultation = new Consultation();
        latestConsultation.setDate(now);
        latestConsultation.setWeightAtVisit(BigDecimal.valueOf(10.0));

        Consultation savedConsultation = new Consultation();
        savedConsultation.setId(UUID.randomUUID());
        savedConsultation.setPet(pet);
        savedConsultation.setDate(request.getDate());
        savedConsultation.setReason(request.getReason());
        savedConsultation.setWeightAtVisit(request.getWeightAtVisit());

        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));
        when(consultationRepositoryPort.findAllByPetId(petId)).thenReturn(List.of(latestConsultation));
        when(consultationRepositoryPort.save(any(Consultation.class))).thenReturn(savedConsultation);

        // Act
        useCase.execute(petId, userId, request);

        // Assert
        assertEquals(BigDecimal.valueOf(10.0), pet.getWeightKg()); // Mantém o peso original do pet
        verify(petRepositoryPort, never()).save(pet); // Nunca atualiza o pet
        verify(saveWeightRecordPort).save(any(WeightRecord.class)); // Mas ainda registra o log de peso histórico!
    }

    @Test
    @DisplayName("Deve lançar PET_NOT_FOUND se o pet não existir")
    void shouldThrowExceptionWhenPetNotFound() {
        // Arrange
        CreateConsultationRequest request = new CreateConsultationRequest();
        request.setDate(OffsetDateTime.now());
        request.setReason("Rotina");

        when(petRepositoryPort.findById(petId)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCase.execute(petId, userId, request));
        assertEquals("PET_NOT_FOUND", exception.getCode());
    }

    @Test
    @DisplayName("Deve lançar FORBIDDEN_PET_ACCESS se o pet não pertencer ao tutor")
    void shouldThrowExceptionWhenPetDoesNotBelongToUser() {
        // Arrange
        CreateConsultationRequest request = new CreateConsultationRequest();
        request.setDate(OffsetDateTime.now());
        request.setReason("Rotina");
        UUID anotherUserId = UUID.randomUUID();

        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCase.execute(petId, anotherUserId, request));
        assertEquals("FORBIDDEN_PET_ACCESS", exception.getCode());
    }

    @Test
    @DisplayName("Deve lançar INVALID_DATE se a data de retorno (followUpDate) for anterior à data da consulta")
    void shouldThrowExceptionWhenFollowUpDateIsBeforeConsultationDate() {
        // Arrange
        CreateConsultationRequest request = new CreateConsultationRequest();
        request.setDate(OffsetDateTime.now());
        request.setReason("Rotina");
        request.setFollowUpDate(LocalDate.now().minusDays(1)); // Ontem

        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCase.execute(petId, userId, request));
        assertEquals("INVALID_DATE", exception.getCode());
    }
}
