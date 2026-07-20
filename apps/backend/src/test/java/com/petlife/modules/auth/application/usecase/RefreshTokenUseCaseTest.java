package com.petlife.modules.auth.application.usecase;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.application.dto.RefreshTokenRequest;
import com.petlife.modules.auth.application.dto.TokenResponse;
import com.petlife.modules.auth.domain.entity.User;
import com.petlife.shared.exception.BusinessException;
import com.petlife.modules.auth.application.port.TokenGeneratorPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private TokenGeneratorPort tokenService;

    @InjectMocks
    private RefreshTokenUseCase refreshTokenUseCase;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
    }

    @Test
    void shouldRefreshTokenSuccessfully() {
        RefreshTokenRequest request = new RefreshTokenRequest("valid_refresh_token");

        when(tokenService.extractUserIdFromRefreshToken("valid_refresh_token")).thenReturn(userId.toString());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(tokenService.generateAccessToken(any())).thenReturn("new_access_token");
        when(tokenService.generateRefreshToken(any())).thenReturn("new_refresh_token");

        TokenResponse response = refreshTokenUseCase.execute(request);

        assertNotNull(response);
        assertEquals("new_access_token", response.accessToken());
        assertEquals("new_refresh_token", response.refreshToken());
    }

    @Test
    void shouldThrowExceptionWhenTokenIsInvalid() {
        RefreshTokenRequest request = new RefreshTokenRequest("invalid_token");
        when(tokenService.extractUserIdFromRefreshToken("invalid_token")).thenReturn(null);

        assertThrows(BusinessException.class, () -> refreshTokenUseCase.execute(request));
    }

    @Test
    void shouldThrowExceptionWhenTokenIsNotARefreshToken() {
        RefreshTokenRequest request = new RefreshTokenRequest("access_token");
        when(tokenService.extractUserIdFromRefreshToken("access_token")).thenReturn(null);

        assertThrows(BusinessException.class, () -> refreshTokenUseCase.execute(request));
    }

    @Test
    void shouldThrowExceptionWhenSubjectIsInvalid() {
        RefreshTokenRequest request = new RefreshTokenRequest("token_invalid_sub");

        when(tokenService.extractUserIdFromRefreshToken("token_invalid_sub")).thenThrow(new IllegalArgumentException("Invalid UUID"));

        assertThrows(IllegalArgumentException.class, () -> refreshTokenUseCase.execute(request));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        RefreshTokenRequest request = new RefreshTokenRequest("valid_token_missing_user");

        when(tokenService.extractUserIdFromRefreshToken("valid_token_missing_user")).thenReturn(userId.toString());
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> refreshTokenUseCase.execute(request));
    }

    @Test
    void shouldThrowExceptionWhenUserIsDeleted() {
        user.setDeletedAt(java.time.LocalDateTime.now());

        RefreshTokenRequest request = new RefreshTokenRequest("valid_token_deleted_user");

        when(tokenService.extractUserIdFromRefreshToken("valid_token_deleted_user")).thenReturn(userId.toString());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(BusinessException.class, () -> refreshTokenUseCase.execute(request));
    }
}
