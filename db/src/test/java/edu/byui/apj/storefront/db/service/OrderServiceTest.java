package edu.byui.apj.storefront.db.service;

import edu.byui.apj.storefront.db.model.CardOrder;
import edu.byui.apj.storefront.db.model.Cart;
import edu.byui.apj.storefront.db.model.Customer;
import edu.byui.apj.storefront.db.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private CardOrder testOrder;

    @BeforeEach
    public void setup() {
        // Create a test order
        Cart cart = new Cart();
        cart.setId("cart123");
        cart.setPersonId("person123");
        cart.setItems(new ArrayList<>());

        Customer customer = new Customer();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john.doe@example.com");
        customer.setPhone("123-456-7890");

        testOrder = new CardOrder();
        testOrder.setId(1L);
        testOrder.setCart(cart);
        testOrder.setCustomer(customer);
        testOrder.setOrderDate(new Date());
        testOrder.setConfirmationSent(false);
        testOrder.setShipMethod("Standard");
        testOrder.setOrderNotes("Test order");
        testOrder.setSubtotal(19.99);
        testOrder.setTax(1.99);
        testOrder.setTotal(21.98);
    }

    @Test
    public void testSaveOrder() {
        // Given
        given(orderRepository.save(any(CardOrder.class))).willReturn(testOrder);

        // When
        CardOrder savedOrder = orderService.saveOrder(testOrder);

        // Then
        assertThat(savedOrder).isNotNull();
        assertThat(savedOrder.getId()).isEqualTo(1L);
        assertThat(savedOrder.getCart().getId()).isEqualTo("cart123");
        assertThat(savedOrder.getCustomer().getFirstName()).isEqualTo("John");
        verify(orderRepository, times(1)).save(testOrder);
    }

    @Test
    public void testGetOrderWhenOrderExists() {
        // Given
        given(orderRepository.findById(1L)).willReturn(Optional.of(testOrder));

        // When
        Optional<CardOrder> result = orderService.getOrder(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getCart().getId()).isEqualTo("cart123");
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetOrderWhenOrderDoesNotExist() {
        // Given
        given(orderRepository.findById(999L)).willReturn(Optional.empty());

        // When
        Optional<CardOrder> result = orderService.getOrder(999L);

        // Then
        assertThat(result).isEmpty();
        verify(orderRepository, times(1)).findById(999L);
    }
}