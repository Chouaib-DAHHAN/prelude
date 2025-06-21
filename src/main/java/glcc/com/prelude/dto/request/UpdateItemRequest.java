package glcc.com.prelude.dto.request;

import jakarta.validation.constraints.Min;

public record UpdateItemRequest(
    @Min(1) int quantity
) {}