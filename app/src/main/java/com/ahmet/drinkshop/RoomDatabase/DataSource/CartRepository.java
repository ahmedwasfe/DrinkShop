package com.ahmet.drinkshop.RoomDatabase.DataSource;

import com.ahmet.drinkshop.RoomDatabase.Model.Cart;
import com.ahmet.drinkshop.RoomDatabase.RoomInterfaces.ICartDataSource;

import java.util.List;

public class CartRepository implements ICartDataSource {

    private ICartDataSource iCartDataSource;

    public CartRepository(ICartDataSource iCartDataSource) {
        this.iCartDataSource = iCartDataSource;
    }

    private static CartRepository instance;

    public static CartRepository getInstance(ICartDataSource iCartDataSource){
        if (instance == null)
            instance = new CartRepository(iCartDataSource);
        return instance;
    }

    @Override
    public List<Cart> getAllCartItems() {
        return iCartDataSource.getAllCartItems();
    }

    @Override
    public List<Cart> getCartItemById(int cartItemId) {
        return iCartDataSource.getCartItemById(cartItemId);
    }

    @Override
    public int getCountCartItems() {
        return iCartDataSource.getCountCartItems();
    }

    @Override
    public float sumPrice() {
        return iCartDataSource.sumPrice();
    }

    @Override
    public void clearCart() {
        iCartDataSource.clearCart();
    }

    @Override
    public void addItemToCart(Cart... carts) {
        iCartDataSource.addItemToCart(carts);
    }

    @Override
    public void updateItemFromCart(Cart... carts) {
        iCartDataSource.updateItemFromCart(carts);
    }

    @Override
    public void deleteItemById(Cart cart) {
        iCartDataSource.deleteItemById(cart);
    }
}
