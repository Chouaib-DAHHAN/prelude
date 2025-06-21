package glcc.com.prelude.dto.response;

import java.time.Instant;
public record ErrorResponse(
    String message,
    String errorCode,  // Optional: Add error codes like "CART_NOT_FOUND"
    Instant timestamp  // Optional: Add timestamp for debugging
) {
    public ErrorResponse(String message) {
        this(message, null, Instant.now());
    }
}