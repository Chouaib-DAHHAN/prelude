package glcc.com.prelude.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;
import java.util.UUID;

@FeignClient(name = "product-service", url = "${services.repertoire.url}")
public interface ProductServiceClient {

    @GetMapping("/products/meta/{productId}")
    Optional<ProductDto> getProduct(@PathVariable UUID productId);

    record ProductDto(UUID id, String name, double price) {}
}
