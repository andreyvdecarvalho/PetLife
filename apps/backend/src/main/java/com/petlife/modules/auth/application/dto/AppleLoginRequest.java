package com.petlife.modules.auth.application.dto;

import jakarta.validation.constraints.NotBlank;

public record AppleLoginRequest(
    @NotBlank(message = "O token da Apple não pode estar vazio")
    String idToken,
    String email,
    String name
) {}
