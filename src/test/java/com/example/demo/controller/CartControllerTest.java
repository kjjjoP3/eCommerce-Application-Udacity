package com.example.demo.controller;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CartControllerTest {

    private CartController cartController;
    private UserRepository userRepository;
    private CartRepository cartRepository;
    private ItemRepository itemRepository;

    @Before
    public void setup() {
        userRepository = mock(UserRepository.class);
        cartRepository = mock(CartRepository.class);
        itemRepository = mock(ItemRepository.class);
        cartController = new CartController(userRepository, cartRepository, itemRepository);
    }

    @Test
    public void testAddToCart() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(2);

        User user = new User();
        user.setUsername("testUser");
        user.setCart(new Cart());

        Item item = new Item();
        item.setId(1L);
        item.setName("TestItem");
        item.setPrice(BigDecimal.valueOf(9.99));

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Cart cart = response.getBody();
        assertEquals(BigDecimal.valueOf(19.98), cart.getTotal());
    }

    @Test
    public void testRemoveFromCart() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(1);

        User user = new User();
        user.setUsername("testUser");
        Cart cart = new Cart();
        Item item = new Item();
        item.setId(1L);
        item.setName("TestItem");
        item.setPrice(BigDecimal.valueOf(9.99));
        cart.addItem(item);
        user.setCart(cart);

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Cart updatedCart = response.getBody();
        assertEquals(0, updatedCart.getItems().size());
    }

    @Test
    public void test_InvalidUsername() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setQuantity(1);
        request.setItemId(1);
        request.setUsername("invalidUser");

        ResponseEntity<Cart> removeResponse = cartController.removeFromcart(request);
        assertNotNull(removeResponse);
        assertEquals(404, removeResponse.getStatusCodeValue());
        assertNull(removeResponse.getBody());

        ResponseEntity<Cart> addResponse = cartController.addTocart(request);
        assertNotNull(addResponse);
        assertEquals(404, addResponse.getStatusCodeValue());
        assertNull(addResponse.getBody());

        verify(userRepository, times(2)).findByUsername("invalidUser");
    }

}
