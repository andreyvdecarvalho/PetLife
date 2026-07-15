package com.petlife.modules.veterinarian.infrastructure.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AddVetAddressRequest(
    @NotBlank(message = "O rótulo do endereço é obrigatório.")
    String label,
    @NotBlank(message = "A rua é obrigatória.")
    String street,
    @NotBlank(message = "O número é obrigatório.")
    String number,
    String complement,
    @NotBlank(message = "O bairro é obrigatório.")
    String neighborhood,
    @NotBlank(message = "A cidade é obrigatória.")
    String city,
    @NotBlank(message = "O estado é obrigatório.")
    @Pattern(regexp = "^[A-Z]{2}$", message = "O estado deve conter 2 letras maiúsculas.")
    String state,
    @NotBlank(message = "O CEP é obrigatório.")
    @Pattern(regexp = "^\\d{5}-\\d{3}$", message = "Formato de CEP inválido (XXXXX-XXX).")
    String postalCode,
    boolean isPrimary
) {}
