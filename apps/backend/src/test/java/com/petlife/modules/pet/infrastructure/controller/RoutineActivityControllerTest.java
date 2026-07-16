package com.petlife.modules.pet.infrastructure.controller;

import com.petlife.modules.pet.application.usecase.CreateRoutineActivityUseCase;
import com.petlife.modules.pet.application.usecase.ListRoutineActivitiesByPetUseCase;
import com.petlife.modules.pet.application.usecase.UpdateRoutineActivityStatusUseCase;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.entity.RoutineActivity;
import com.petlife.modules.pet.entity.RoutineActivityStatus;
import com.petlife.modules.pet.entity.RoutineActivityType;
import com.petlife.modules.pet.infrastructure.dto.CreateRoutineActivityRequest;
import com.petlife.modules.pet.infrastructure.dto.RoutineActivityResponse;
import com.petlife.modules.pet.infrastructure.dto.UpdateRoutineActivityStatusRequest;
import com.petlife.shared.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoutineActivityControllerTest {

    @Mock
    private CreateRoutineActivityUseCase createRoutineActivityUseCase;
    
    @Mock
    private ListRoutineActivitiesByPetUseCase listRoutineActivitiesByPetUseCase;
    
    @Mock
    private UpdateRoutineActivityStatusUseCase updateRoutineActivityStatusUseCase;

    @InjectMocks
    private RoutineActivityController controller;

    private UUID petId;
    private RoutineActivity activity;

    @BeforeEach
    void setUp() {
        petId = UUID.randomUUID();
        Pet pet = new Pet();
        pet.setId(petId);

        activity = new RoutineActivity();
        activity.setId(UUID.randomUUID());
        activity.setPet(pet);
        activity.setTitle("Morning Walk");
        activity.setActivityDate(LocalDate.now());
        activity.setActivityTime(LocalTime.of(8, 0));
        activity.setType(RoutineActivityType.WALK);
        activity.setStatus(RoutineActivityStatus.PENDING);
    }

    @Test
    void shouldCreateRoutineActivity() {
        CreateRoutineActivityRequest request = new CreateRoutineActivityRequest(
                "Morning Walk", "Desc", LocalDate.now(), LocalTime.of(8, 0), RoutineActivityType.WALK, RoutineActivityStatus.PENDING
        );
        when(createRoutineActivityUseCase.execute(petId, request)).thenReturn(activity);

        ResponseEntity<ApiResponse<RoutineActivityResponse>> response = controller.create(petId, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().data().title()).isEqualTo("Morning Walk");
    }

    @Test
    void shouldListRoutineActivities() {
        when(listRoutineActivitiesByPetUseCase.execute(petId, null)).thenReturn(List.of(activity));

        ResponseEntity<ApiResponse<List<RoutineActivityResponse>>> response = controller.listByPet(petId, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().data()).hasSize(1);
        assertThat(response.getBody().data().get(0).title()).isEqualTo("Morning Walk");
    }

    @Test
    void shouldUpdateRoutineActivityStatus() {
        UpdateRoutineActivityStatusRequest request = new UpdateRoutineActivityStatusRequest(RoutineActivityStatus.COMPLETED);
        activity.setStatus(RoutineActivityStatus.COMPLETED);
        when(updateRoutineActivityStatusUseCase.execute(activity.getId(), RoutineActivityStatus.COMPLETED)).thenReturn(activity);

        ResponseEntity<ApiResponse<RoutineActivityResponse>> response = controller.updateStatus(activity.getId(), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().data().status()).isEqualTo(RoutineActivityStatus.COMPLETED);
    }
}
