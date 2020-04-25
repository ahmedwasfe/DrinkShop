package com.ahmet.drinkshop.RoomDatabase.DataSource;

import com.ahmet.drinkshop.RoomDatabase.Model.Favorite;
import com.ahmet.drinkshop.RoomDatabase.RoomInterfaces.IFavoriteDataSource;

import java.util.List;

public class FavoriteRepository implements IFavoriteDataSource {

    private IFavoriteDataSource iFavoriteDataSource;
    private static FavoriteRepository instance;

    public FavoriteRepository(IFavoriteDataSource iFavoriteDataSource) {
        this.iFavoriteDataSource = iFavoriteDataSource;
    }

    public static FavoriteRepository getInstance(IFavoriteDataSource iFavoriteDataSource){
        if (instance == null)
            instance = new FavoriteRepository(iFavoriteDataSource);
        return instance;
    }


    @Override
    public List<Favorite> getAllFavoritesItems() {
        return iFavoriteDataSource.getAllFavoritesItems();
    }

    @Override
    public int isFavoriteExist(int itemId) {
        return iFavoriteDataSource.isFavoriteExist(itemId);
    }

    @Override
    public int getCountFavoriteItems() {
        return iFavoriteDataSource.getCountFavoriteItems();
    }

    @Override
    public void addToFavoritr(Favorite... favorites) {
        iFavoriteDataSource.addToFavoritr(favorites);
    }

    @Override
    public void clearFavorite(Favorite favorite) {
        iFavoriteDataSource.clearFavorite(favorite);
    }
}
