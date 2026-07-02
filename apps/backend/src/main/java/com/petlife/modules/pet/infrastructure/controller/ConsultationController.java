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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.petlife.modules.pet.application.usecase.CreateConsultationUseCase;
import com.petlife.modules.pet.application.usecase.DeleteConsultationAttachmentUseCase;
import com.petlife.modules.pet.application.usecase.ListConsultationsByPetUseCase;
import com.petlife.modules.pet.application.usecase.UploadConsultationAttachmentUseCase;
import com.petlife.modules.pet.infrastructure.dto.ConsultationResponse;
import com.petlife.modules.pet.infrastructure.dto.CreateConsultationRequest;
import com.petlife.shared.response.ApiResponse;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ConsultationController {

    private final CreateConsultationUseCase createConsultationUseCase;
    private final ListConsultationsByPetUseCase listConsultationsByPetUseCase;
    private final UploadConsultationAttachmentUseCase uploadConsultationAttachmentUseCase;
    private final DeleteConsultationAttachmentUseCase deleteConsultationAttachmentUseCase;

    @PostMapping("/pets/{petId}/consultations")
    public ResponseEntity<ApiResponse<ConsultationResponse>> createConsultation(
            @PathVariable UUID petId,
            @Valid @RequestBody CreateConsultationRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        ConsultationResponse response = createConsultationUseCase.execute(petId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    @GetMapping("/pets/{petId}/consultations")
    public ResponseEntity<ApiResponse<List<ConsultationResponse>>> listConsultations(
            @PathVariable UUID petId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        List<ConsultationResponse> response = listConsultationsByPetUseCase.execute(petId, userId);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @PostMapping(
            value = "/pets/{petId}/consultations/{id}/attachments",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<ConsultationResponse>> uploadAttachments(
            @PathVariable UUID petId,
            @PathVariable UUID id,
            @RequestPart("files") List<MultipartFile> files,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        ConsultationResponse response = uploadConsultationAttachmentUseCase.execute(petId, id, userId, files);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @DeleteMapping("/pets/{petId}/consultations/{id}/attachments/{index}")
    public ResponseEntity<ApiResponse<ConsultationResponse>> deleteAttachment(
            @PathVariable UUID petId,
            @PathVariable UUID id,
            @PathVariable int index,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        ConsultationResponse response = deleteConsultationAttachmentUseCase.execute(petId, id, userId, index);
        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
