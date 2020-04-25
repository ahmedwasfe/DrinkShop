package com.ahmet.drinkshop.RoomDatabase.LocalDatabase;

import com.ahmet.drinkshop.RoomDatabase.RoomInterfaces.CartDAO;
import com.ahmet.drinkshop.RoomDatabase.RoomInterfaces.ICartDataSource;
import com.ahmet.drinkshop.RoomDatabase.Model.Cart;

import java.util.List;

public class CartDataSource implements ICartDataSource {

    private CartDAO cartDAO;
    private static CartDataSource instance;

    public CartDataSource(CartDAO cartDAO) {
        this.cartDAO = cartDAO;
    }

    public static CartDataSource getInstance(CartDAO cartDAO){
        if (instance == null)
            instance = new CartDataSource(cartDAO);
        return instance;
    }

    @Override
    public List<Cart> getAllCartItems() {
        return cartDAO.getAllCartItems();
    }

    @Override
    public List<Cart> getCartItemById(int cartItemId) {
        return cartDAO.getCartItemById(cartItemId);
    }

    @Override
    public int getCountCartItems() {
        return cartDAO.getCountCartItems();
    }

    @Override
    public float sumPrice() {
        return cartDAO.sumPrice();
    }

    @Override
    public void clearCart() {
        cartDAO.clearCart();
    }

    @Override
    public void addItemToCart(Cart... carts) {
        cartDAO.addItemToCart(carts);
    }

    @Override
    public void updateItemFromCart(Cart... carts) {
        cartDAO.updateItemFromCart(carts);
    }

    @Override
    public void deleteItemById(Cart cart) {
        cartDAO.deleteItemById(cart);
    }
}
