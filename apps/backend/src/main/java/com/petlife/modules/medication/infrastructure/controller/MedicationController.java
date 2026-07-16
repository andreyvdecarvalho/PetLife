package com.petlife.modules.medication.infrastructure.controller;

import com.petlife.modules.medication.application.usecase.CreateMedicationUseCase;
import com.petlife.modules.medication.application.usecase.GetMedicationAdherenceUseCase;
import com.petlife.modules.medication.application.usecase.ListMedicationsUseCase;
import com.petlife.modules.medication.application.usecase.StopMedicationUseCase;
import com.petlife.modules.medication.application.usecase.UpdateMedicationAdministrationUseCase;
import com.petlife.modules.medication.application.usecase.UpdateMedicationUseCase;
import com.petlife.modules.medication.infrastructure.dto.AdherenceResponse;
import com.petlife.modules.medication.infrastructure.dto.CreateMedicationRequest;
import com.petlife.modules.medication.infrastructure.dto.MedicationAdministrationResponse;
import com.petlife.modules.medication.infrastructure.dto.MedicationResponse;
import com.petlife.modules.medication.infrastructure.dto.UpdateAdministrationRequest;
import com.petlife.modules.medication.infrastructure.dto.UpdateMedicationRequest;
import com.petlife.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MedicationController {

    private final CreateMedicationUseCase createMedicationUseCase;
    private final ListMedicationsUseCase listMedicationsUseCase;
    private final UpdateMedicationUseCase updateMedicationUseCase;
    private final UpdateMedicationAdministrationUseCase updateMedicationAdministrationUseCase;
    private final StopMedicationUseCase stopMedicationUseCase;
    private final GetMedicationAdherenceUseCase getMedicationAdherenceUseCase;

    @PostMapping("/pets/{petId}/medications")
    public ResponseEntity<ApiResponse<MedicationResponse>> create(
            @PathVariable UUID petId,
            @Valid @RequestBody CreateMedicationRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        MedicationResponse response = createMedicationUseCase.execute(petId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    @GetMapping("/pets/{petId}/medications")
    public ResponseEntity<ApiResponse<List<MedicationResponse>>> list(
            @PathVariable UUID petId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        List<MedicationResponse> response = listMedicationsUseCase.execute(petId, userId);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @PutMapping("/pets/{petId}/medications/{id}")
    public ResponseEntity<ApiResponse<MedicationResponse>> updateMedication(
            @PathVariable UUID petId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateMedicationRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        MedicationResponse response = updateMedicationUseCase.execute(petId, id, userId, request);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @PatchMapping("/medications/doses/{doseId}")
    public ResponseEntity<ApiResponse<MedicationAdministrationResponse>> updateDose(
            @PathVariable UUID doseId,
            @Valid @RequestBody UpdateAdministrationRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        MedicationAdministrationResponse response = updateMedicationAdministrationUseCase
                .execute(doseId, userId, request);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @PatchMapping("/medications/{id}/stop")
    public ResponseEntity<ApiResponse<MedicationResponse>> stop(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        MedicationResponse response = stopMedicationUseCase.execute(id, userId);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @GetMapping("/pets/{petId}/medications/adherence")
    public ResponseEntity<ApiResponse<AdherenceResponse>> getAdherence(
            @PathVariable UUID petId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        AdherenceResponse response = getMedicationAdherenceUseCase.execute(petId, userId);
        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
