package com.petlife.modules.pet.infrastructure.controller;

import com.petlife.modules.pet.application.usecase.CreateRoutineActivityUseCase;
import com.petlife.modules.pet.application.usecase.ListRoutineActivitiesByPetUseCase;
import com.petlife.modules.pet.application.usecase.UpdateRoutineActivityStatusUseCase;
import com.petlife.modules.pet.domain.entity.RoutineActivity;
import com.petlife.modules.pet.infrastructure.dto.CreateRoutineActivityRequest;
import com.petlife.modules.pet.infrastructure.dto.RoutineActivityResponse;
import com.petlife.modules.pet.infrastructure.dto.UpdateRoutineActivityStatusRequest;
import com.petlife.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Routine Activities", description = "Routine activities management endpoints")
@RequiredArgsConstructor
public class RoutineActivityController {

    private final CreateRoutineActivityUseCase createRoutineActivityUseCase;
    private final ListRoutineActivitiesByPetUseCase listRoutineActivitiesByPetUseCase;
    private final UpdateRoutineActivityStatusUseCase updateRoutineActivityStatusUseCase;

    @Operation(summary = "Create a new routine activity")
    @PostMapping("/pets/{petId}/activities")
    public ResponseEntity<ApiResponse<RoutineActivityResponse>> create(
            @PathVariable UUID petId,
            @Valid @RequestBody CreateRoutineActivityRequest request) {
        
        RoutineActivity activity = createRoutineActivityUseCase.execute(petId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(RoutineActivityResponse.fromEntity(activity)));
    }

    @Operation(summary = "List routine activities by pet")
    @GetMapping("/pets/{petId}/activities")
    public ResponseEntity<ApiResponse<List<RoutineActivityResponse>>> listByPet(
            @PathVariable UUID petId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        List<RoutineActivity> activities = listRoutineActivitiesByPetUseCase.execute(petId, date);
        List<RoutineActivityResponse> response = activities.stream()
                .map(RoutineActivityResponse::fromEntity)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @Operation(summary = "Update routine activity status")
    @PatchMapping("/activities/{id}/status")
    public ResponseEntity<ApiResponse<RoutineActivityResponse>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRoutineActivityStatusRequest request) {
        
        RoutineActivity activity = updateRoutineActivityStatusUseCase.execute(id, request.status());
        return ResponseEntity.ok(ApiResponse.of(RoutineActivityResponse.fromEntity(activity)));
    }
}
