package ru.practicum.shareit.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practicum.shareit.user.exception.DuplicateEmailException;
import ru.practicum.shareit.user.exception.UserAlreadyExistsException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.exception.ValidationException;

@ControllerAdvice
public class ErrorHandler {

    /*@ExceptionHandler
    public ResponseEntity<ErrorResponse> catchValidateException(DuplicateEmailException e) {
        return new ResponseEntity<>(
                new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()),
                        HttpStatus.BAD_REQUEST);
    }*/

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> catchConflictArgsException(DuplicateEmailException e) {
        return new ResponseEntity<>(
                new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> catchNotFoundException(UserNotFoundException e) {
        return new ResponseEntity<>(
                new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> catchValidationException(ValidationException e) {
        return new ResponseEntity<>(
                new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    /*@ExceptionHandler
    public ResponseEntity<ErrorResponse> catchAlreadyExistsException(UserAlreadyExistsException e) {
        return new ResponseEntity<>(
                new ErrorResponse(HttpStatus.AL.value(), e.getMessage()),
                HttpStatus.NOT_FOUND);
    }*/
}
