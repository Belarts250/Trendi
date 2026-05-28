package com.Trendi.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException{

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleNotFound(ResourceNotFoundException ex){
        Map<String, Object> error = new HashMap<>();

        error.put("timestamp", LocalDateTime.now());
        error.put("status", 404);
        error.put("error" , "Not Found");
        error.put("message", ex.getMessage());

        return new ResponseEntity<>( error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public  ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex){
        Map<String, Object> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(
                error -> {
                    String fieldName = ((FieldError) error).getField();

                    String errorMessage = error.getDefaultMessage();

                    errors.put(fieldName, errorMessage);
                }
        );

        return new ResponseEntity<>( errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneral(Exception e){
        Map<String, Object> err = new HashMap<>();

        err.put("timestamp", LocalDateTime.now());
        err.put("status", 500);
        err.put("error", "Internal Server Error");
        err.put("message", e.getMessage());

        return  new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
