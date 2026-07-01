package com.petlife.modules.auth.application.usecase;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use Case: Excluir conta do usuário e todos os dados associados (LGPD Art. 18).
 * O cascade delete na entidade User garante remoção de pets e dados relacionados.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteUserAccountUseCase {

    private final UserRepositoryPort userRepository;

    @Transactional
    public void execute(UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("USER_NOT_FOUND", "Usuário não encontrado."));

        userRepository.delete(user);
        log.info("Conta e todos os dados do usuário excluídos (conformidade LGPD), ID: {}", userId);
    }
}
