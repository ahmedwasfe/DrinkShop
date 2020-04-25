package com.ahmet.drinkshop.RoomDatabase.RoomInterfaces;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ahmet.drinkshop.RoomDatabase.Model.Cart;

import java.util.List;

@Dao
public interface CartDAO {

    @Query("SELECT * FROM cart")
    List<Cart> getAllCartItems();

    @Query("SELECT * FROM cart WHERE id=:cartItemId")
    List<Cart> getCartItemById(int cartItemId);

    @Query("SELECT COUNT(*) from cart")
    int getCountCartItems();

    @Query("SELECT SUM(price) from cart")
    float sumPrice();

    @Query("DELETE FROM cart")
    void clearCart();

    @Insert
    void addItemToCart(Cart...carts);

    @Update
    void updateItemFromCart(Cart...carts);

    @Delete
    void deleteItemById(Cart cart);


}
