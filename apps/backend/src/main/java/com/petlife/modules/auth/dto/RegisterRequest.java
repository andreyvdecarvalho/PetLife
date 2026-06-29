package com.petlife.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 2, max = 200, message = "O nome deve ter entre 2 e 200 caracteres.")
    String name,

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Formato de e-mail inválido.")
    @Size(max = 255, message = "O e-mail deve ter no máximo 255 caracteres.")
    String email,

    @NotBlank(message = "A senha é obrigatória.")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$",
        message = "A senha deve ter no mínimo 8 caracteres, com pelo menos uma letra maiúscula e um número."
    )
    String password
) {}
