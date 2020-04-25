package com.ahmet.drinkshop.RoomDatabase.RoomInterfaces;

import com.ahmet.drinkshop.RoomDatabase.Model.Cart;

import java.util.List;

public interface ICartDataSource {

    List<Cart> getAllCartItems();

    List<Cart> getCartItemById(int cartItemId);

    int getCountCartItems();

    float sumPrice();

    void clearCart();

    void addItemToCart(Cart...carts);

    void updateItemFromCart(Cart...carts);

    void deleteItemById(Cart cart);
}
