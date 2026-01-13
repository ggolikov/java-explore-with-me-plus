package ewm.common.error;

import ewm.common.dto.ApiError;
import ewm.common.exception.BadRequestException;
import ewm.common.exception.ConflictException;
import ewm.common.exception.NotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
        return buildError(ex.getMessage(), "Не найден необходимый объект.", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException ex) {
        return buildError(ex.getMessage(), "Не выполнены условия для запрашиваемой операции.",
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return buildError(ex.getMessage(), "Некорректный запрос", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex) {
        return buildError(ex.getMessage(), "Некорректный запрос",  HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ApiError> buildError(String message, String reason, HttpStatus status) {
        ApiError error = ApiError.builder()
                .message(message)
                .reason(reason)
                .status(status.name())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, status);
    }
}
