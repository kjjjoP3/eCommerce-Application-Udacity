package com.example.demo.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void create_user_happy_path() {
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("testPassword");
        request.setConfirmPassword("testPassword");

        final ResponseEntity<User> response = userController.createUser(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();

        assertNotNull(user);

        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());

    }
    
    @Test
    public void testFindById() {
        long id = 1L;
        User user = new User();
        user.setUsername("test");
        user.setPassword("testPassword");
        user.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.findById(id);

        validateUserResponse(response, id, "test", "testPassword");
    }

    @Test
    public void testFindByUserName() {
        long id = 1L;
        User user = new User();
        user.setUsername("test");
        user.setPassword("testPassword");
        user.setId(id);

        when(userRepository.findByUsername("test")).thenReturn(user);

        ResponseEntity<User> response = userController.findByUserName("test");

        validateUserResponse(response, id, "test", "testPassword");
    }

    private void validateUserResponse(ResponseEntity<User> response, long id, String username, String password) {
        assertEquals(200, response.getStatusCodeValue());

        User actualUser = response.getBody();
        assertNotNull(actualUser);
        assertEquals(id, actualUser.getId());
        assertEquals(username, actualUser.getUsername());
        assertEquals(password, actualUser.getPassword());
    }
}
