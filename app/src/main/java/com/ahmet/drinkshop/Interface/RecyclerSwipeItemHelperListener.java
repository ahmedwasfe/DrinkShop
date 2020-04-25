package com.ahmet.drinkshop.Interface;


import androidx.recyclerview.widget.RecyclerView;

public interface RecyclerSwipeItemHelperListener {

    void onSwipe(RecyclerView.ViewHolder holder, int direction, int position);
}
