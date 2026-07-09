package com.petlife.modules.pet.infrastructure.controller;

import com.petlife.modules.pet.application.usecase.CreateGroomingUseCase;
import com.petlife.modules.pet.application.usecase.ListGroomingsByPetUseCase;
import com.petlife.modules.pet.application.usecase.UpdateGroomingUseCase;
import com.petlife.modules.pet.application.usecase.UploadGroomingPhotoUseCase;
import com.petlife.modules.pet.infrastructure.dto.CreateGroomingRequest;
import com.petlife.modules.pet.infrastructure.dto.GroomingResponse;
import com.petlife.modules.pet.infrastructure.dto.UpdateGroomingRequest;
import com.petlife.shared.response.ApiResponse;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class GroomingController {

    private final CreateGroomingUseCase createGroomingUseCase;
    private final ListGroomingsByPetUseCase listGroomingsByPetUseCase;
    private final UpdateGroomingUseCase updateGroomingUseCase;
    private final UploadGroomingPhotoUseCase uploadGroomingPhotoUseCase;

    @PostMapping("/pets/{petId}/groomings")
    public ResponseEntity<ApiResponse<GroomingResponse>> createGrooming(
            @PathVariable UUID petId,
            @Valid @RequestBody CreateGroomingRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        GroomingResponse response = createGroomingUseCase.execute(petId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    @GetMapping("/pets/{petId}/groomings")
    public ResponseEntity<ApiResponse<List<GroomingResponse>>> listGroomings(
            @PathVariable UUID petId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        List<GroomingResponse> response = listGroomingsByPetUseCase.execute(petId, userId);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @PutMapping("/pets/{petId}/groomings/{id}")
    public ResponseEntity<ApiResponse<GroomingResponse>> updateGrooming(
            @PathVariable UUID petId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateGroomingRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        GroomingResponse response = updateGroomingUseCase.execute(petId, id, userId, request);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @PostMapping(
            value = "/pets/{petId}/groomings/{id}/photos",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<GroomingResponse>> uploadPhoto(
            @PathVariable UUID petId,
            @PathVariable UUID id,
            @RequestPart("file") MultipartFile file,
            @RequestParam("type") String type,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        GroomingResponse response = uploadGroomingPhotoUseCase.execute(petId, id, userId, file, type);
        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
