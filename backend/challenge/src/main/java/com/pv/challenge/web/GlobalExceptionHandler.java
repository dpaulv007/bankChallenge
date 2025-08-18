package com.pv.challenge.web;

import com.pv.challenge.exception.BusinessException;
import com.pv.challenge.exception.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

import javax.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest req) {
    ErrorResponse body = new ErrorResponse(
        status.value(),
        status.getReasonPhrase(),
        message,
        req.getRequestURI(),
        OffsetDateTime.now().toString()
    );
    return ResponseEntity.status(status).body(body);
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException ex, HttpServletRequest req) {
    return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest req) {
    return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleConflict(DataIntegrityViolationException ex, HttpServletRequest req) {
    return build(HttpStatus.CONFLICT, "Conflicto con los datos (duplicados o integridad).", req);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleBadBody(HttpMessageNotReadableException ex, HttpServletRequest req) {
    return build(HttpStatus.BAD_REQUEST, "Cuerpo de la petición inválido.", req);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
    String msg = ex.getBindingResult().getFieldErrors().stream()
        .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
        .collect(Collectors.joining("; "));
    if (msg.isEmpty()) msg = "Validación inválida.";
    return build(HttpStatus.BAD_REQUEST, msg, req);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {
    log.error("Error interno en {}: {}", req.getRequestURI(), ex.getMessage(), ex);
    return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno. Intenta más tarde.", req);
  }
}
