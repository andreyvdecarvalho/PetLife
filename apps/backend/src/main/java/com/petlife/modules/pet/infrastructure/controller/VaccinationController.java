package com.petlife.modules.pet.infrastructure.controller;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.petlife.modules.pet.application.usecase.AddVaccinationUseCase;
import com.petlife.modules.pet.application.usecase.DeleteVaccinationUseCase;
import com.petlife.modules.pet.application.usecase.GetVaccineSuggestionsUseCase;
import com.petlife.modules.pet.application.usecase.ListVaccinationsByPetUseCase;
import com.petlife.modules.pet.application.usecase.UpdateVaccinationUseCase;
import com.petlife.modules.pet.application.usecase.UploadVaccinationProofUseCase;
import com.petlife.modules.pet.infrastructure.dto.CreateVaccinationRequest;
import com.petlife.modules.pet.infrastructure.dto.UpdateVaccinationRequest;
import com.petlife.modules.pet.infrastructure.dto.VaccinationResponse;
import com.petlife.shared.response.ApiResponse;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class VaccinationController {

    private final AddVaccinationUseCase addVaccinationUseCase;
    private final ListVaccinationsByPetUseCase listVaccinationsByPetUseCase;
    private final UpdateVaccinationUseCase updateVaccinationUseCase;
    private final DeleteVaccinationUseCase deleteVaccinationUseCase;
    private final GetVaccineSuggestionsUseCase getVaccineSuggestionsUseCase;
    private final UploadVaccinationProofUseCase uploadVaccinationProofUseCase;

    @PostMapping("/pets/{petId}/vaccines")
    public ResponseEntity<ApiResponse<VaccinationResponse>> addVaccination(
            @PathVariable UUID petId,
            @Valid @RequestBody CreateVaccinationRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        VaccinationResponse response = addVaccinationUseCase.execute(petId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    @GetMapping("/pets/{petId}/vaccines")
    public ResponseEntity<ApiResponse<List<VaccinationResponse>>> listVaccinations(
            @PathVariable UUID petId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        List<VaccinationResponse> response = listVaccinationsByPetUseCase.execute(petId, userId);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @PutMapping("/pets/{petId}/vaccines/{vaccineId}")
    public ResponseEntity<ApiResponse<VaccinationResponse>> updateVaccination(
            @PathVariable UUID petId,
            @PathVariable UUID vaccineId,
            @Valid @RequestBody UpdateVaccinationRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        VaccinationResponse response = updateVaccinationUseCase.execute(petId, vaccineId, userId, request);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/pets/{petId}/vaccines/{vaccineId}")
    public ResponseEntity<ApiResponse<Void>> deleteVaccination(
            @PathVariable UUID petId,
            @PathVariable UUID vaccineId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        deleteVaccinationUseCase.execute(petId, vaccineId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(
            value = "/pets/{petId}/vaccines/{vaccineId}/proof",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<VaccinationResponse>> uploadProof(
            @PathVariable UUID petId,
            @PathVariable UUID vaccineId,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        VaccinationResponse response = uploadVaccinationProofUseCase.execute(petId, vaccineId, userId, file);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @GetMapping("/vaccines/suggestions")
    public ResponseEntity<ApiResponse<List<String>>> getSuggestions(@RequestParam("species") String species) {
        return ResponseEntity.ok(ApiResponse.of(getVaccineSuggestionsUseCase.execute(species)));
    }
}
