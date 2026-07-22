package com.petlife.modules.notification.application.usecase;

import java.util.List;

/**
 * Resultado paginado de domínio retornado por Use Cases.
 * O wrapping em ApiResponse é responsabilidade do Controller.
 *
 * @param <T> tipo do conteúdo paginado
 */
public record PagedResult<T>(List<T> content, com.petlife.shared.response.PageMeta meta) {}
