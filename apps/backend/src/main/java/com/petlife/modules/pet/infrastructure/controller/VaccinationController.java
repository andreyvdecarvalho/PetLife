package com.petlife.modules.pet.infrastructure.controller;

import com.petlife.modules.pet.application.usecase.AddVaccinationUseCase;
import com.petlife.modules.pet.application.usecase.GetVaccineSuggestionsUseCase;
import com.petlife.modules.pet.application.usecase.ListVaccinationsByPetUseCase;
import com.petlife.modules.pet.application.usecase.UpdateVaccinationUseCase;
import com.petlife.modules.pet.application.usecase.UploadVaccinationProofUseCase;
import com.petlife.modules.pet.infrastructure.dto.CreateVaccinationRequest;
import com.petlife.modules.pet.infrastructure.dto.UpdateVaccinationRequest;
import com.petlife.modules.pet.infrastructure.dto.VaccinationResponse;
import com.petlife.shared.response.ApiResponse;
import com.petlife.shared.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
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

@RestController
@RequestMapping("/api/v1")
public class VaccinationController {

    private final AddVaccinationUseCase addVaccinationUseCase;
    private final ListVaccinationsByPetUseCase listVaccinationsByPetUseCase;
    private final UpdateVaccinationUseCase updateVaccinationUseCase;
    private final GetVaccineSuggestionsUseCase getVaccineSuggestionsUseCase;
    private final UploadVaccinationProofUseCase uploadVaccinationProofUseCase;

    public VaccinationController(
            AddVaccinationUseCase addVaccinationUseCase,
            ListVaccinationsByPetUseCase listVaccinationsByPetUseCase,
            UpdateVaccinationUseCase updateVaccinationUseCase,
            GetVaccineSuggestionsUseCase getVaccineSuggestionsUseCase,
            UploadVaccinationProofUseCase uploadVaccinationProofUseCase) {
        this.addVaccinationUseCase = addVaccinationUseCase;
        this.listVaccinationsByPetUseCase = listVaccinationsByPetUseCase;
        this.updateVaccinationUseCase = updateVaccinationUseCase;
        this.getVaccineSuggestionsUseCase = getVaccineSuggestionsUseCase;
        this.uploadVaccinationProofUseCase = uploadVaccinationProofUseCase;
    }

    @PostMapping("/pets/{petId}/vaccines")
    public ResponseEntity<ApiResponse<VaccinationResponse>> addVaccination(
            @PathVariable UUID petId,
            @Valid @RequestBody CreateVaccinationRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        VaccinationResponse response = addVaccinationUseCase.execute(petId, UUID.fromString(jwt.getSubject()), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(response));
    }

    @GetMapping("/pets/{petId}/vaccines")
    public ResponseEntity<ApiResponse<List<VaccinationResponse>>> listVaccinations(
            @PathVariable UUID petId,
            @AuthenticationPrincipal Jwt jwt) {

        List<VaccinationResponse> response = listVaccinationsByPetUseCase.execute(petId, UUID.fromString(jwt.getSubject()));
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @PutMapping("/pets/{petId}/vaccines/{vaccineId}")
    public ResponseEntity<ApiResponse<VaccinationResponse>> updateVaccination(
            @PathVariable UUID petId,
            @PathVariable UUID vaccineId,
            @Valid @RequestBody UpdateVaccinationRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        VaccinationResponse response = updateVaccinationUseCase.execute(
                petId, vaccineId, UUID.fromString(jwt.getSubject()), request);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @PostMapping(value = "/pets/{petId}/vaccines/{vaccineId}/proof", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<VaccinationResponse>> uploadProof(
            @PathVariable UUID petId,
            @PathVariable UUID vaccineId,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt) {

        VaccinationResponse response = uploadVaccinationProofUseCase.execute(
                petId, vaccineId, UUID.fromString(jwt.getSubject()), file);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @GetMapping("/vaccines/suggestions")
    public ResponseEntity<ApiResponse<List<String>>> getSuggestions(
            @RequestParam("species") String species) {

        List<String> response = getVaccineSuggestionsUseCase.execute(species);
        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
