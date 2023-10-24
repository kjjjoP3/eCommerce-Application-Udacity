package com.example.demo.controller;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;
    private ItemRepository itemRepository;

    @Before
    public void setup() {
        itemRepository = mock(ItemRepository.class);
        itemController = new ItemController(itemRepository);
    }

    @Test
    public void testGetItems() {
        List<Item> items = Arrays.asList(
                new Item(1L, "Item 1", "Description 1", BigDecimal.valueOf(9.99)),
                new Item(2L, "Item 2", "Description 2", BigDecimal.valueOf(19.99))
        );

        when(itemRepository.findAll()).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItems();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(items, response.getBody());
    }

    @Test
    public void testGetItemById() {
        Item item = new Item(1L, "Item 1", "Description 1", BigDecimal.valueOf(9.99));

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(item, response.getBody());
    }

    @Test
    public void testGetItemsByName() {
        List<Item> items = Arrays.asList(
                new Item(1L, "Item 1", "Description 1", BigDecimal.valueOf(9.99)),
                new Item(2L, "Item 1", "Description 2", BigDecimal.valueOf(19.99))
        );

        when(itemRepository.findByName("Item 1")).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItemsByName("Item 1");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(items, response.getBody());
    }

    @Test
    public void testGetItemsByNameWhenNotFound() {
        when(itemRepository.findByName("NonExistentItem")).thenReturn(Arrays.asList());

        ResponseEntity<List<Item>> response = itemController.getItemsByName("NonExistentItem");

        assertEquals(404, response.getStatusCodeValue());
    }
}

