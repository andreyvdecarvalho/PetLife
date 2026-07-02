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
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
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

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class VaccinationController {

    private final AddVaccinationUseCase addVaccinationUseCase;
    private final ListVaccinationsByPetUseCase listVaccinationsByPetUseCase;
    private final UpdateVaccinationUseCase updateVaccinationUseCase;
    private final GetVaccineSuggestionsUseCase getVaccineSuggestionsUseCase;
    private final UploadVaccinationProofUseCase uploadVaccinationProofUseCase;


    @PostMapping("/pets/{petId}/vaccines")
    public ResponseEntity<ApiResponse<VaccinationResponse>> addVaccination(
            @PathVariable UUID petId, @Valid @RequestBody CreateVaccinationRequest request, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(addVaccinationUseCase.execute(petId, UUID.fromString(jwt.getSubject()), request)));
    }

    @GetMapping("/pets/{petId}/vaccines")
    public ResponseEntity<ApiResponse<List<VaccinationResponse>>> listVaccinations(
            @PathVariable UUID petId, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ApiResponse.of(listVaccinationsByPetUseCase.execute(petId, UUID.fromString(jwt.getSubject()))));
    }

    @PutMapping("/pets/{petId}/vaccines/{vaccineId}")
    public ResponseEntity<ApiResponse<VaccinationResponse>> updateVaccination(
            @PathVariable UUID petId, @PathVariable UUID vaccineId, @Valid @RequestBody UpdateVaccinationRequest request, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ApiResponse.of(updateVaccinationUseCase.execute(petId, vaccineId, UUID.fromString(jwt.getSubject()), request)));
    }

    @PostMapping(value = "/pets/{petId}/vaccines/{vaccineId}/proof", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<VaccinationResponse>> uploadProof(
            @PathVariable UUID petId, @PathVariable UUID vaccineId, @RequestPart("file") MultipartFile file, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ApiResponse.of(uploadVaccinationProofUseCase.execute(petId, vaccineId, UUID.fromString(jwt.getSubject()), file)));
    }

    @GetMapping("/vaccines/suggestions")
    public ResponseEntity<ApiResponse<List<String>>> getSuggestions(@RequestParam("species") String species) {
        return ResponseEntity.ok(ApiResponse.of(getVaccineSuggestionsUseCase.execute(species)));
    }
}
