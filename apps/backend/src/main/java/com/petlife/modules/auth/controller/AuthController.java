package com.petlife.modules.auth.controller;

import com.petlife.modules.auth.dto.ForgotPasswordRequest;
import com.petlife.modules.auth.dto.GoogleLoginRequest;
import com.petlife.modules.auth.dto.LoginRequest;
import com.petlife.modules.auth.dto.RegisterRequest;
import com.petlife.modules.auth.dto.ResetPasswordRequest;
import com.petlife.modules.auth.dto.TokenResponse;
import com.petlife.modules.auth.dto.UpdateProfileRequest;
import com.petlife.modules.auth.dto.UserResponse;
import com.petlife.modules.auth.service.AuthService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints de autenticação, perfil e gerenciamento de conta")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar novo tutor")
    public ApiResponse<TokenResponse> register(@Valid @RequestBody RegisterRequest request) {
        var response = authService.register(request);
        return ApiResponse.of(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar tutor")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        var response = authService.login(request);
        return ApiResponse.of(response);
    }

    @PostMapping("/google")
    @Operation(summary = "Autenticar ou cadastrar tutor via Google OAuth2")
    public ApiResponse<TokenResponse> googleLogin(@Valid @RequestBody GoogleLoginRequest request) {
        var response = authService.loginWithGoogle(request);
        return ApiResponse.of(response);
    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Solicitar recuperação de senha")
    public void forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Redefinir senha usando token")
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
    }

    @GetMapping("/me")
    @Operation(summary = "Obter dados do tutor autenticado")
    public ApiResponse<UserResponse> me(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        var response = authService.getProfile(userId);
        return ApiResponse.of(response);
    }

    @PutMapping("/me")
    @Operation(summary = "Atualizar dados do perfil")
    public ApiResponse<UserResponse> updateMe(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateProfileRequest request) {
        UUID userId = UUID.fromString(jwt.getSubject());
        var response = authService.updateProfile(userId, request);
        return ApiResponse.of(response);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Excluir conta do tutor e todos os dados associados (LGPD)")
    public void deleteMe(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        authService.deleteAccount(userId);
    }
}
