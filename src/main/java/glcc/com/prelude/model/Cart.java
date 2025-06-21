package glcc.com.prelude.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID userId;

    @OneToMany(
        mappedBy = "cart",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    @Column(nullable = false)
    private double subtotal;

    @Column(nullable = false)
    private double total;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Helper method to add/update items
    public void addOrUpdateItem(CartItem item) {
        items.stream()
            .filter(i -> i.getProductId().equals(item.getProductId()))
            .findFirst()
            .ifPresentOrElse(
                existingItem -> existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity()),
                () -> {
                    item.setCart(this);
                    items.add(item);
                }
            );
    }

    // Recalculate cart totals
    public void calculateTotals() {
        this.subtotal = items.stream()
            .mapToDouble(item -> item.getUnitPrice() * item.getQuantity())
            .sum();
        this.total = subtotal; // Will add discounts later
    }
}