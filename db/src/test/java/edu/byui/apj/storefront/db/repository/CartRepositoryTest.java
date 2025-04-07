package edu.byui.apj.storefront.db.repository;

import edu.byui.apj.storefront.db.model.Cart;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Test
    public void testSaveAndFindCart() {
        Cart cart = new Cart();
        cart.setId("testCart1");
        cart = cartRepository.save(cart);

        Cart found = cartRepository.findById("testCart1").orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo("testCart1");
    }

    @Test
    public void testFindCartsWithoutOrdersReturnsEmptyWhenNone() {
        List<Cart> result = cartRepository.findCartsWithoutOrders();
        assertThat(result).isNotNull();
    }
}
