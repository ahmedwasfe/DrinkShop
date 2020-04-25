package com.ahmet.drinkshop.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ahmet.drinkshop.Adapter.FavoriteAdapter;
import com.ahmet.drinkshop.Common.Common;
import com.ahmet.drinkshop.Common.RecyclerSwipeHelper;
import com.ahmet.drinkshop.Interface.RecyclerSwipeItemHelperListener;
import com.ahmet.drinkshop.R;
import com.ahmet.drinkshop.RoomDatabase.Model.Favorite;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import ahmed.com.recycleradapter.Adapter.RecyclerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteFragment extends Fragment implements RecyclerSwipeItemHelperListener {

    @BindView(R.id.recycler_favorite)
    RecyclerView mRecyclerFavorite;
    @BindView(R.id.constraint_favorite)
    ConstraintLayout mConstraintFavorite;

    private FavoriteAdapter mFavoriteAdapter;
    private List<Favorite> mListLocalFavorite;

    private static FavoriteFragment instance;
    public static FavoriteFragment getInstance(){
        return instance == null ? new FavoriteFragment() : instance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View layoutView = inflater.inflate(R.layout.fragment_favorite, container, false);
        ButterKnife.bind(this, layoutView);

        init();

        getAllItemsFromFavorite();

        return layoutView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllItemsFromFavorite();
    }

    private void getAllItemsFromFavorite() {

        List<Favorite> mListFavorite = Common.mFavoriteRepository.getAllFavoritesItems();
        mListLocalFavorite = mListFavorite;
        mFavoriteAdapter = new FavoriteAdapter(getActivity(), mListFavorite);
        mRecyclerFavorite.setAdapter(mFavoriteAdapter);
    }

    private void init() {

        mRecyclerFavorite.setHasFixedSize(true);
        mRecyclerFavorite.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayout.VERTICAL));

        ItemTouchHelper.SimpleCallback simpleCallback = new
                RecyclerSwipeHelper(0,ItemTouchHelper.START, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(mRecyclerFavorite);
    }

    @Override
    public void onSwipe(RecyclerView.ViewHolder holder, int direction, int position) {

        if (holder instanceof FavoriteAdapter.FavoriteHolder){
            String name = mListLocalFavorite.get(holder.getAdapterPosition()).getName();
            Favorite deleteItemFavorite = mListLocalFavorite.get(holder.getAdapterPosition());
            int index = holder.getAdapterPosition();

            // Delete Item from adapter
            mFavoriteAdapter.removeItem(index);
            // Delete item from database
            Common.mFavoriteRepository.clearFavorite(deleteItemFavorite);

            Snackbar snackbar = Snackbar.make(mConstraintFavorite,
                    new StringBuilder(name).append("removed from favorite list").toString(),
                    Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFavoriteAdapter.restoreItem(deleteItemFavorite, index);
                    Common.mFavoriteRepository.addToFavoritr(deleteItemFavorite);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();

        }
    }
}
