package com.petlife.modules.pet.infrastructure.controller;

import com.petlife.modules.pet.application.usecase.CreatePetUseCase;
import com.petlife.modules.pet.application.usecase.GetPetByIdUseCase;
import com.petlife.modules.pet.application.usecase.GetPetsUseCase;
import com.petlife.modules.pet.application.usecase.UploadPetPhotoUseCase;
import com.petlife.modules.pet.infrastructure.dto.CreatePetRequest;
import com.petlife.modules.pet.infrastructure.dto.PetResponse;
import com.petlife.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pets")
@RequiredArgsConstructor
@Tag(name = "Pets", description = "Endpoints de gerenciamento de pets")
public class PetController {

    private final CreatePetUseCase createPetUseCase;
    private final UploadPetPhotoUseCase uploadPetPhotoUseCase;
    private final GetPetsUseCase getPetsUseCase;
    private final GetPetByIdUseCase getPetByIdUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar novo pet")
    public ApiResponse<PetResponse> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreatePetRequest request) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return ApiResponse.of(createPetUseCase.execute(userId, request));
    }

    @PostMapping("/{id}/photo")
    @Operation(summary = "Upload de foto do pet")
    public ApiResponse<PetResponse> uploadPhoto(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return ApiResponse.of(uploadPetPhotoUseCase.execute(userId, id, file));
    }

    @GetMapping
    @Operation(summary = "Listar pets do tutor autenticado (paginado)")
    public ApiResponse<List<PetResponse>> list(
            @AuthenticationPrincipal Jwt jwt,
            @PageableDefault(size = 10) Pageable pageable) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return getPetsUseCase.execute(userId, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter detalhes do pet")
    public ApiResponse<PetResponse> getById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return ApiResponse.of(getPetByIdUseCase.execute(userId, id));
    }
}
