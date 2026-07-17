package com.petlife.shared.exception;

import com.petlife.shared.response.ErrorResponse;
import com.petlife.shared.response.ErrorResponse.ErrorDetail;
import com.petlife.shared.response.ErrorResponse.FieldError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        log.warn("Business rule violation: {} - {}", ex.getCode(), ex.getMessage());
        return ResponseEntity
                .status(ex.getStatus())
                .body(new ErrorResponse(new ErrorDetail(ex.getCode(), ex.getMessage(), List.of())));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        var details = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new FieldError(e.getField(), e.getDefaultMessage()))
                .toList();
        log.warn("Validation failed for request: {}", details);
        return ResponseEntity
                .status(422)
                .body(new ErrorResponse(new ErrorDetail("VALIDATION_ERROR", "Dados inválidos", details)));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(new ErrorDetail("FORBIDDEN", "Acesso negado", List.of())));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(new ErrorDetail("UNAUTHORIZED", "Não autorizado", List.of())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unhandled exception caught: ", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(new ErrorDetail(
                        "INTERNAL_SERVER_ERROR", "Ocorreu um erro interno no servidor", List.of())));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        log.warn("Max upload size exceeded: {}", exc.getMessage());
        return ResponseEntity
                .status(413)
                .body(new ErrorResponse(new ErrorDetail("PAYLOAD_TOO_LARGE",
                        "O arquivo enviado é muito grande", List.of())));
    }
}
