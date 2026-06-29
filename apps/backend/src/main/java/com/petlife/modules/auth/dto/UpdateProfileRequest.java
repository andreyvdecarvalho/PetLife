package com.petlife.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 2, max = 200, message = "O nome deve ter entre 2 e 200 caracteres.")
    String name,

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Formato de e-mail inválido.")
    @Size(max = 255, message = "O e-mail deve ter no máximo 255 caracteres.")
    String email,

    @Size(max = 500, message = "O link do avatar deve ter no máximo 500 caracteres.")
    String avatarUrl,

    @NotBlank(message = "O fuso horário é obrigatório.")
    @Size(max = 50, message = "O fuso horário deve ter no máximo 50 caracteres.")
    String timezone
) {}
