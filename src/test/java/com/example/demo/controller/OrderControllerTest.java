package com.example.demo.controller;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.springframework.http.ResponseEntity;
import static org.mockito.ArgumentMatchers.any;

import com.example.demo.TestUtils;
import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

public class OrderControllerTest {

    private OrderController orderController;
    private UserRepository userRepository;
    private OrderRepository orderRepository;

    @Before
    public void setup() {
        userRepository = mock(UserRepository.class);
        orderRepository = mock(OrderRepository.class);
        orderController = new OrderController(userRepository, orderRepository);
    }

    @Test
    public void testSubmitValidOrder() {
    	User user = TestUtils.createUser();

		when(userRepository.findByUsername("TestUsername")).thenReturn(user);
		when(orderRepository.findByUser(any())).thenReturn(TestUtils.createOrders());
		
    	ResponseEntity<UserOrder> response = orderController.submit("TestUsername");
		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());

		UserOrder order = response.getBody();

		assertEquals(TestUtils.createItems(), order.getItems());
		assertEquals(TestUtils.createUser().getId(), order.getUser().getId());

		verify(orderRepository, times(1)).save(order);
    }



    @Test
    public void testSubmitInvalidUser() {
        when(userRepository.findByUsername("InvalidUser")).thenReturn(null);

        ResponseEntity<UserOrder> response = orderController.submit("InvalidUser");

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    public void testGetOrdersForUser() {
        User user = new User();
        user.setUsername("TestUsername");

        when(userRepository.findByUsername("TestUsername")).thenReturn(user);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("TestUsername");

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetOrdersForInvalidUser() {
        when(userRepository.findByUsername("InvalidUser")).thenReturn(null);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("InvalidUser");

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }
}


