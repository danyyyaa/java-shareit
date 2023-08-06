package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTests {
    @InjectMocks
    private ErrorHandler errorHandler;

    @Test
    void testHandleNotFound() {
        NotFoundException notFoundException = new NotFoundException("Resource not found");
        Map<String, String> result = errorHandler.handleNotFound(notFoundException);
        assertEquals(HttpStatus.NOT_FOUND.toString(), result.get("status"));
        assertEquals("Resource not found", result.get("message"));
    }

    @Test
    void testHandleConflict() {
        AlreadyExistsException alreadyExistsException = new AlreadyExistsException("Resource already exists");
        Map<String, String> result = errorHandler.handleConflict(alreadyExistsException);
        assertEquals(HttpStatus.CONFLICT.toString(), result.get("status"));
        assertEquals("Resource already exists", result.get("message"));
    }

    @Test
    void testHandleBadRequest() {
        ValidationException validationException = new ValidationException("Bad request");
        Map<String, String> result = errorHandler.handleBadRequest(validationException);
        assertEquals(HttpStatus.BAD_REQUEST.toString(), result.get("status"));
        assertEquals("Bad request", result.get("message"));
    }

    @Test
    void testHandleRaw() {
        Throwable throwable = new Throwable("Internal server error");
        Map<String, String> result = errorHandler.handleRaw(throwable);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.toString(), result.get("status"));
        assertEquals("Internal server error", result.get("message"));
    }
}

