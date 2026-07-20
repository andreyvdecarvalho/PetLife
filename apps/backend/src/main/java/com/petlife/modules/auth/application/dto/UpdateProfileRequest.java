package com.petlife.modules.auth.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import com.petlife.modules.auth.domain.entity.Timezone;

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

    @Size(max = 100, message = "O apelido deve ter no máximo 100 caracteres.")
    String nickname,

    @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres.")
    String phone,

    @NotNull(message = "O fuso horário é obrigatório.")
    Timezone timezone
) {}
