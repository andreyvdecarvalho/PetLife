package com.petlife.modules.pet.infrastructure.controller;

import com.petlife.modules.pet.application.usecase.ExportPetMedicalPassUseCase;
import com.petlife.modules.pet.application.usecase.GetPetTimelineUseCase;
import com.petlife.modules.pet.entity.TimelineEventType;
import com.petlife.modules.pet.infrastructure.dto.TimelineEventResponse;
import com.petlife.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TimelineController {

    private final GetPetTimelineUseCase getPetTimelineUseCase;
    private final ExportPetMedicalPassUseCase exportPetMedicalPassUseCase;

    @GetMapping("/pets/{petId}/timeline")
    public ResponseEntity<ApiResponse<List<TimelineEventResponse>>> getTimeline(
            @PathVariable UUID petId,
            @RequestParam(value = "types", required = false) List<TimelineEventType> types,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        List<TimelineEventResponse> response = getPetTimelineUseCase.execute(petId, userId, types, page, size);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @GetMapping("/pets/{petId}/export")
    public ResponseEntity<byte[]> exportMedicalPass(
            @PathVariable UUID petId,
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        byte[] pdfBytes = exportPetMedicalPassUseCase.execute(petId, userId, startDate, endDate);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "prontuario.pdf");
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
