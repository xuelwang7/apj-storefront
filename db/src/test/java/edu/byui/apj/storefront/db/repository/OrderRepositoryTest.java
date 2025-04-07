package edu.byui.apj.storefront.db.repository;

import edu.byui.apj.storefront.db.model.Address;
import edu.byui.apj.storefront.db.model.CardOrder;
import edu.byui.apj.storefront.db.model.Cart;
import edu.byui.apj.storefront.db.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void testSaveOrder() {
        // Create components for the order
        Cart cart = new Cart();
        cart.setId("cart123");
        cart.setPersonId("person123");
        cart.setItems(new ArrayList<>());

        Customer customer = new Customer();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john.doe@example.com");
        customer.setPhone("123-456-7890");

        Address address = new Address();
        // Assuming Address has these fields
        // Set address fields

        // Create the order
        CardOrder order = new CardOrder();
        order.setCart(cart);
        order.setCustomer(customer);
        order.setShippingAddress(address);
        order.setOrderDate(new Date());
        order.setConfirmationSent(false);
        order.setShipMethod("Standard");
        order.setOrderNotes("Test order");
        order.setSubtotal(19.99);
        order.setTax(1.99);
        order.setTotal(21.98);

        // Save the order
        CardOrder savedOrder = orderRepository.save(order);

        // Verify the order was saved with an ID
        assertThat(savedOrder).isNotNull();
        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getCart().getId()).isEqualTo("cart123");
        assertThat(savedOrder.getCustomer().getFirstName()).isEqualTo("John");
    }

    @Test
    public void testFindOrderById() {
        // Create components for the order
        Cart cart = new Cart();
        cart.setId("cart456");
        cart.setPersonId("person456");
        cart.setItems(new ArrayList<>());

        Customer customer = new Customer();
        customer.setFirstName("Jane");
        customer.setLastName("Smith");
        customer.setEmail("jane.smith@example.com");
        customer.setPhone("987-654-3210");

        // Create the order
        CardOrder order = new CardOrder();
        order.setCart(cart);
        order.setCustomer(customer);
        order.setOrderDate(new Date());
        order.setShipMethod("Express");
        order.setTotal(45.99);

        // Persist the order
        CardOrder persistedOrder = entityManager.persist(order);
        entityManager.flush();

        // Find the order by ID
        Optional<CardOrder> foundOrder = orderRepository.findById(persistedOrder.getId());

        // Verify the order was found
        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get().getCart().getId()).isEqualTo("cart456");
        assertThat(foundOrder.get().getCustomer().getFirstName()).isEqualTo("Jane");
        assertThat(foundOrder.get().getShipMethod()).isEqualTo("Express");
    }

    @Test
    public void testDeleteOrderById() {
        // Create a simple order
        CardOrder order = new CardOrder();
        order.setOrderDate(new Date());
        order.setShipMethod("Ground");
        order.setTotal(15.99);

        // Persist the order
        CardOrder persistedOrder = entityManager.persist(order);
        entityManager.flush();
        Long orderId = persistedOrder.getId();

        // Delete the order
        orderRepository.deleteById(orderId);

        // Verify the order was deleted
        Optional<CardOrder> foundOrder = orderRepository.findById(orderId);
        assertThat(foundOrder).isNotPresent();
    }
}