package com.petlife.modules.auth.controller;

import com.petlife.modules.auth.application.dto.GoogleLoginRequest;
import com.petlife.modules.auth.application.dto.LoginRequest;
import com.petlife.modules.auth.application.dto.RegisterRequest;
import com.petlife.modules.auth.application.dto.UpdateProfileRequest;
import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.shared.IntegrationTestBase;
import com.petlife.shared.factories.UserFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("AuthController Integration Tests")
class AuthControllerTest extends IntegrationTestBase {

    @Autowired
    private UserRepositoryPort userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private org.springframework.security.oauth2.jwt.JwtEncoder jwtEncoder;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private com.petlife.modules.auth.application.port.OAuthProviderPort oauthProviderPort;

    @Nested
    @DisplayName("POST /api/v1/auth/register")
    class Register {

        @Test
        @DisplayName("Deve registrar tutor com dados válidos e retornar tokens JWT")
        void shouldRegisterUserAndReturnTokens() throws Exception {
            var request = new RegisterRequest(
                    "Camila Tutora",
                    "camila." + System.currentTimeMillis() + "@petlife.com",
                    "Senha@123"
            );

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.accessToken").isString())
                    .andExpect(jsonPath("$.data.refreshToken").isString());
        }

        @Test
        @DisplayName("Deve retornar 409 quando o e-mail já existe")
        void shouldReturn409WhenEmailAlreadyExists() throws Exception {
            var existingUser = UserFactory.make();
            userRepository.save(existingUser);

            var request = new RegisterRequest("Outro Nome", existingUser.getEmail(), "Senha@123");

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error.code").value("AUTH_EMAIL_ALREADY_EXISTS"));
        }

        @Test
        @DisplayName("Deve retornar 422 quando a senha for fraca")
        void shouldReturn422WhenPasswordIsTooWeak() throws Exception {
            var request = new RegisterRequest("Camila", "camila@petlife.com", "fraca");

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(422))
                    .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.error.details[0].field").value("password"));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/login")
    class Login {

        @Test
        @DisplayName("Deve autenticar tutor com credenciais corretas")
        void shouldAuthenticateWithValidCredentials() throws Exception {
            var user = UserFactory.make(u -> {
                u.setEmail("login.test@petlife.com");
                u.setPasswordHash(passwordEncoder.encode("Senha@123"));
            });
            userRepository.save(user);

            var request = new LoginRequest(user.getEmail(), "Senha@123");

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.accessToken").isString())
                    .andExpect(jsonPath("$.data.refreshToken").isString());
        }

        @Test
        @DisplayName("Deve retornar 401 para credenciais inválidas")
        void shouldReturn401ForInvalidCredentials() throws Exception {
            var request = new LoginRequest("inexistente@petlife.com", "Senha@123");

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error.code").value("AUTH_INVALID_CREDENTIALS"));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/google")
    class GoogleLogin {

