package glcc.com.prelude.dto;

import java.util.UUID;

public record ProductDto(
    UUID id,
    String name,
    double price
) {}