package com.global.logic.handler;

import com.global.logic.dtos.ErrorDetail;
import com.global.logic.dtos.ErrorResponse;
import com.global.logic.service.InvalidTokenException;
import com.global.logic.service.UserAlreadyExistsException;
import com.global.logic.service.UserNotFoundException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {

        List<ErrorDetail> errorDetails = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ErrorDetail(
                        LocalDateTime.now(),
                        status.value(),
                        formatValidationError(fieldError)))
                .collect(Collectors.toList());

        ErrorResponse errorResponse = new ErrorResponse(errorDetails);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(
            UserAlreadyExistsException ex,
            WebRequest request
    ) {
        ErrorDetail errorDetail = new ErrorDetail(LocalDateTime.now(), HttpStatus.CONFLICT.value(), ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(Collections.singletonList(errorDetail));
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTokenException(
            InvalidTokenException ex,
            WebRequest request
    ) {
        ErrorDetail errorDetail = new ErrorDetail(LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(Collections.singletonList(errorDetail));
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(
            UserNotFoundException ex,
            WebRequest request
    ) {
        ErrorDetail errorDetail = new ErrorDetail(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(Collections.singletonList(errorDetail));
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException( 
            Exception ex,
            WebRequest request
    ) {
        logger.error("Unhandled exception occurred: ", ex);
        ErrorDetail errorDetail = new ErrorDetail(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocurrió un error interno inesperado.");
        ErrorResponse errorResponse = new ErrorResponse(Collections.singletonList(errorDetail));
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String formatValidationError(FieldError fieldError) {
        return "Campo '" + fieldError.getField() + "': " + fieldError.getDefaultMessage();
    }
}
