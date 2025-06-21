package glcc.com.prelude.service;

import glcc.com.prelude.dto.request.AddItemRequest;
import glcc.com.prelude.dto.response.CartResponse;
import glcc.com.prelude.dto.response.CheckoutPreparation;
import glcc.com.prelude.model.Cart;
import glcc.com.prelude.model.CartItem;
import glcc.com.prelude.exception.CartItemNotFoundException;
import glcc.com.prelude.exception.CartNotFoundException;
import glcc.com.prelude.exception.ProductNotFoundException;
import glcc.com.prelude.repository.CartRepository;
import glcc.com.prelude.service.client.ProductServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

    private static final ZoneId UTC_ZONE = ZoneId.of("UTC");
    private final CartRepository cartRepository;
    private final ProductServiceClient productServiceClient;

    @Transactional(readOnly = true)
    public CartResponse getCart(UUID userId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));
        return mapToCartResponse(cart);
    }

    @Transactional
    public CartResponse addItem(UUID userId, AddItemRequest request) {
        Cart cart = getOrCreateCart(userId);
        ProductServiceClient.ProductDto product = fetchProduct(request.productId());

        CartItem newItem = buildCartItem(cart, request, product);
        cart.addOrUpdateItem(newItem);
        cart.calculateTotals();

        return mapToCartResponse(cartRepository.save(cart));
    }

    @Transactional
    public CartResponse updateItemQuantity(UUID userId, UUID itemId, int newQuantity) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException(itemId));

        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        item.setQuantity(newQuantity);
        cart.calculateTotals();

        return mapToCartResponse(cartRepository.save(cart));
    }

    @Transactional
    public void removeItem(UUID userId, UUID itemId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));

        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(itemId));

        if (!removed) {
            throw new CartItemNotFoundException(itemId);
        }

        cart.calculateTotals();
        cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(UUID userId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));

        cart.getItems().clear();
        cart.setSubtotal(0);
        cart.setTotal(0);

        cartRepository.save(cart);
    }

    @Transactional
public CheckoutPreparation prepareCheckout(UUID userId) {
    Cart cart = cartRepository.findByUserIdWithItems(userId)
        .orElseThrow(() -> new CartNotFoundException(userId));
    
    // Verify stock with METRONOME service here (would throw 409 if conflict)
    
    return new CheckoutPreparation(
        cart.getId(),
        cart.getUserId(),
        cart.getItems().stream()
            .map(item -> new CheckoutPreparation.CheckoutItem(
                item.getProductId(),
                item.getQuantity(),
                item.getUnitPrice()
            ))
            .toList(),
        cart.getTotal()
    );
}

    // ===== HELPER METHODS =====
    private Cart getOrCreateCart(UUID userId) {
        return cartRepository.findByUserIdWithItems(userId)
                .orElseGet(() -> createNewCart(userId));
    }

    private Cart createNewCart(UUID userId) {
        return cartRepository.save(
                Cart.builder()
                        .userId(userId)
                        .build());
    }

    private ProductServiceClient.ProductDto fetchProduct(UUID productId) {
        ProductServiceClient.ProductDto product = productServiceClient.getProduct(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        if (product.price() <= 0) {
            throw new IllegalStateException("Invalid price for product: " + productId);
        }
        return product;
    }

    private CartItem buildCartItem(Cart cart, AddItemRequest request,
            ProductServiceClient.ProductDto product) {
        return CartItem.builder()
                .id(UUID.randomUUID())
                .cart(cart)
                .productId(request.productId())
                .productName(product.name())
                .unitPrice(product.price())
                .quantity(request.quantity())
                .build();
    }

    private CartResponse mapToCartResponse(Cart cart) {
        Objects.requireNonNull(cart, "Cart cannot be null");

        return new CartResponse(
                cart.getId(),
                cart.getUserId(),
                cart.getItems().stream()
                        .map(this::mapToCartItemResponse)
                        .toList(),
                cart.getSubtotal(),
                cart.getTotal(),
                cart.getCreatedAt().atZone(UTC_ZONE).toInstant());
    }

    private CartResponse.CartItemResponse mapToCartItemResponse(CartItem item) {
        return new CartResponse.CartItemResponse(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getUnitPrice(),
                item.getQuantity());
    }
}