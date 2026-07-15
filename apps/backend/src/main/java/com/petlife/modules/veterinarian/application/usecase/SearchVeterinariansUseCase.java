package com.petlife.modules.veterinarian.application.usecase;

import com.petlife.modules.veterinarian.application.port.VeterinarianRepositoryPort;
import com.petlife.modules.veterinarian.entity.Veterinarian;
import com.petlife.modules.veterinarian.infrastructure.dto.request.SearchVeterinariansRequest;
import com.petlife.modules.veterinarian.infrastructure.dto.response.VeterinarianResponse;
import com.petlife.shared.response.ApiResponse;
import com.petlife.shared.response.PageMeta;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchVeterinariansUseCase {

    private final VeterinarianRepositoryPort veterinarianRepository;

    public ApiResponse<List<VeterinarianResponse>> execute(SearchVeterinariansRequest request) {
        Page<Veterinarian> vetPage = veterinarianRepository.search(request);
        
        List<VeterinarianResponse> content = vetPage.getContent().stream()
                .map(VeterinarianResponse::fromEntity)
                .toList();

        return ApiResponse.paged(
            content,
            new PageMeta(vetPage.getNumber(), vetPage.getSize(), vetPage.getTotalElements(), vetPage.getTotalPages())
        );
    }
}
