package com.petlife.modules.auth.infrastructure.controller;

import com.petlife.modules.auth.application.usecase.DeleteUserAccountUseCase;
import com.petlife.modules.auth.application.usecase.ForgotPasswordUseCase;
import com.petlife.modules.auth.application.usecase.GetUserProfileUseCase;
import com.petlife.modules.auth.application.usecase.LoginUserUseCase;
import com.petlife.modules.auth.application.usecase.LoginWithGoogleUseCase;
import com.petlife.modules.auth.application.usecase.RegisterUserUseCase;
import com.petlife.modules.auth.application.usecase.ResetPasswordUseCase;
import com.petlife.modules.auth.application.usecase.UpdateUserProfileUseCase;
import com.petlife.modules.auth.application.usecase.UploadUserPhotoUseCase;
import com.petlife.modules.auth.application.usecase.RefreshTokenUseCase;
import com.petlife.modules.auth.application.dto.ForgotPasswordRequest;
import com.petlife.modules.auth.application.dto.GoogleLoginRequest;
import com.petlife.modules.auth.application.dto.LoginRequest;
import com.petlife.modules.auth.application.dto.RegisterRequest;
import com.petlife.modules.auth.application.dto.ResetPasswordRequest;
import com.petlife.modules.auth.application.dto.RefreshTokenRequest;
import com.petlife.modules.auth.application.dto.TokenResponse;
import com.petlife.modules.auth.application.dto.UpdateProfileRequest;
import com.petlife.modules.auth.application.dto.UserResponse;
import com.petlife.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Adaptador de Entrada REST (Driving Adapter / Primary Adapter) na Arquitetura Hexagonal.
 * Porta de Entrada / Entry Point para o módulo de Autenticação.
 * Responsabilidade única: receber requests HTTP, delegar aos Use Cases do Core e retornar ApiResponse.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints de autenticação, perfil e gerenciamento de conta")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final LoginWithGoogleUseCase loginWithGoogleUseCase;
    private final ForgotPasswordUseCase forgotPasswordUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;
    private final GetUserProfileUseCase getUserProfileUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final UploadUserPhotoUseCase uploadUserPhotoUseCase;
    private final DeleteUserAccountUseCase deleteUserAccountUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar novo tutor")
    public ApiResponse<TokenResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.of(registerUserUseCase.execute(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar tutor")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.of(loginUserUseCase.execute(request));
    }

    @PostMapping("/google")
    @Operation(summary = "Autenticar ou cadastrar tutor via Google OAuth2")
    public ApiResponse<TokenResponse> googleLogin(@Valid @RequestBody GoogleLoginRequest request) {
        return ApiResponse.of(loginWithGoogleUseCase.execute(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar token de acesso usando refresh token")
    public ApiResponse<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.of(refreshTokenUseCase.execute(request));
    }

    @PostMapping("/me/photo")
    @Operation(summary = "Upload de foto do perfil")
    public ApiResponse<UserResponse> uploadPhoto(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        UUID userId = UUID.fromString(jwt.getSubject());
        try {
            return ApiResponse.of(uploadUserPhotoUseCase.execute(userId, file.getBytes(), file.getSize()));
        } catch (java.io.IOException e) {
            throw com.petlife.shared.exception.BusinessException.badRequest("FILE_READ_ERROR", 
                "Falha ao ler o arquivo.");
        }
    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Solicitar recuperação de senha")
    public void forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        forgotPasswordUseCase.execute(request);
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Redefinir senha usando token")
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        resetPasswordUseCase.execute(request);
    }

    @GetMapping("/me")
    @Operation(summary = "Obter dados do tutor autenticado")
    public ApiResponse<UserResponse> me(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return ApiResponse.of(getUserProfileUseCase.execute(userId));
    }

    @PutMapping("/me")
    @Operation(summary = "Atualizar dados do perfil")
    public ApiResponse<UserResponse> updateMe(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateProfileRequest request) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return ApiResponse.of(updateUserProfileUseCase.execute(userId, request));
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Excluir conta do tutor e todos os dados associados (LGPD)")
    public void deleteMe(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        deleteUserAccountUseCase.execute(userId);
    }
}
