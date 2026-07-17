package com.petlife.modules.auth.application.usecase;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.dto.RefreshTokenRequest;
import com.petlife.modules.auth.dto.TokenResponse;
import com.petlife.modules.auth.entity.User;
import com.petlife.shared.exception.BusinessException;
import com.petlife.shared.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;


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
    private JwtService jwtService;

    @Mock
    private JwtDecoder jwtDecoder;

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

        Jwt jwt = Jwt.withTokenValue("valid_refresh_token")
                .header("alg", "none")
                .claim("refresh", true)
                .subject(userId.toString())
                .build();

        when(jwtDecoder.decode("valid_refresh_token")).thenReturn(jwt);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(any())).thenReturn("new_access_token");
        when(jwtService.generateRefreshToken(any())).thenReturn("new_refresh_token");

        TokenResponse response = refreshTokenUseCase.execute(request);

        assertNotNull(response);
        assertEquals("new_access_token", response.accessToken());
        assertEquals("new_refresh_token", response.refreshToken());
    }

    @Test
    void shouldThrowExceptionWhenTokenIsInvalid() {
        RefreshTokenRequest request = new RefreshTokenRequest("invalid_token");
        when(jwtDecoder.decode("invalid_token")).thenThrow(new RuntimeException("Invalid token"));

        assertThrows(BusinessException.class, () -> refreshTokenUseCase.execute(request));
    }

    @Test
    void shouldThrowExceptionWhenTokenIsNotARefreshToken() {
        RefreshTokenRequest request = new RefreshTokenRequest("access_token");

        Jwt jwt = Jwt.withTokenValue("access_token")
                .header("alg", "none")
                .claim("refresh", false)
                .subject(userId.toString())
                .build();

        when(jwtDecoder.decode("access_token")).thenReturn(jwt);

        assertThrows(BusinessException.class, () -> refreshTokenUseCase.execute(request));
    }

    @Test
    void shouldThrowExceptionWhenSubjectIsInvalid() {
        RefreshTokenRequest request = new RefreshTokenRequest("token_invalid_sub");

        Jwt jwt = Jwt.withTokenValue("token_invalid_sub")
                .header("alg", "none")
                .claim("refresh", true)
                .subject("not-a-uuid")
                .build();

        when(jwtDecoder.decode("token_invalid_sub")).thenReturn(jwt);

        assertThrows(BusinessException.class, () -> refreshTokenUseCase.execute(request));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        RefreshTokenRequest request = new RefreshTokenRequest("valid_token_missing_user");

        Jwt jwt = Jwt.withTokenValue("valid_token_missing_user")
                .header("alg", "none")
                .claim("refresh", true)
                .subject(userId.toString())
                .build();

        when(jwtDecoder.decode("valid_token_missing_user")).thenReturn(jwt);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> refreshTokenUseCase.execute(request));
    }

    @Test
    void shouldThrowExceptionWhenUserIsDeleted() {
        user.setDeletedAt(java.time.LocalDateTime.now());

        RefreshTokenRequest request = new RefreshTokenRequest("valid_token_deleted_user");

        Jwt jwt = Jwt.withTokenValue("valid_token_deleted_user")
                .header("alg", "none")
                .claim("refresh", true)
                .subject(userId.toString())
                .build();

        when(jwtDecoder.decode("valid_token_deleted_user")).thenReturn(jwt);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(BusinessException.class, () -> refreshTokenUseCase.execute(request));
    }
}
