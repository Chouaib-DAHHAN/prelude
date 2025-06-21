package glcc.com.prelude.repository;
import glcc.com.prelude.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;


public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    void deleteAllByCartId(UUID cartId);
}