package com.petlife.modules.medication.application.usecase;

import com.petlife.modules.medication.application.port.MedicationAdministrationRepositoryPort;
import com.petlife.modules.medication.application.port.MedicationRepositoryPort;
import com.petlife.modules.medication.domain.entity.Medication;
import com.petlife.modules.medication.domain.entity.MedicationAdministration;
import com.petlife.modules.medication.domain.entity.MedicationAdministrationStatus;
import com.petlife.modules.medication.domain.entity.MedicationFrequency;
import com.petlife.modules.medication.domain.entity.MedicationStatus;
import com.petlife.modules.medication.infrastructure.dto.MedicationResponse;
import com.petlife.modules.medication.infrastructure.dto.UpdateMedicationRequest;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.auth.entity.User;
import com.petlife.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateMedicationUseCaseTest {

    @Mock
    private MedicationRepositoryPort medicationRepository;

    @Mock
    private MedicationAdministrationRepositoryPort administrationRepository;

    @Mock
    private PetRepositoryPort petRepository;

    @InjectMocks
    private UpdateMedicationUseCase updateMedicationUseCase;

    private UUID userId;
    private UUID petId;
    private UUID medId;
    private User user;
    private Pet pet;
    private Medication medication;

    @Captor
    private ArgumentCaptor<List<MedicationAdministration>> adminsCaptor;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        petId = UUID.randomUUID();
        medId = UUID.randomUUID();

        user = new User();
        user.setId(userId);
        user.setTimezone(null); // defaults to America/Sao_Paulo

        pet = new Pet();
        pet.setId(petId);
        pet.setUser(user);

        medication = new Medication();
        medication.setId(medId);
        medication.setPet(pet);
        medication.setStatus(MedicationStatus.ACTIVE);
        medication.setFrequency(MedicationFrequency.DAILY);
        medication.setStartDate(LocalDate.now());
        medication.setEndDate(LocalDate.now().plusDays(10));
        medication.setTimesOfDay(List.of("08:00", "20:00"));
        
        List<MedicationAdministration> admins = new ArrayList<>();
        MedicationAdministration pendingAdmin = new MedicationAdministration();
        pendingAdmin.setStatus(MedicationAdministrationStatus.PENDING);
        pendingAdmin.setMedication(medication);
        admins.add(pendingAdmin);
        medication.setAdministrations(admins);
    }

    @Test
    void shouldUpdateMedicationAndKeepScheduleIfNoScheduleFieldsChanged() {
        UpdateMedicationRequest req = new UpdateMedicationRequest(
                "New Name", "New Dosage", null, null, null, null, null);

        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
        when(medicationRepository.findById(medId)).thenReturn(Optional.of(medication));
        when(medicationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        MedicationResponse response = updateMedicationUseCase.execute(petId, medId, userId, req);

        assertEquals("New Name", response.name());
        assertEquals("New Dosage", response.dosage());
        
        verify(administrationRepository, never()).deleteAll(any());
        verify(administrationRepository, never()).saveAll(any());
    }

    @Test
    void shouldUpdateScheduleAndRecreateAdministrations() {
        UpdateMedicationRequest req = new UpdateMedicationRequest(
                null, null, MedicationFrequency.WEEKLY, null, null, LocalDate.now().plusDays(20), List.of("10:00"));

        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
        when(medicationRepository.findById(medId)).thenReturn(Optional.of(medication));
        when(medicationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        MedicationResponse response = updateMedicationUseCase.execute(petId, medId, userId, req);

        assertEquals(MedicationFrequency.WEEKLY, response.frequency());
        assertEquals(LocalDate.now().plusDays(20), response.endDate());
        assertEquals(List.of("10:00"), response.timesOfDay());

        verify(administrationRepository).deleteAll(any());
        verify(administrationRepository).saveAll(adminsCaptor.capture());
        assertFalse(adminsCaptor.getValue().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenPetNotFound() {
        UpdateMedicationRequest req = new UpdateMedicationRequest("A", "B", null, null, null, null, null);
        when(petRepository.findById(petId)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> 
                updateMedicationUseCase.execute(petId, medId, userId, req));
    }

    @Test
    void shouldThrowExceptionWhenPetBelongsToAnotherUser() {
        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());
        pet.setUser(anotherUser);
        
        UpdateMedicationRequest req = new UpdateMedicationRequest("A", "B", null, null, null, null, null);
        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));

        assertThrows(BusinessException.class, () -> 
                updateMedicationUseCase.execute(petId, medId, userId, req));
    }

    @Test
    void shouldThrowExceptionWhenMedicationNotFound() {
        UpdateMedicationRequest req = new UpdateMedicationRequest("A", "B", null, null, null, null, null);
        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
        when(medicationRepository.findById(medId)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> 
                updateMedicationUseCase.execute(petId, medId, userId, req));
    }

    @Test
    void shouldThrowExceptionWhenMedicationPetMismatch() {
        Pet anotherPet = new Pet();
        anotherPet.setId(UUID.randomUUID());
        medication.setPet(anotherPet);

        UpdateMedicationRequest req = new UpdateMedicationRequest("A", "B", null, null, null, null, null);
        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
        when(medicationRepository.findById(medId)).thenReturn(Optional.of(medication));

        assertThrows(BusinessException.class, () -> 
                updateMedicationUseCase.execute(petId, medId, userId, req));
    }

    @Test
    void shouldThrowExceptionWhenMedicationNotActive() {
        medication.setStatus(MedicationStatus.CANCELLED);

        UpdateMedicationRequest req = new UpdateMedicationRequest("A", "B", null, null, null, null, null);
        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
        when(medicationRepository.findById(medId)).thenReturn(Optional.of(medication));

        assertThrows(BusinessException.class, () -> 
                updateMedicationUseCase.execute(petId, medId, userId, req));
    }

    @Test
    void shouldThrowExceptionWhenCustomFrequencyLacksHours() {
        UpdateMedicationRequest req = new UpdateMedicationRequest(
                null, null, MedicationFrequency.CUSTOM, null, null, null, null);

        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
        when(medicationRepository.findById(medId)).thenReturn(Optional.of(medication));

        assertThrows(BusinessException.class, () -> 
                updateMedicationUseCase.execute(petId, medId, userId, req));
    }
    
    @Test
    void shouldGenerateCustomFrequency() {
        UpdateMedicationRequest req = new UpdateMedicationRequest(
                null, null, MedicationFrequency.CUSTOM, null, 12, LocalDate.now().plusDays(2), List.of("10:00"));

        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
        when(medicationRepository.findById(medId)).thenReturn(Optional.of(medication));
        when(medicationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        updateMedicationUseCase.execute(petId, medId, userId, req);
        verify(administrationRepository).saveAll(adminsCaptor.capture());
        assertFalse(adminsCaptor.getValue().isEmpty());
    }

    @Test
    void shouldGenerateOnceFrequency() {
        UpdateMedicationRequest req = new UpdateMedicationRequest(
                null, null, MedicationFrequency.ONCE, null, null, null, List.of("23:59"));

        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
        when(medicationRepository.findById(medId)).thenReturn(Optional.of(medication));
        when(medicationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        updateMedicationUseCase.execute(petId, medId, userId, req);
        verify(administrationRepository).saveAll(adminsCaptor.capture());
    }
}
