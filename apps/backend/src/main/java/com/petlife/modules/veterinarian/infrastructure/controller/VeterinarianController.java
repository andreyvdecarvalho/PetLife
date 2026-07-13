package com.petlife.modules.veterinarian.infrastructure.controller;

import com.petlife.modules.veterinarian.application.usecase.AddVetAddressUseCase;
import com.petlife.modules.veterinarian.application.usecase.CreateVeterinarianProfileUseCase;
import com.petlife.modules.veterinarian.application.usecase.GetVetProfileUseCase;
import com.petlife.modules.veterinarian.application.usecase.SearchVeterinariansUseCase;
import com.petlife.modules.veterinarian.application.usecase.SetVetScheduleUseCase;
import com.petlife.modules.veterinarian.application.usecase.ToggleFavoriteVetUseCase;
import com.petlife.modules.veterinarian.application.usecase.UpdateAvailabilityUseCase;
import com.petlife.modules.veterinarian.entity.Modality;
import com.petlife.modules.veterinarian.infrastructure.dto.request.AddVetAddressRequest;
import com.petlife.modules.veterinarian.infrastructure.dto.request.CreateVeterinarianRequest;
import com.petlife.modules.veterinarian.infrastructure.dto.request.SearchVeterinariansRequest;
import com.petlife.modules.veterinarian.infrastructure.dto.request.SetVetScheduleRequest;
import com.petlife.modules.veterinarian.infrastructure.dto.request.UpdateAvailabilityRequest;
import com.petlife.modules.veterinarian.infrastructure.dto.response.VetAddressResponse;
import com.petlife.modules.veterinarian.infrastructure.dto.response.VetScheduleResponse;
import com.petlife.modules.veterinarian.infrastructure.dto.response.VeterinarianResponse;
import com.petlife.shared.response.ApiResponse;
import com.petlife.shared.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/veterinarians")
@RequiredArgsConstructor
@Tag(name = "Veterinarians", description = "Gerenciamento de veterinários e clínicas")
public class VeterinarianController {

    private final CreateVeterinarianProfileUseCase createVeterinarianProfileUseCase;
    private final AddVetAddressUseCase addVetAddressUseCase;
    private final SetVetScheduleUseCase setVetScheduleUseCase;
    private final UpdateAvailabilityUseCase updateAvailabilityUseCase;
    private final ToggleFavoriteVetUseCase toggleFavoriteVetUseCase;
    private final SearchVeterinariansUseCase searchVeterinariansUseCase;
    private final GetVetProfileUseCase getVetProfileUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria o perfil de um veterinário (requer CRMV)")
    public ApiResponse<VeterinarianResponse> createProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid CreateVeterinarianRequest request) {
        return ApiResponse.of(createVeterinarianProfileUseCase.execute(userPrincipal.getUserId(), request));
    }

    @PostMapping("/address")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Adiciona endereço ao veterinário logado")
    public ApiResponse<VetAddressResponse> addAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid AddVetAddressRequest request) {
        return ApiResponse.of(addVetAddressUseCase.execute(userPrincipal.getUserId(), request));
    }

    @PostMapping("/schedule")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Define horário de funcionamento do veterinário logado")
    public ApiResponse<VetScheduleResponse> setSchedule(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid SetVetScheduleRequest request) {
        return ApiResponse.of(setVetScheduleUseCase.execute(userPrincipal.getUserId(), request));
    }

    @PatchMapping("/availability")
    @Operation(summary = "Atualiza o status de disponibilidade do veterinário logado")
    public ApiResponse<VeterinarianResponse> updateAvailability(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid UpdateAvailabilityRequest request) {
        return ApiResponse.of(updateAvailabilityUseCase.execute(userPrincipal.getUserId(), request));
    }

    @PostMapping("/{veterinarianId}/favorite")
    @Operation(summary = "Tutor favorita ou desfavorita um veterinário")
    public ApiResponse<Void> toggleFavorite(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID veterinarianId) {
        toggleFavoriteVetUseCase.execute(userPrincipal.getUserId(), veterinarianId);
        return ApiResponse.of(null);
    }

    @GetMapping("/{veterinarianId}")
    @Operation(summary = "Busca perfil público de um veterinário por ID")
    public ApiResponse<VeterinarianResponse> getProfile(@PathVariable UUID veterinarianId) {
        return ApiResponse.of(getVetProfileUseCase.execute(veterinarianId));
    }

    @GetMapping("/search")
    @Operation(summary = "Busca veterinários próximos com filtros opcionais")
    public ApiResponse<List<VeterinarianResponse>> search(
            @RequestParam(required = false) BigDecimal lat,
            @RequestParam(required = false) BigDecimal lng,
            @RequestParam(required = false, defaultValue = "10.0") Double radiusKm,
            @RequestParam(required = false) Modality modality,
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) Boolean emergency,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        SearchVeterinariansRequest request = SearchVeterinariansRequest.builder()
                .latitude(lat)
                .longitude(lng)
                .radiusKm(radiusKm)
                .modality(modality)
                .specialty(specialty)
                .emergencyOnDuty(emergency)
                .page(page)
                .size(size)
                .build();
                
        return searchVeterinariansUseCase.execute(request);
    }
}
