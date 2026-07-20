package com.petlife.modules.medication.application.usecase;

import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.medication.application.port.MedicationRepositoryPort;
import com.petlife.modules.medication.application.port.MedicationAdministrationRepositoryPort;
import com.petlife.modules.medication.domain.entity.Medication;
import com.petlife.modules.medication.domain.entity.MedicationStatus;
import com.petlife.modules.medication.infrastructure.dto.MedicationResponse;
import com.petlife.shared.exception.BusinessException;
import com.petlife.shared.factories.MedicationFactory;
import com.petlife.shared.factories.PetFactory;
import com.petlife.shared.factories.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StopMedicationUseCaseTest {

    @Mock
    private MedicationRepositoryPort medicationRepository;
    @Mock
    private MedicationAdministrationRepositoryPort administrationRepository;

    @InjectMocks
    private StopMedicationUseCase stopMedicationUseCase;

    private User user;
    private Pet pet;
    private Medication medication;

    @BeforeEach
    void setUp() {
        user = UserFactory.make(u -> u.setId(UUID.randomUUID()));
        pet = PetFactory.make(p -> {
            p.setId(UUID.randomUUID());
            p.setUser(com.petlife.modules.auth.infrastructure.persistence.mapper.UserMapper.toJpaEntity(user));
        });
        medication = MedicationFactory.makeMedication(pet, m -> m.setId(UUID.randomUUID()));
    }

    @Test
    @DisplayName("Deve parar tratamento com sucesso, cancelando doses futuras")
    void shouldStopMedicationAndCancelFutureDoses() {
        UUID userId = user.getId();

        given(medicationRepository.findById(medication.getId())).willReturn(Optional.of(medication));
        given(medicationRepository.save(any(Medication.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(administrationRepository.findByMedicationIdAndStatusAndScheduledTimeAfter(any(), any(), any()))
                .willReturn(List.of(MedicationFactory.makeAdministration(medication)));

        MedicationResponse response = stopMedicationUseCase.execute(medication.getId(), userId);

        assertThat(response.status()).isEqualTo(MedicationStatus.CANCELLED);
        verify(medicationRepository).save(any(Medication.class));
        verify(administrationRepository).deleteAll(any());
    }

    @Test
    @DisplayName("Deve lancar excecao ao tentar parar tratamento de pet de outro tutor")
    void shouldThrowWhenStoppingMedicationForOtherUserPet() {
        UUID otherUserId = UUID.randomUUID();

        given(medicationRepository.findById(medication.getId())).willReturn(Optional.of(medication));

        assertThatThrownBy(() -> stopMedicationUseCase.execute(medication.getId(), otherUserId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Este pet não pertence ao usuário autenticado.");
    }
}
