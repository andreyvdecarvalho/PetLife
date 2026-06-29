package com.petlife.modules.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petlife.modules.auth.dto.GoogleLoginRequest;
import com.petlife.modules.auth.dto.LoginRequest;
import com.petlife.modules.auth.dto.RegisterRequest;
import com.petlife.modules.auth.dto.UpdateProfileRequest;
import com.petlife.modules.auth.entity.User;
import com.petlife.modules.auth.entity.UserPlan;
import com.petlife.modules.auth.repository.UserRepository;
import com.petlife.shared.exception.BusinessException;
import com.petlife.shared.factories.UserFactory;
import com.petlife.shared.security.JwtService;
import com.petlife.shared.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private JwtDecoder jwtDecoder;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("register")
    class Register {

        @Test
        @DisplayName("Deve cadastrar tutor com sucesso e retornar tokens")
        void shouldRegisterUserSuccessfully() {
            var request = new RegisterRequest("Camila", "camila@petlife.com", "Senha@123");
            given(userRepository.existsByEmail(request.email())).willReturn(false);
            given(passwordEncoder.encode(request.password())).willReturn("encoded-password");

            given(jwtService.generateAccessToken(any(UserPrincipal.class))).willReturn("access-token");
            given(jwtService.generateRefreshToken(any(UserPrincipal.class))).willReturn("refresh-token");

            var response = authService.register(request);

            assertThat(response.accessToken()).isEqualTo("access-token");
            assertThat(response.refreshToken()).isEqualTo("refresh-token");
            then(userRepository).should().save(any(User.class));
        }

        @Test
        @DisplayName("Deve lançar conflito se e-mail já existe")
        void shouldThrowConflictWhenEmailExists() {
            var request = new RegisterRequest("Camila", "camila@petlife.com", "Senha@123");
            given(userRepository.existsByEmail(request.email())).willReturn(true);

            assertThatThrownBy(() -> authService.register(request))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        var businessEx = (BusinessException) ex;
                        assertThat(businessEx.getCode()).isEqualTo("AUTH_EMAIL_ALREADY_EXISTS");
                        assertThat(businessEx.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                    });

            then(userRepository).should(never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("login")
    class Login {

        @Test
        @DisplayName("Deve autenticar tutor com credenciais corretas")
        void shouldAuthenticateSuccessfully() {
            var user = UserFactory.make(u -> {
                u.setEmail("camila@petlife.com");
                u.setPasswordHash("encoded-password");
            });
            var request = new LoginRequest("camila@petlife.com", "Senha@123");

            given(userRepository.findByEmail(request.email())).willReturn(Optional.of(user));
            given(passwordEncoder.matches(request.password(), user.getPasswordHash())).willReturn(true);
            given(jwtService.generateAccessToken(any(UserPrincipal.class))).willReturn("access-token");
            given(jwtService.generateRefreshToken(any(UserPrincipal.class))).willReturn("refresh-token");

            var response = authService.login(request);

            assertThat(response.accessToken()).isEqualTo("access-token");
            assertThat(response.refreshToken()).isEqualTo("refresh-token");
        }

        @Test
        @DisplayName("Deve lançar não autorizado para senha incorreta")
        void shouldThrowUnauthorizedForWrongPassword() {
            var user = UserFactory.make(u -> u.setPasswordHash("encoded-password"));
            var request = new LoginRequest(user.getEmail(), "SenhaErrada");

            given(userRepository.findByEmail(request.email())).willReturn(Optional.of(user));
            given(passwordEncoder.matches(request.password(), user.getPasswordHash())).willReturn(false);

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        var businessEx = (BusinessException) ex;
                        assertThat(businessEx.getCode()).isEqualTo("AUTH_INVALID_CREDENTIALS");
                        assertThat(businessEx.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
                    });
        }
    }

    @Nested
    @DisplayName("loginWithGoogle")
    class LoginWithGoogle {

        @Test
        @DisplayName("Deve autenticar tutor com sucesso via Google para conta existente")
        void shouldLoginExistingUserViaGoogle() {
            var user = UserFactory.make(u -> u.setEmail("google.tutor@petlife.com"));
            var request = new GoogleLoginRequest("dummyHeader.eyJlbWFpbCI6Imdvb2dsZS50dXRvckBwZXRsaWZlLmNvbSIsIm5hbWUiOiJHb29nbGUgVHV0b3IiLCJwaWN0dXJlIjoiaHR0cDovL2dvb2dsZS51cmwvYXZhdGFyIn0.dummySignature");

            given(userRepository.findByEmail("google.tutor@petlife.com")).willReturn(Optional.of(user));
            given(jwtService.generateAccessToken(any(UserPrincipal.class))).willReturn("access-token");
            given(jwtService.generateRefreshToken(any(UserPrincipal.class))).willReturn("refresh-token");

            var response = authService.loginWithGoogle(request);

            assertThat(response.accessToken()).isEqualTo("access-token");
            assertThat(response.refreshToken()).isEqualTo("refresh-token");
            then(userRepository).should().save(user);
        }

        @Test
        @DisplayName("Deve criar nova conta e autenticar tutor via Google")
        void shouldRegisterAndLoginNewUserViaGoogle() {
            var request = new GoogleLoginRequest("dummyHeader.eyJlbWFpbCI6Imdvb2dsZS50dXRvckBwZXRsaWZlLmNvbSIsIm5hbWUiOiJHb29nbGUgVHV0b3IiLCJwaWN0dXJlIjoiaHR0cDovL2dvb2dsZS51cmwvYXZhdGFyIn0.dummySignature");

            given(userRepository.findByEmail("google.tutor@petlife.com")).willReturn(Optional.empty());
            given(jwtService.generateAccessToken(any(UserPrincipal.class))).willReturn("access-token");
            given(jwtService.generateRefreshToken(any(UserPrincipal.class))).willReturn("refresh-token");

            var response = authService.loginWithGoogle(request);

            assertThat(response.accessToken()).isEqualTo("access-token");
            assertThat(response.refreshToken()).isEqualTo("refresh-token");
            then(userRepository).should().save(any(User.class));
        }

        @Test
        @DisplayName("Deve lançar erro para token do Google inválido")
        void shouldThrowExceptionForInvalidGoogleToken() {
            var request = new GoogleLoginRequest("token-invalido");

            assertThatThrownBy(() -> authService.loginWithGoogle(request))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        var businessEx = (BusinessException) ex;
                        assertThat(businessEx.getCode()).isEqualTo("AUTH_INVALID_GOOGLE_TOKEN");
                        assertThat(businessEx.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    });
        }
    }

    @Nested
    @DisplayName("deleteAccount")
    class DeleteAccount {

        @Test
        @DisplayName("Deve deletar tutor com sucesso")
        void shouldDeleteUserAccountSuccessfully() {
            var userId = UUID.randomUUID();
            var user = UserFactory.make(u -> u.setId(userId));
            given(userRepository.findById(userId)).willReturn(Optional.of(user));

            authService.deleteAccount(userId);

            then(userRepository).should().delete(user);
        }

        @Test
        @DisplayName("Deve lançar não encontrado se ID do tutor não existe")
        void shouldThrowNotFoundWhenUserDoesNotExist() {
            var userId = UUID.randomUUID();
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            assertThatThrownBy(() -> authService.deleteAccount(userId))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        var businessEx = (BusinessException) ex;
                        assertThat(businessEx.getCode()).isEqualTo("USER_NOT_FOUND");
                        assertThat(businessEx.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    });

            then(userRepository).should(never()).delete(any(User.class));
        }
    }

    @Nested
    @DisplayName("updateProfile")
    class UpdateProfile {

        @Test
        @DisplayName("Deve atualizar dados de perfil com sucesso")
        void shouldUpdateProfileDataSuccessfully() {
            var userId = UUID.randomUUID();
            var user = UserFactory.make(u -> {
                u.setId(userId);
                u.setName("Camila");
                u.setEmail("camila@petlife.com");
            });

            var request = new UpdateProfileRequest("Camila Nova", "camila.nova@petlife.com", "http://new.avatar", "America/Sao_Paulo");
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(userRepository.existsByEmail(request.email())).willReturn(false);

            var response = authService.updateProfile(userId, request);

            assertThat(response.name()).isEqualTo("Camila Nova");
            assertThat(response.email()).isEqualTo("camila.nova@petlife.com");
            assertThat(response.avatarUrl()).isEqualTo("http://new.avatar");
            then(userRepository).should().save(user);
        }

        @Test
        @DisplayName("Deve lançar conflito se novo e-mail já pertence a outro usuário")
        void shouldThrowConflictIfNewEmailIsTaken() {
            var userId = UUID.randomUUID();
            var user = UserFactory.make(u -> {
                u.setId(userId);
                u.setEmail("camila@petlife.com");
            });

            var request = new UpdateProfileRequest("Camila", "outro@petlife.com", null, "America/Sao_Paulo");
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(userRepository.existsByEmail(request.email())).willReturn(true);

            assertThatThrownBy(() -> authService.updateProfile(userId, request))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        var businessEx = (BusinessException) ex;
                        assertThat(businessEx.getCode()).isEqualTo("AUTH_EMAIL_ALREADY_EXISTS");
                        assertThat(businessEx.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                    });

            then(userRepository).should(never()).save(user);
        }
    }
}
