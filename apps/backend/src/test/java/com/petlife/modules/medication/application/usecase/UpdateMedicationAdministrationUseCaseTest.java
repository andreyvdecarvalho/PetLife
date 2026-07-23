package com.petlife.modules.medication.application.usecase;

import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.pet.domain.entity.Pet;
import com.petlife.modules.medication.application.port.MedicationAdministrationRepositoryPort;
import com.petlife.modules.medication.domain.entity.Medication;
import com.petlife.modules.medication.domain.entity.MedicationAdministration;
import com.petlife.modules.medication.domain.entity.MedicationAdministrationStatus;
import com.petlife.modules.medication.infrastructure.dto.MedicationAdministrationResponse;
import com.petlife.modules.medication.infrastructure.dto.UpdateAdministrationRequest;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UpdateMedicationAdministrationUseCaseTest {

    @Mock
    private MedicationAdministrationRepositoryPort administrationRepository;

    @InjectMocks
    private UpdateMedicationAdministrationUseCase updateMedicationAdministrationUseCase;

    private User user;
    private Pet pet;
    private Medication medication;
    private MedicationAdministration administration;

    @BeforeEach
    void setUp() {
        user = UserFactory.make(u -> u.setId(UUID.randomUUID()));
        pet = PetFactory.make(p -> {
            p.setId(UUID.randomUUID());
            p.setUser(user);
        });
        medication = MedicationFactory.makeMedication(pet, m -> m.setId(UUID.randomUUID()));
        administration = MedicationFactory.makeAdministration(medication, a -> a.setId(UUID.randomUUID()));
    }

    @Test
    @DisplayName("Deve marcar dose como tomada com sucesso")
    void shouldMarkDoseAsTaken() {
        UUID userId = user.getId();
        UpdateAdministrationRequest request = new UpdateAdministrationRequest(MedicationAdministrationStatus.TAKEN, null);

        given(administrationRepository.findById(administration.getId())).willReturn(Optional.of(administration));
        given(administrationRepository.save(any(MedicationAdministration.class))).willAnswer(invocation -> invocation.getArgument(0));

        MedicationAdministrationResponse response = updateMedicationAdministrationUseCase.execute(
                administration.getId(), userId, request
        );

        assertThat(response.status()).isEqualTo(MedicationAdministrationStatus.TAKEN);
        assertThat(response.administeredAt()).isNotNull();
        verify(administrationRepository).save(any(MedicationAdministration.class));
    }

    @Test
    @DisplayName("Deve marcar dose como pulada e salvar motivo")
    void shouldMarkDoseAsSkippedWithReason() {
        UUID userId = user.getId();
        UpdateAdministrationRequest request = new UpdateAdministrationRequest(MedicationAdministrationStatus.SKIPPED, "Pet vomitou");

        given(administrationRepository.findById(administration.getId())).willReturn(Optional.of(administration));
        given(administrationRepository.save(any(MedicationAdministration.class))).willAnswer(invocation -> invocation.getArgument(0));

        MedicationAdministrationResponse response = updateMedicationAdministrationUseCase.execute(
                administration.getId(), userId, request
        );

        assertThat(response.status()).isEqualTo(MedicationAdministrationStatus.SKIPPED);
        assertThat(response.skippedReason()).isEqualTo("Pet vomitou");
        assertThat(response.administeredAt()).isNull();
        verify(administrationRepository).save(any(MedicationAdministration.class));
    }

    @Test
    @DisplayName("Deve falhar ao tentar atualizar dose de pet de outro tutor")
    void shouldThrowWhenUpdatingDoseForOtherUserPet() {
        UUID otherUserId = UUID.randomUUID();
        UpdateAdministrationRequest request = new UpdateAdministrationRequest(MedicationAdministrationStatus.TAKEN, null);

        given(administrationRepository.findById(administration.getId())).willReturn(Optional.of(administration));

        assertThatThrownBy(() -> updateMedicationAdministrationUseCase.execute(
                administration.getId(), otherUserId, request
        ))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Este pet não pertence ao usuário autenticado.");
    }
}

