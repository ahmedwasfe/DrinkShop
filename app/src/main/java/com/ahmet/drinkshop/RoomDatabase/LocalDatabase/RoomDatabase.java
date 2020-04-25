package com.ahmet.drinkshop.RoomDatabase.LocalDatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;

import com.ahmet.drinkshop.RoomDatabase.Model.Cart;
import com.ahmet.drinkshop.RoomDatabase.Model.Favorite;
import com.ahmet.drinkshop.RoomDatabase.RoomInterfaces.CartDAO;
import com.ahmet.drinkshop.RoomDatabase.RoomInterfaces.FavoriteDAO;

@Database(entities = {Cart.class, Favorite.class}, version = 1)
public abstract class RoomDatabase extends androidx.room.RoomDatabase {

    private static RoomDatabase instance;

    public abstract CartDAO cartDAO();
    public abstract FavoriteDAO favoriteDAO();

    public static RoomDatabase getInstance(Context mContext){

        if (instance == null)
            instance = Room
                    .databaseBuilder(mContext, RoomDatabase.class,"CartDB")
                    .allowMainThreadQueries()
                    .build();

        return instance;
    }

}