        @Test
        @DisplayName("Deve autenticar tutor com sucesso via Google e retornar tokens")
        void shouldAuthenticateWithGoogle() throws Exception {
            var request = new GoogleLoginRequest("valid_dummy_token");

            org.mockito.Mockito.when(oauthProviderPort.getGoogleUserInfo("valid_dummy_token"))
                    .thenReturn(new com.petlife.modules.auth.application.port.OAuthProviderPort.GoogleUserInfo("google.tutor@petlife.com", "Google Tutor", "http://google.url/avatar"));

            mockMvc.perform(post("/api/v1/auth/google")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.accessToken").isString())
                    .andExpect(jsonPath("$.data.refreshToken").isString());
        }

        @Test
        @DisplayName("Deve retornar 400 para token do Google inválido")
        void shouldReturn400ForInvalidGoogleToken() throws Exception {
            var request = new GoogleLoginRequest("token-invalido");

            org.mockito.Mockito.when(oauthProviderPort.getGoogleUserInfo("token-invalido"))
                    .thenThrow(com.petlife.shared.exception.BusinessException.unauthorized("AUTH_INVALID_GOOGLE_TOKEN", "Token inválido"));

            mockMvc.perform(post("/api/v1/auth/google")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error.code").value("AUTH_INVALID_GOOGLE_TOKEN"));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/forgot-password")
    class ForgotPassword {

        @Test
        @DisplayName("Deve solicitar recuperação de senha com e-mail existente (retorna 204)")
        void shouldRequestPasswordRecoveryWithExistingEmail() throws Exception {
            var user = UserFactory.make();
            userRepository.save(user);

            var request = new com.petlife.modules.auth.application.dto.ForgotPasswordRequest(user.getEmail());

            mockMvc.perform(post("/api/v1/auth/forgot-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Deve retornar 204 silenciosamente se o e-mail não existir")
        void shouldReturn204SilentlyIfEmailDoesNotExist() throws Exception {
            var request = new com.petlife.modules.auth.application.dto.ForgotPasswordRequest("inexistente@petlife.com");

            mockMvc.perform(post("/api/v1/auth/forgot-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/reset-password")
    class ResetPassword {

        @Test
        @DisplayName("Deve redefinir a senha do usuário com token de recuperação válido")
        void shouldResetPasswordWithValidToken() throws Exception {
            var user = UserFactory.make(u -> u.setPasswordHash(passwordEncoder.encode("SenhaAntiga@123")));
            userRepository.save(user);

            var now = java.time.Instant.now();
            var claims = org.springframework.security.oauth2.jwt.JwtClaimsSet.builder()
                    .issuer("petlife")
                    .issuedAt(now)
                    .expiresAt(now.plus(15, java.time.temporal.ChronoUnit.MINUTES))
                    .subject(user.getEmail())
                    .claim("action", "reset-password")
                    .build();

            var resetToken = jwtEncoder.encode(org.springframework.security.oauth2.jwt.JwtEncoderParameters.from(claims)).getTokenValue();

            var request = new com.petlife.modules.auth.application.dto.ResetPasswordRequest(resetToken, "NovaSenha@123");

            mockMvc.perform(post("/api/v1/auth/reset-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent());

            var updatedUser = userRepository.findById(user.getId()).orElseThrow();
            assertThat(passwordEncoder.matches("NovaSenha@123", updatedUser.getPasswordHash())).isTrue();
        }

        @Test
        @DisplayName("Deve retornar 400 para token com action inválida")
        void shouldReturn400ForTokenWithInvalidAction() throws Exception {
            var user = UserFactory.make();
            userRepository.save(user);

            var now = java.time.Instant.now();
            var claims = org.springframework.security.oauth2.jwt.JwtClaimsSet.builder()
                    .issuer("petlife")
                    .issuedAt(now)
                    .expiresAt(now.plus(15, java.time.temporal.ChronoUnit.MINUTES))
                    .subject(user.getEmail())
                    .claim("action", "invalid-action")
                    .build();

            var resetToken = jwtEncoder.encode(org.springframework.security.oauth2.jwt.JwtEncoderParameters.from(claims)).getTokenValue();

            var request = new com.petlife.modules.auth.application.dto.ResetPasswordRequest(resetToken, "NovaSenha@123");

            mockMvc.perform(post("/api/v1/auth/reset-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error.code").value("AUTH_INVALID_RESET_TOKEN"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/auth/me")
    class Profile {

        @Test
        @DisplayName("Deve retornar perfil do tutor autenticado")
        void shouldReturnAuthenticatedUserProfile() throws Exception {
            var user = UserFactory.make();
            userRepository.save(user);

            mockMvc.perform(get("/api/v1/auth/me")
                            .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail()))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(user.getId().toString()))
                    .andExpect(jsonPath("$.data.email").value(user.getEmail()));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/auth/me")
    class UpdateProfile {

        @Test
        @DisplayName("Deve atualizar dados do perfil")
        void shouldUpdateProfileData() throws Exception {
            var user = UserFactory.make();
            userRepository.save(user);

            var request = new UpdateProfileRequest("Novo Nome", user.getEmail(), "http://avatar.url", "Apelido", "11999999999", com.petlife.modules.auth.domain.entity.Timezone.AMERICA_SAO_PAULO);

            mockMvc.perform(put("/api/v1/auth/me")
                            .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.name").value("Novo Nome"))
                    .andExpect(jsonPath("$.data.avatarUrl").value("http://avatar.url"));

            var updatedUser = userRepository.findById(user.getId()).orElseThrow();
            assertThat(updatedUser.getName()).isEqualTo("Novo Nome");
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/auth/me")
    class DeleteAccount {

        @Test
        @DisplayName("Deve excluir conta do tutor (cascade delete)")
        void shouldDeleteUserAccount() throws Exception {
            var user = UserFactory.make();
            userRepository.save(user);

            mockMvc.perform(delete("/api/v1/auth/me")
                            .with(jwt().jwt(j -> j.subject(user.getId().toString()).claim("email", user.getEmail()))))
                    .andExpect(status().isNoContent());

            var deletedUser = userRepository.findById(user.getId());
            assertThat(deletedUser).isEmpty();
        }
    }
}
