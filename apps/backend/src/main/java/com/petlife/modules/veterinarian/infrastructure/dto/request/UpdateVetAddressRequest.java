package com.petlife.modules.veterinarian.infrastructure.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UpdateVetAddressRequest(
        String label,
        @NotBlank(message = "Logradouro é obrigatório") String street,
        @NotBlank(message = "Número é obrigatório") String number,
        String complement,
        @NotBlank(message = "Bairro é obrigatório") String neighborhood,
        @NotBlank(message = "Cidade é obrigatória") String city,
        @NotBlank(message = "Estado é obrigatório") String state,
        @NotBlank(message = "CEP é obrigatório") String postalCode,
        boolean isPrimary
) {}
