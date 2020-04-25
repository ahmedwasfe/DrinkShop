package com.ahmet.drinkshop.RoomDatabase.LocalDatabase;


import com.ahmet.drinkshop.RoomDatabase.Model.Favorite;
import com.ahmet.drinkshop.RoomDatabase.RoomInterfaces.FavoriteDAO;
import com.ahmet.drinkshop.RoomDatabase.RoomInterfaces.IFavoriteDataSource;

import java.util.List;

public class FavoriteDataSource implements IFavoriteDataSource {

    private FavoriteDAO favoriteDAO;
    private static FavoriteDataSource instance;

    public FavoriteDataSource(FavoriteDAO favoriteDAO) {
        this.favoriteDAO = favoriteDAO;
    }

    public static FavoriteDataSource getInstance(FavoriteDAO favoriteDAO){
        if (instance == null)
            instance = new FavoriteDataSource(favoriteDAO);
        return instance;
    }

    @Override
    public List<Favorite> getAllFavoritesItems() {
        return favoriteDAO.getAllFavoritesItems();
    }

    @Override
    public int isFavoriteExist(int itemId) {
        return favoriteDAO.isFavoriteExist(itemId);
    }

    @Override
    public int getCountFavoriteItems() {
        return favoriteDAO.getCountFavoriteItems();
    }

    @Override
    public void addToFavoritr(Favorite... favorites) {
        favoriteDAO.addToFavoritr(favorites);
    }

    @Override
    public void clearFavorite(Favorite favorite) {
        favoriteDAO.clearFavorite(favorite);
    }
}
