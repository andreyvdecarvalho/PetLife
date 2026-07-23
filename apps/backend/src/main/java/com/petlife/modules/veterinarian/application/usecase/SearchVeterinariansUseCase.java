package com.petlife.modules.veterinarian.application.usecase;

import com.petlife.modules.veterinarian.application.port.VeterinarianRepositoryPort;

import com.petlife.modules.veterinarian.infrastructure.dto.request.SearchVeterinariansRequest;
import com.petlife.modules.veterinarian.infrastructure.dto.response.VeterinarianResponse;

import com.petlife.shared.response.PageMeta;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import com.petlife.modules.notification.application.usecase.PagedResult;

@Service
@RequiredArgsConstructor
public class SearchVeterinariansUseCase {

    private final VeterinarianRepositoryPort veterinarianRepository;

    public PagedResult<VeterinarianResponse> execute(
            SearchVeterinariansRequest request
    ) {
        var vetPage = veterinarianRepository.search(request);
        
        List<VeterinarianResponse> content = vetPage
                .getContent()
                .stream()
                .map(VeterinarianResponse::fromEntity)
                .toList();

        return new PagedResult<>(
            content,
            new PageMeta(
                vetPage.getPageNumber(), 
                vetPage.getPageSize(), 
                vetPage.getTotalElements(), 
                vetPage.getTotalPages())
        );
    }
}
