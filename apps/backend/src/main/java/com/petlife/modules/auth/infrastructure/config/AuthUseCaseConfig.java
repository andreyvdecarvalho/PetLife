package com.petlife.modules.auth.infrastructure.config;

import com.petlife.modules.auth.application.port.OAuthProviderPort;
import com.petlife.modules.auth.application.port.PasswordEncryptionPort;
import com.petlife.modules.auth.application.port.TokenGeneratorPort;
import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.application.usecase.LoginUserUseCase;
import com.petlife.modules.auth.application.usecase.RegisterUserUseCase;
import com.petlife.modules.auth.application.usecase.RefreshTokenUseCase;
import com.petlife.modules.auth.application.usecase.LoginWithGoogleUseCase;
import com.petlife.modules.auth.application.usecase.GetUserProfileUseCase;
import com.petlife.modules.auth.application.usecase.UpdateUserProfileUseCase;
import com.petlife.modules.auth.application.usecase.DeleteUserAccountUseCase;
import com.petlife.modules.auth.application.usecase.ForgotPasswordUseCase;
import com.petlife.modules.auth.application.usecase.ResetPasswordUseCase;
import com.petlife.modules.auth.application.usecase.UploadUserPhotoUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthUseCaseConfig {

    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UserRepositoryPort userRepositoryPort,
            PasswordEncryptionPort passwordEncryptionPort,
            TokenGeneratorPort tokenGeneratorPort) {
        return new RegisterUserUseCase(userRepositoryPort, passwordEncryptionPort, tokenGeneratorPort);
    }

    @Bean
    public LoginUserUseCase loginUserUseCase(
            UserRepositoryPort userRepositoryPort,
            PasswordEncryptionPort passwordEncryptionPort,
            TokenGeneratorPort tokenGeneratorPort) {
        return new LoginUserUseCase(userRepositoryPort, passwordEncryptionPort, tokenGeneratorPort);
    }

    @Bean
    public LoginWithGoogleUseCase loginWithGoogleUseCase(
            UserRepositoryPort userRepositoryPort,
            TokenGeneratorPort tokenGeneratorPort,
            OAuthProviderPort oAuthProviderPort) {
        return new LoginWithGoogleUseCase(userRepositoryPort, tokenGeneratorPort, oAuthProviderPort);
    }

    @Bean
    public RefreshTokenUseCase refreshTokenUseCase(
            UserRepositoryPort userRepositoryPort,
            TokenGeneratorPort tokenGeneratorPort) {
        return new RefreshTokenUseCase(userRepositoryPort, tokenGeneratorPort);
    }

    @Bean
    public GetUserProfileUseCase getUserProfileUseCase(UserRepositoryPort userRepositoryPort) {
        return new GetUserProfileUseCase(userRepositoryPort);
    }

    @Bean
    public UpdateUserProfileUseCase updateUserProfileUseCase(UserRepositoryPort userRepositoryPort) {
        return new UpdateUserProfileUseCase(userRepositoryPort);
    }

    @Bean
    public DeleteUserAccountUseCase deleteUserAccountUseCase(UserRepositoryPort userRepositoryPort) {
        return new DeleteUserAccountUseCase(userRepositoryPort);
    }

    @Bean
    public ForgotPasswordUseCase forgotPasswordUseCase(
            UserRepositoryPort userRepositoryPort,
            TokenGeneratorPort tokenGeneratorPort) {
        return new ForgotPasswordUseCase(userRepositoryPort, tokenGeneratorPort); 
        // Wait, ForgotPassword needs to generate a reset token.
    }

    @Bean
    public ResetPasswordUseCase resetPasswordUseCase(
            UserRepositoryPort userRepositoryPort,
            PasswordEncryptionPort passwordEncryptionPort,
            TokenGeneratorPort tokenGeneratorPort) {
        return new ResetPasswordUseCase(userRepositoryPort, passwordEncryptionPort, tokenGeneratorPort);
    }

    @Bean
    public UploadUserPhotoUseCase uploadUserPhotoUseCase(UserRepositoryPort userRepositoryPort) {
        return new UploadUserPhotoUseCase(userRepositoryPort);
    }
}
