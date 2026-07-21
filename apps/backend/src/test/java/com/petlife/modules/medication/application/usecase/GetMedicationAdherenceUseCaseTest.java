package com.petlife.modules.medication.application.usecase;

import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.domain.entity.Pet;
import com.petlife.modules.medication.application.port.MedicationAdministrationRepositoryPort;
import com.petlife.modules.medication.domain.entity.Medication;
import com.petlife.modules.medication.domain.entity.MedicationAdministration;
import com.petlife.modules.medication.domain.entity.MedicationAdministrationStatus;
import com.petlife.modules.medication.infrastructure.dto.AdherenceResponse;
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

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GetMedicationAdherenceUseCaseTest {

    @Mock
    private MedicationAdministrationRepositoryPort administrationRepository;

    @Mock
    private PetRepositoryPort petRepository;

    @InjectMocks
    private GetMedicationAdherenceUseCase getMedicationAdherenceUseCase;

    private User user;
    private Pet pet;
    private Medication medication;

    @BeforeEach
    void setUp() {
        user = UserFactory.make(u -> u.setId(UUID.randomUUID()));
        pet = PetFactory.make(p -> {
            p.setId(UUID.randomUUID());
            p.setUser(user);
        });
        medication = MedicationFactory.makeMedication(pet, m -> m.setId(UUID.randomUUID()));
    }

    @Test
    @DisplayName("Deve calcular corretamene a aderencia de doses")
    void shouldCalculateCorrectAdherence() {
        UUID petId = pet.getId();
        UUID userId = user.getId();

        MedicationAdministration doseTaken = MedicationFactory.makeAdministration(medication, a -> {
            a.setStatus(MedicationAdministrationStatus.TAKEN);
            a.setScheduledTime(OffsetDateTime.now(ZoneOffset.UTC).minusHours(5));
        });

        MedicationAdministration doseSkipped = MedicationFactory.makeAdministration(medication, a -> {
            a.setStatus(MedicationAdministrationStatus.SKIPPED);
            a.setScheduledTime(OffsetDateTime.now(ZoneOffset.UTC).minusHours(4));
        });

        MedicationAdministration doseLate = MedicationFactory.makeAdministration(medication, a -> {
            a.setStatus(MedicationAdministrationStatus.LATE);
            a.setScheduledTime(OffsetDateTime.now(ZoneOffset.UTC).minusHours(3));
        });

        MedicationAdministration dosePendingPast = MedicationFactory.makeAdministration(medication, a -> {
            a.setStatus(MedicationAdministrationStatus.PENDING);
            a.setScheduledTime(OffsetDateTime.now(ZoneOffset.UTC).minusHours(2));
        });

        MedicationAdministration dosePendingFuture = MedicationFactory.makeAdministration(medication, a -> {
            a.setStatus(MedicationAdministrationStatus.PENDING);
            a.setScheduledTime(OffsetDateTime.now(ZoneOffset.UTC).plusHours(2));
        });

        given(petRepository.findById(petId)).willReturn(Optional.of(pet));
        given(administrationRepository.findByMedicationPetEntityId(petId)).willReturn(
                List.of(doseTaken, doseSkipped, doseLate, dosePendingPast, dosePendingFuture)
        );

        AdherenceResponse response = getMedicationAdherenceUseCase.execute(petId, userId);

        // totalExpected = TAKEN (1) + SKIPPED (1) + LATE (1) + PENDING in the past (1) = 4
        // taken = 1
        // Adherence = (1 / 4) * 100 = 25.0%
        assertThat(response.adherenceRate()).isEqualTo(25.0);
        assertThat(response.totalDoses()).isEqualTo(5);
        assertThat(response.takenDoses()).isEqualTo(1);
        assertThat(response.skippedDoses()).isEqualTo(1);
        assertThat(response.lateDoses()).isEqualTo(1);
        assertThat(response.pendingDoses()).isEqualTo(2);
    }

    @Test
    @DisplayName("Deve retornar 100% de aderencia se nao houver doses esperadas")
    void shouldReturn100PercentIfNoExpectedDoses() {
        UUID petId = pet.getId();
        UUID userId = user.getId();

        given(petRepository.findById(petId)).willReturn(Optional.of(pet));
        given(administrationRepository.findByMedicationPetEntityId(petId)).willReturn(List.of());

        AdherenceResponse response = getMedicationAdherenceUseCase.execute(petId, userId);

        assertThat(response.adherenceRate()).isEqualTo(100.0);
        assertThat(response.totalDoses()).isZero();
    }

    @Test
    @DisplayName("Deve lancar excecao ao tentar ver aderencia de pet de outro usuario")
    void shouldThrowWhenViewingAdherenceForOtherUserPet() {
        UUID petId = pet.getId();
        UUID otherUserId = UUID.randomUUID();

        given(petRepository.findById(petId)).willReturn(Optional.of(pet));

        assertThatThrownBy(() -> getMedicationAdherenceUseCase.execute(petId, otherUserId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Este pet não pertence ao usuário autenticado.");
    }
}

