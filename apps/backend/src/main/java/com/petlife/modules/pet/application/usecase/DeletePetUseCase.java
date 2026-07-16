package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use Case: Excluir um pet e todos os seus registros associados (Cascade via DB).
 * Requisito LGPD: Direito ao esquecimento/eliminação.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DeletePetUseCase {

    private final PetRepositoryPort petRepository;

    @Transactional
    public void execute(UUID petId, UUID userId) {
        var pet = petRepository.findById(petId)
                .orElseThrow(() -> BusinessException.notFound("PET_NOT_FOUND", "Pet não encontrado."));

        if (!pet.getUser().getId().equals(userId)) {
            log.warn("Usuário {} tentou deletar pet {} que não lhe pertence.", userId, petId);
            throw BusinessException.forbidden("ACCESS_DENIED", "Acesso negado.");
        }

        petRepository.delete(pet);
        log.info("Pet {} deletado com sucesso pelo usuário {} (Cascade Delete aplicado).", petId, userId);
    }
}
