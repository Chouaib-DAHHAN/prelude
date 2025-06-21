package glcc.com.prelude.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import glcc.com.prelude.exception.CartNotFoundException;
import glcc.com.prelude.dto.response.ErrorResponse;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CartNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleCartNotFound(CartNotFoundException ex) {
        return new ErrorResponse(
            ex.getMessage(),
            "CART_NOT_FOUND",  // Custom error code
            Instant.now()
        );
    }
}