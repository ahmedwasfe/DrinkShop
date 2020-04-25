package com.ahmet.drinkshop.RoomDatabase.RoomInterfaces;

import com.ahmet.drinkshop.RoomDatabase.Model.Favorite;

import java.util.List;

public interface IFavoriteDataSource {

    List<Favorite> getAllFavoritesItems();

    int isFavoriteExist(int itemId);

    int getCountFavoriteItems();

    void addToFavoritr(Favorite...favorites);

    void clearFavorite(Favorite favorite);
}
