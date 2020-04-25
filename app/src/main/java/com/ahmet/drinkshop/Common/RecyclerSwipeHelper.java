package com.ahmet.drinkshop.Common;

import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmet.drinkshop.Adapter.CartAdapter;
import com.ahmet.drinkshop.Adapter.FavoriteAdapter;
import com.ahmet.drinkshop.Interface.RecyclerSwipeItemHelperListener;

public class RecyclerSwipeHelper extends ItemTouchHelper.SimpleCallback {

    /**
     * Creates a Callback for the given drag and swipe allowance. These values serve as
     * defaults
     * and if you want to customize behavior per ViewHolder, you can override
     * {@link #getSwipeDirs(RecyclerView, ViewHolder)}
     * and / or {@link #getDragDirs(RecyclerView, ViewHolder)}.
     *
     * @param dragDirs  Binary OR of direction flags in which the Views can be dragged. Must be
     *                  composed of {@link #LEFT}, {@link #RIGHT}, {@link #START}, {@link
     *                  #END},
     *                  {@link #UP} and {@link #DOWN}.
     * @param swipeDirs Binary OR of direction flags in which the Views can be swiped. Must be
     *                  composed of {@link #LEFT}, {@link #RIGHT}, {@link #START}, {@link
     *                  #END},
     *                  {@link #UP} and {@link #DOWN}.
     */

    private RecyclerSwipeItemHelperListener mSwipeListener;

    public RecyclerSwipeHelper(int dragDirs, int swipeDirs, RecyclerSwipeItemHelperListener mSwipeListener) {
        super(dragDirs, swipeDirs);
        this.mSwipeListener = mSwipeListener;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        if (mSwipeListener != null)
            mSwipeListener.onSwipe(viewHolder, direction, viewHolder.getAdapterPosition());
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

        if (viewHolder instanceof FavoriteAdapter.FavoriteHolder){
            View foregroundView = ((FavoriteAdapter.FavoriteHolder)viewHolder).mLinearForeground;
            getDefaultUIUtil().clearView(foregroundView);

        }else if (viewHolder instanceof CartAdapter.CartHolder){
            View foregroundView = ((CartAdapter.CartHolder)viewHolder).mConstraintForeground;
            getDefaultUIUtil().clearView(foregroundView);
        }
        super.clearView(recyclerView, viewHolder);
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);

        if (viewHolder != null){
            if (viewHolder instanceof FavoriteAdapter.FavoriteHolder){

                View foregroundView = ((FavoriteAdapter.FavoriteHolder)viewHolder).mLinearForeground;
                getDefaultUIUtil().onSelected(foregroundView);

            }else if (viewHolder instanceof CartAdapter.CartHolder){
                View foregroundView = ((CartAdapter.CartHolder)viewHolder).mConstraintForeground;
                getDefaultUIUtil().onSelected(foregroundView);
            }
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        if (viewHolder instanceof FavoriteAdapter.FavoriteHolder){
            View foregroundView = ((FavoriteAdapter.FavoriteHolder)viewHolder).mLinearForeground;
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        }else if (viewHolder instanceof CartAdapter.CartHolder){
            View foregroundView = ((CartAdapter.CartHolder)viewHolder).mConstraintForeground;
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (viewHolder instanceof FavoriteAdapter.FavoriteHolder){
            View foregroundView = ((FavoriteAdapter.FavoriteHolder)viewHolder).mLinearForeground;
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        }else if (viewHolder instanceof CartAdapter.CartHolder){
            View foregroundView = ((CartAdapter.CartHolder)viewHolder).mConstraintForeground;
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        }
    }
}
