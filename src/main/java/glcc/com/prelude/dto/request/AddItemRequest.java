package glcc.com.prelude.dto.request;

import jakarta.validation.constraints.*;
import java.util.UUID;

public record AddItemRequest(
    @NotNull(message = "Product ID cannot be null")
    UUID productId,
    
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 100, message = "Quantity cannot exceed 100")
    int quantity
) {}