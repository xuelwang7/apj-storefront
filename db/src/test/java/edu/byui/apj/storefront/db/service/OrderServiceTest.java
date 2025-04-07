package edu.byui.apj.storefront.db.service;

import edu.byui.apj.storefront.db.model.CardOrder;
import edu.byui.apj.storefront.db.model.Cart;
import edu.byui.apj.storefront.db.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    private OrderRepository orderRepository;
    private CartService cartService;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        cartService = mock(CartService.class);
        orderService = new OrderService(orderRepository, cartService);
    }

    @Test
    void testSaveOrder() {
        Cart cart = new Cart();
        cart.setId("cart1");

        CardOrder order = new CardOrder();
        order.setCart(cart);

        when(cartService.getCart("cart1")).thenReturn(cart);
        when(orderRepository.save(order)).thenReturn(order);

        CardOrder saved = orderService.saveOrder(order);
        assertThat(saved).isNotNull();
        assertThat(saved.getCart()).isEqualTo(cart);
    }

    @Test
    void testGetOrder() {
        CardOrder order = new CardOrder();
        order.setId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        Optional<CardOrder> found = orderService.getOrder(1L);

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(1L);
    }
}
