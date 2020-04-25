package com.ahmet.drinkshop.RoomDatabase.RoomInterfaces;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.ahmet.drinkshop.RoomDatabase.Model.Favorite;

import java.util.List;

@Dao
public interface FavoriteDAO {

    @Query("SELECT * FROM favorite")
    List<Favorite> getAllFavoritesItems();

    @Query("SELECT EXISTS (SELECT 1 FROM favorite WHERE id=:itemId)")
    int isFavoriteExist(int itemId);

    @Query("SELECT COUNT(*) from favorite")
    int getCountFavoriteItems();

    @Insert
    void addToFavoritr(Favorite...favorites);

    @Delete
    void clearFavorite(Favorite favorite);


}
