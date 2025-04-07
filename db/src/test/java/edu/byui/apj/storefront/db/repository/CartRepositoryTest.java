package edu.byui.apj.storefront.db.repository;

import edu.byui.apj.storefront.db.model.Cart;
import edu.byui.apj.storefront.db.model.CardOrder;
import edu.byui.apj.storefront.db.model.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CartRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CartRepository cartRepository;

    @Test
    public void testSaveCart() {
        // Create a cart with items
        Cart cart = new Cart();
        cart.setId("cart1");
        cart.setPersonId("person1");
        cart.setItems(new ArrayList<>());

        Item item = new Item();
        item.setId(1L);
        item.setCardId("card1");
        item.setName("Test Card");
        item.setPrice(2.99);
        item.setQuantity(1);
        item.setCart(cart);

        cart.getItems().add(item);

        // Save the cart
        Cart savedCart = cartRepository.save(cart);

        // Verify the cart was saved correctly
        assertThat(savedCart).isNotNull();
        assertThat(savedCart.getId()).isEqualTo("cart1");
        assertThat(savedCart.getItems()).hasSize(1);
    }

    @Test
    public void testFindCartById() {
        // Create and persist a cart
        Cart cart = new Cart();
        cart.setId("cart2");
        cart.setPersonId("person2");
        cart.setItems(new ArrayList<>());

        entityManager.persist(cart);
        entityManager.flush();

        // Find the cart by ID
        Optional<Cart> foundCart = cartRepository.findById("cart2");

        // Verify the cart was found
        assertThat(foundCart).isPresent();
        assertThat(foundCart.get().getId()).isEqualTo("cart2");
        assertThat(foundCart.get().getPersonId()).isEqualTo("person2");
    }

    @Test
    public void testFindCartsWithoutOrders() {
        // Create several carts
        Cart cart1 = new Cart();
        cart1.setId("cart3");
        cart1.setPersonId("person3");
        cart1.setItems(new ArrayList<>());

        Cart cart2 = new Cart();
        cart2.setId("cart4");
        cart2.setPersonId("person4");
        cart2.setItems(new ArrayList<>());

        Cart cart3 = new Cart();
        cart3.setId("cart5");
        cart3.setPersonId("person5");
        cart3.setItems(new ArrayList<>());

        // Persist all carts
        entityManager.persist(cart1);
        entityManager.persist(cart2);
        entityManager.persist(cart3);

        // Create an order for cart2 only
        CardOrder order = new CardOrder();
        order.setCart(cart2);
        entityManager.persist(order);

        entityManager.flush();

        // Find carts without orders
        List<Cart> cartsWithoutOrders = cartRepository.findCartsWithoutOrders();

        // Verify only the carts without orders are returned
        assertThat(cartsWithoutOrders).hasSize(2);
        assertThat(cartsWithoutOrders).extracting(Cart::getId).containsExactlyInAnyOrder("cart3", "cart5");
        assertThat(cartsWithoutOrders).extracting(Cart::getId).doesNotContain("cart4");
    }

    @Test
    public void testDeleteCartById() {
        // Create and persist a cart
        Cart cart = new Cart();
        cart.setId("cart6");
        cart.setPersonId("person6");
        cart.setItems(new ArrayList<>());

        entityManager.persist(cart);
        entityManager.flush();

        // Delete the cart
        cartRepository.deleteById("cart6");

        // Verify the cart was deleted
        Optional<Cart> foundCart = cartRepository.findById("cart6");
        assertThat(foundCart).isNotPresent();
    }
}