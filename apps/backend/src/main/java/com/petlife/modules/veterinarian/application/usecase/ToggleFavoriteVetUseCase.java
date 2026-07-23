package com.petlife.modules.veterinarian.application.usecase;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.veterinarian.application.port.VetFavoriteRepositoryPort;
import com.petlife.modules.veterinarian.application.port.VeterinarianRepositoryPort;
import com.petlife.modules.veterinarian.domain.entity.VetFavorite;
import com.petlife.modules.veterinarian.domain.entity.Veterinarian;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ToggleFavoriteVetUseCase {

    private final UserRepositoryPort userRepository;
    private final VeterinarianRepositoryPort veterinarianRepository;
    private final VetFavoriteRepositoryPort vetFavoriteRepository;

    @Transactional
    public void execute(UUID userId, UUID veterinarianId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("USER_NOT_FOUND", "Usuário não encontrado."));

        Veterinarian vet = veterinarianRepository.findById(veterinarianId)
                .orElseThrow(() -> BusinessException.notFound("VET_NOT_FOUND", "Veterinário não encontrado."));

        vetFavoriteRepository.findByUserIdAndVeterinarianId(userId, veterinarianId)
                .ifPresentOrElse(
                    favorite -> vetFavoriteRepository.delete(favorite),
                    () -> {
                        VetFavorite favorite = new VetFavorite();
                        favorite.setUser(user);
                        favorite.setVeterinarian(vet);
                        vetFavoriteRepository.save(favorite);
                    }
                );
    }
}
