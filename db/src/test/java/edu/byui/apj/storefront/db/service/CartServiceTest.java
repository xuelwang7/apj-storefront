package edu.byui.apj.storefront.db.service;

import edu.byui.apj.storefront.db.model.Cart;
import edu.byui.apj.storefront.db.model.Item;
import edu.byui.apj.storefront.db.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class CartServiceTest {

    private CartRepository cartRepository;
    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartRepository = mock(CartRepository.class);
        cartService = new CartService();
        cartService = Mockito.spy(cartService);
        cartService.setCartRepository(cartRepository);

    }

    @Test
    void testAddItemToCart() {
        Cart cart = new Cart();
        cart.setId("c1");
        cart.setItems(new ArrayList<>());
        Item item = new Item();
        item.setId(1L);

        when(cartRepository.findById("c1")).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Cart updated = cartService.addItemToCart("c1", item);
        assertThat(updated.getItems()).hasSize(1);
    }

    @Test
    void testRemoveCart() {
        cartService.removeCart("c1");
        verify(cartRepository, times(1)).deleteById("c1");
    }
}
