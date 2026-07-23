package com.petlife.modules.veterinarian.application.usecase;

import com.petlife.modules.veterinarian.application.port.VeterinarianRepositoryPort;
import com.petlife.modules.veterinarian.domain.entity.Modality;
import com.petlife.modules.veterinarian.domain.entity.Veterinarian;
import com.petlife.modules.veterinarian.infrastructure.dto.request.SearchVeterinariansRequest;
import com.petlife.modules.veterinarian.infrastructure.dto.response.VeterinarianResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchVeterinariansUseCaseTest {

    @Mock
    private VeterinarianRepositoryPort veterinarianRepository;

    @InjectMocks
    private SearchVeterinariansUseCase useCase;

    @Test
    void shouldSearchVeterinariansSuccessfully() {
        SearchVeterinariansRequest request = SearchVeterinariansRequest.builder()
                .latitude(BigDecimal.valueOf(-23.5))
                .longitude(BigDecimal.valueOf(-46.6))
                .radiusKm(10.0)
                .modality(Modality.IN_PERSON)
                .page(0)
                .size(10)
                .build();

        Veterinarian vet = new Veterinarian();
        vet.setCrmvNumber("12345");
        com.petlife.shared.domain.PageResult<Veterinarian> page = new com.petlife.shared.domain.PageResult<>(List.of(vet), 0, 10, 1L, 1);

        when(veterinarianRepository.search(any(SearchVeterinariansRequest.class))).thenReturn(page);

        com.petlife.modules.notification.application.usecase.PagedResult<VeterinarianResponse> response = useCase.execute(request);

        assertEquals(1, response.meta().total());
        assertEquals(1, response.meta().totalPages());
        assertEquals("12345", response.content().get(0).getCrmvNumber());
    }
}
