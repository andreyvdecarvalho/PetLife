package com.petlife.modules.pet.infrastructure.controller;

import com.petlife.modules.pet.application.usecase.CreateConsultationUseCase;
import com.petlife.modules.pet.application.usecase.DeleteConsultationAttachmentUseCase;
import com.petlife.modules.pet.application.usecase.ListConsultationsByPetUseCase;
import com.petlife.modules.pet.application.usecase.UploadConsultationAttachmentUseCase;
import com.petlife.modules.pet.infrastructure.dto.ConsultationResponse;
import com.petlife.modules.pet.infrastructure.dto.CreateConsultationRequest;
import com.petlife.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

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
            @PathVariable UUID petId, @Valid @RequestBody CreateConsultationRequest request, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(createConsultationUseCase.execute(petId, UUID.fromString(jwt.getSubject()), request)));
    }

    @GetMapping("/pets/{petId}/consultations")
    public ResponseEntity<ApiResponse<List<ConsultationResponse>>> listConsultations(
            @PathVariable UUID petId, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ApiResponse.of(listConsultationsByPetUseCase.execute(petId, UUID.fromString(jwt.getSubject()))));
    }

    @PostMapping(value = "/pets/{petId}/consultations/{id}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ConsultationResponse>> uploadAttachments(
            @PathVariable UUID petId, @PathVariable UUID id, @RequestPart("files") List<MultipartFile> files, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ApiResponse.of(uploadConsultationAttachmentUseCase.execute(petId, id, UUID.fromString(jwt.getSubject()), files)));
    }

    @DeleteMapping("/pets/{petId}/consultations/{id}/attachments/{index}")
    public ResponseEntity<ApiResponse<ConsultationResponse>> deleteAttachment(
            @PathVariable UUID petId, @PathVariable UUID id, @PathVariable int index, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ApiResponse.of(deleteConsultationAttachmentUseCase.execute(petId, id, UUID.fromString(jwt.getSubject()), index)));
    }
}
