package com.global.logic.handler;

import com.global.logic.dtos.ErrorResponse;
import com.global.logic.service.InvalidTokenException;
import com.global.logic.service.UserAlreadyExistsException;
import com.global.logic.service.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {
    @Test
    void testHandleUserNotFoundException() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ResponseEntity<ErrorResponse> response = handler.handleUserNotFoundException(new UserNotFoundException("not found"), null);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testHandleUserAlreadyExistsException() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ResponseEntity<ErrorResponse> response = handler.handleUserAlreadyExistsException(new UserAlreadyExistsException("exists"), null);
        assertEquals(409, response.getStatusCodeValue());
    }

    @Test
    void testHandleInvalidTokenException() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ResponseEntity<ErrorResponse> response = handler.handleInvalidTokenException(new InvalidTokenException("invalid"), null);
        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    void testHandleGlobalException() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ResponseEntity<ErrorResponse> response = handler.handleGlobalException(new Exception("error"), null);
        assertEquals(500, response.getStatusCodeValue());
    }
}
