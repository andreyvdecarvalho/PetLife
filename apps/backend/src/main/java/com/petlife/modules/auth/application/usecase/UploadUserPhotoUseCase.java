package com.petlife.modules.auth.application.usecase;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.auth.application.dto.UserResponse;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class UploadUserPhotoUseCase {

    private final UserRepositoryPort userRepository;

        public UserResponse execute(UUID userId, byte[] fileBytes, long fileSize) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("USER_NOT_FOUND", "Usuário não encontrado."));

        if (fileBytes == null || fileBytes.length == 0) {
            throw BusinessException.badRequest("EMPTY_FILE", "O arquivo de foto não pode ser vazio.");
        }

        if (fileSize > 500 * 1024) {
            throw BusinessException.badRequest("FILE_TOO_LARGE", "A foto de perfil deve ter no máximo 500KB.");
        }

        String fakePhotoUrl = "https://s3.amazonaws.com/petlife/users/" 
                + userId + "_" + System.currentTimeMillis() + ".jpg";
        user.setAvatarUrl(fakePhotoUrl);

        User savedUser = userRepository.save(user);

        return UserResponse.from(savedUser);
    }
}
