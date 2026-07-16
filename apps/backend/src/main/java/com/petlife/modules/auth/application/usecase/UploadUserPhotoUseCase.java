package com.petlife.modules.auth.application.usecase;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.entity.User;
import com.petlife.modules.auth.dto.UserResponse;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UploadUserPhotoUseCase {

    private final UserRepositoryPort userRepository;

    @Transactional
    public UserResponse execute(UUID userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("USER_NOT_FOUND", "Usuário não encontrado."));

        if (file.isEmpty()) {
            throw BusinessException.badRequest("EMPTY_FILE", "O arquivo de foto não pode ser vazio.");
        }

        if (file.getSize() > 500 * 1024) {
            throw BusinessException.badRequest("FILE_TOO_LARGE", "A foto de perfil deve ter no máximo 500KB.");
        }

        String fakePhotoUrl = "https://s3.amazonaws.com/petlife/users/" 
                + userId + "_" + System.currentTimeMillis() + ".jpg";
        user.setAvatarUrl(fakePhotoUrl);

        User savedUser = userRepository.save(user);
        log.info("Foto de perfil atualizada com sucesso para o usuário {} (URL: {})", userId, fakePhotoUrl);

        return UserResponse.from(savedUser);
    }
}
