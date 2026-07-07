package com.petlife.modules.medication.application.usecase;

import com.petlife.modules.auth.entity.User;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.medication.application.port.MedicationRepositoryPort;
import com.petlife.modules.medication.application.port.MedicationAdministrationRepositoryPort;
import com.petlife.modules.medication.domain.entity.Medication;
import com.petlife.modules.medication.domain.entity.MedicationFrequency;
import com.petlife.modules.medication.infrastructure.dto.CreateMedicationRequest;
import com.petlife.modules.medication.infrastructure.dto.MedicationResponse;
import com.petlife.shared.exception.BusinessException;
import com.petlife.shared.factories.PetFactory;
import com.petlife.shared.factories.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateMedicationUseCaseTest {

    @Mock
    private MedicationRepositoryPort medicationRepository;
    @Mock
    private MedicationAdministrationRepositoryPort administrationRepository;
    @Mock
    private PetRepositoryPort petRepository;

    @InjectMocks
    private CreateMedicationUseCase createMedicationUseCase;

    private User user;
    private Pet pet;

    @BeforeEach
    void setUp() {
        user = UserFactory.make(u -> u.setId(UUID.randomUUID()));
        pet = PetFactory.make(p -> {
            p.setId(UUID.randomUUID());
            p.setUser(user);
        });
    }

    @Test
    @DisplayName("Deve cadastrar um medicamento com sucesso e projetar doses")
    void shouldCreateMedicationAndProjectDoses() {
        UUID petId = pet.getId();
        UUID userId = user.getId();
        CreateMedicationRequest request = new CreateMedicationRequest(
                "Dipirona", "5 gotas", MedicationFrequency.DAILY, null,
                LocalDate.now(), LocalDate.now().plusDays(2), List.of("08:00", "20:00")
        );

        given(petRepository.findById(petId)).willReturn(Optional.of(pet));
        given(medicationRepository.save(any(Medication.class))).willAnswer(invocation -> {
            Medication med = invocation.getArgument(0);
            med.setId(UUID.randomUUID());
            return med;
        });

        MedicationResponse response = createMedicationUseCase.execute(petId, userId, request);

        assertThat(response.name()).isEqualTo("Dipirona");
        assertThat(response.dosage()).isEqualTo("5 gotas");
        assertThat(response.frequency()).isEqualTo(MedicationFrequency.DAILY);
        verify(medicationRepository).save(any(Medication.class));
        verify(administrationRepository).saveAll(any());
    }

    @Test
    @DisplayName("Deve lancar excecao se pet nao pertence ao usuario")
    void shouldThrowIfPetDoesNotBelongToUser() {
        UUID petId = pet.getId();
        UUID otherUserId = UUID.randomUUID();
        CreateMedicationRequest request = new CreateMedicationRequest(
                "Dipirona", "5 gotas", MedicationFrequency.DAILY, null,
                LocalDate.now(), LocalDate.now().plusDays(2), List.of("08:00")
        );

        given(petRepository.findById(petId)).willReturn(Optional.of(pet));

        assertThatThrownBy(() -> createMedicationUseCase.execute(petId, otherUserId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Este pet não pertence ao usuário autenticado.");
    }
}
