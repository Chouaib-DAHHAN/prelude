// src/main/java/glcc/com/prelude/dto/response/CheckoutPreparation.java
package glcc.com.prelude.dto.response;

import java.util.List;
import java.util.UUID;

public record CheckoutPreparation(
    UUID cartId,
    UUID userId,
    List<CheckoutItem> items,
    double totalAmount
) {
    public record CheckoutItem(
        UUID productId,
        int quantity,
        double unitPrice
    ) {}
}