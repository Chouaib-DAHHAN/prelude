package glcc.com.prelude.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CartResponse(
    UUID id,
    UUID userId,
    List<CartItemResponse> items,
    double subtotal,
    double total,
    Instant createdAt
) {
    public record CartItemResponse(
        UUID id,
        UUID productId,
        String productName,
        double unitPrice,
        int quantity
    ) {}
}
