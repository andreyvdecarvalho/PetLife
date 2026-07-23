package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.ConsultationRepositoryPort;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.domain.entity.Consultation;
import com.petlife.modules.pet.domain.entity.Pet;
import com.petlife.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteConsultationUseCase {

    private final ConsultationRepositoryPort consultationRepositoryPort;
    private final PetRepositoryPort petRepositoryPort;

    @Transactional
    public void execute(UUID petId, UUID consultationId, UUID userId) {
        Pet pet = petRepositoryPort.findById(petId)
                .orElseThrow(() -> BusinessException.notFound("PET_NOT_FOUND", "Pet não encontrado."));

        if (!pet.getUser().getId().equals(userId)) {
            throw BusinessException.forbidden("FORBIDDEN_PET_ACCESS", "Este pet não pertence ao usuário autenticado.");
        }

        Consultation consultation = consultationRepositoryPort.findById(consultationId)
                .orElseThrow(() -> BusinessException.notFound("CONSULTATION_NOT_FOUND", "Consulta não encontrada."));

        if (!consultation.getPet().getId().equals(petId)) {
            throw BusinessException.badRequest("CONSULTATION_PET_MISMATCH", "Esta consulta não pertence a este pet.");
        }

        consultationRepositoryPort.delete(consultation);
        log.info("Consulta {} excluída com sucesso para o pet {}", consultationId, petId);
    }
}
