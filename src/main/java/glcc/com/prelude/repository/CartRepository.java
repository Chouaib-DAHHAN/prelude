package glcc.com.prelude.repository;

import glcc.com.prelude.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {

    // Find cart by user ID with items loaded
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.userId = :userId")
    Optional<Cart> findByUserIdWithItems(@Param("userId") UUID userId);

    // Simplified version without items
    Optional<Cart> findByUserId(UUID userId);
    
    // Check if cart exists for user
    boolean existsByUserId(UUID userId);
}
