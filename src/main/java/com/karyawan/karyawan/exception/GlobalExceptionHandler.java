package com.karyawan.karyawan.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Pencarian data gagal: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        log.error("Terjadi kesalahan operasi bisnis: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Validasi input gagal: {}", ex.getMessage());
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Validasi input gagal");
                
        return ResponseEntity.badRequest().body(Map.of("error", errorMessage));
    }
}