package edu.byui.apj.storefront.db.service;

import edu.byui.apj.storefront.db.model.Cart;
import edu.byui.apj.storefront.db.model.Item;
import edu.byui.apj.storefront.db.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    private Cart testCart;
    private Item testItem;

    @BeforeEach
    public void setup() {
        // Initialize test data
        testCart = new Cart();
        testCart.setId("cart1");
        testCart.setPersonId("person1");
        testCart.setItems(new ArrayList<>());

        testItem = new Item();
        testItem.setId(1L);
        testItem.setCardId("card1");
        testItem.setName("Test Card");
        testItem.setPrice(2.99);
        testItem.setQuantity(1);
        testItem.setCart(testCart);

        testCart.getItems().add(testItem);
    }

    @Test
    public void testGetCartsWithoutOrders() {
        // Given
        List<Cart> carts = Arrays.asList(testCart);
        given(cartRepository.findCartsWithoutOrders()).willReturn(carts);

        // When
        List<Cart> result = cartService.getCartsWithoutOrders();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("cart1");
        verify(cartRepository, times(1)).findCartsWithoutOrders();
    }

    @Test
    public void testGetCart() {
        // Given
        given(cartRepository.findById("cart1")).willReturn(Optional.of(testCart));

        // When
        Cart result = cartService.getCart("cart1");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("cart1");
        verify(cartRepository, times(1)).findById("cart1");
    }

    @Test
    public void testGetCartNotFound() {
        // Given
        given(cartRepository.findById("nonexistent")).willReturn(Optional.empty());

        // When/Then
        assertThrows(RuntimeException.class, () -> cartService.getCart("nonexistent"));
        verify(cartRepository, times(1)).findById("nonexistent");
    }

    @Test
    public void testSaveCart() {
        // Given
        given(cartRepository.save(any(Cart.class))).willReturn(testCart);

        // When
        Cart savedCart = cartService.saveCart(testCart);

        // Then
        assertThat(savedCart).isNotNull();
        assertThat(savedCart.getId()).isEqualTo("cart1");
        verify(cartRepository, times(1)).save(testCart);
    }

    @Test
    public void testAddItemToCart() {
        // Given
        Cart cartWithoutItems = new Cart();
        cartWithoutItems.setId("cart2");
        cartWithoutItems.setPersonId("person2");
        cartWithoutItems.setItems(new ArrayList<>());

        Item newItem = new Item();
        newItem.setId(2L);
        newItem.setCardId("card2");
        newItem.setName("New Card");
        newItem.setPrice(3.99);
        newItem.setQuantity(1);

        given(cartRepository.findById("cart2")).willReturn(Optional.of(cartWithoutItems));

        Cart updatedCart = new Cart();
        updatedCart.setId("cart2");
        updatedCart.setPersonId("person2");
        updatedCart.setItems(new ArrayList<>());
        newItem.setCart(updatedCart);
        updatedCart.getItems().add(newItem);

        given(cartRepository.save(any(Cart.class))).willReturn(updatedCart);

        // When
        Cart result = cartService.addItemToCart("cart2", newItem);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getId()).isEqualTo(2L);
        verify(cartRepository, times(1)).findById("cart2");
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    public void testRemoveCart() {
        // Given
        doNothing().when(cartRepository).deleteById(anyString());

        // When
        cartService.removeCart("cart1");

        // Then
        verify(cartRepository, times(1)).deleteById("cart1");
    }

    @Test
    public void testRemoveItemFromCart() {
        // Given
        given(cartRepository.findById("cart1")).willReturn(Optional.of(testCart));

        Cart updatedCart = new Cart();
        updatedCart.setId("cart1");
        updatedCart.setPersonId("person1");
        updatedCart.setItems(new ArrayList<>());

        given(cartRepository.save(any(Cart.class))).willReturn(updatedCart);

        // When
        Cart result = cartService.removeItemFromCart("cart1", 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getItems()).isEmpty();
        verify(cartRepository, times(1)).findById("cart1");
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    public void testUpdateCartItem() {
        // Given
        given(cartRepository.findById("cart1")).willReturn(Optional.of(testCart));

        Item updatedItem = new Item();
        updatedItem.setId(1L);
        updatedItem.setCardId("card1");
        updatedItem.setName("Updated Card");
        updatedItem.setPrice(4.99);
        updatedItem.setQuantity(2);

        Cart updatedCart = new Cart();
        updatedCart.setId("cart1");
        updatedCart.setPersonId("person1");
        updatedCart.setItems(new ArrayList<>());
        updatedItem.setCart(updatedCart);
        updatedCart.getItems().add(updatedItem);

        given(cartRepository.save(any(Cart.class))).willReturn(updatedCart);

        // When
        Cart result = cartService.updateCartItem("cart1", updatedItem);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getName()).isEqualTo("Updated Card");
        assertThat(result.getItems().get(0).getPrice()).isEqualTo(4.99);
        assertThat(result.getItems().get(0).getQuantity()).isEqualTo(2);
        verify(cartRepository, times(1)).findById("cart1");
        verify(cartRepository, times(1)).save(any(Cart.class));
    }
}