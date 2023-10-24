package com.example.demo;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;

public class TestUtils {

    public static void injectObjects(Object target, String fieldName, Object toInject){
        boolean wasPrivate = false;
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            if(!f.isAccessible()){
                f.setAccessible(true);
                wasPrivate = true;
            }
            f.set(target, toInject);
            if(wasPrivate){
                f.setAccessible(false);
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static User createUser() {
        User user = new User(1L, "TestUsername", "password", createCart());
        user.getCart().setUser(user);
        return user;
    }
    public static Cart createCart() {
        List<Item> items = createItems();
        BigDecimal total = items.stream().map(Item::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Cart(1L, items, total);
    }
    public static List<Item> createItems() {
        List<Item> items = new ArrayList<>();
        for (long i = 1; i <= 2; i++) {
            items.add(new Item(i, BigDecimal.valueOf(i * 1.2), "Item " + i, "Description"));
        }
        return items;
    }

    public static List<UserOrder> createOrders() {
        List<UserOrder> orders = new ArrayList<>();
        IntStream.range(0, 2).forEach(i -> {
            UserOrder order = new UserOrder(createCart(), createUser(), Long.valueOf(i));
            orders.add(order);
        });
        return orders;
    }
}
