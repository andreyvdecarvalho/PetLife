package com.petlife.modules.auth.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResetPasswordRequest(
    @NotBlank(message = "O token é obrigatório.")
    String token,

    @NotBlank(message = "A nova senha é obrigatória.")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$",
        message = "A senha deve ter no mínimo 8 caracteres, com pelo menos uma letra maiúscula e um número."
    )
    String newPassword
) {}
