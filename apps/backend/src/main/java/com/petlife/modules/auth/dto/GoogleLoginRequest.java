package com.petlife.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequest(
    @NotBlank(message = "O token do Google é obrigatório.")
    String idToken
) {}
