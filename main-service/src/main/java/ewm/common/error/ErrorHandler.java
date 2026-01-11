package ewm.common.error;

import ewm.common.dto.ApiError;
import ewm.common.exception.BadRequestException;
import ewm.common.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        List<String> errors = Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .toList();
        String message = "Not found";
        String reason = e.getMessage();
        String status = "404";
        String timestamp = LocalDateTime.now().toString();
        return new ApiError(errors, message, reason, status, timestamp);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestException(final BadRequestException e) {
        List<String> errors = Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .toList();
        String message = "Bad request";
        String reason = e.getMessage();
        String status = "400";
        String timestamp = LocalDateTime.now().toString();
        return new ApiError(errors, message, reason, status, timestamp);
    }
}
