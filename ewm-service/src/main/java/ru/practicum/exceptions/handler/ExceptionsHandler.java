package ru.practicum.exceptions.handler;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.exceptions.exception.BadRequestException;
import ru.practicum.exceptions.exception.ForbiddenException;
import ru.practicum.exceptions.exception.NotFoundException;
import ru.practicum.exceptions.model.ApiError;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionsHandler {

    //400 BAD REQUEST
    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            NumberFormatException.class,
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class,
            BadRequestException.class
    })
    public ResponseEntity<ApiError> handleBadRequest(Exception e) {
        ApiError apiError = new ApiError(
                "BAD_REQUEST",
                "Incorrectly made request.",
                e.getMessage(),
                LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    //409 CONFLICT
    @ExceptionHandler({
            ConstraintViolationException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ApiError> handleConflictException(Exception e) {
        ApiError apiError = new ApiError(
                "CONFLICT",
                "Integrity constraint has been violated.",
                e.getMessage(),
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
    }

    //404 NOT FOUND
    @ExceptionHandler({
            NotFoundException.class
    })
    public ResponseEntity<ApiError> handleNotFoundException(Exception e) {
        ApiError apiError = new ApiError(
                "NOT_FOUND",
                "The required object was not found.",
                e.getMessage(),
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    //409 FORBIDDEN
    @ExceptionHandler({
            ForbiddenException.class,
    })
    public ResponseEntity<ApiError> handleForbiddenException(Exception e) {
        ApiError apiError = new ApiError(
                "FORBIDDEN",
                "For the requested operation the conditions are not met.",
                e.getMessage(),
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
    }
}
