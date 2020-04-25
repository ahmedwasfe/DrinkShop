package com.ahmet.drinkshop.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmet.drinkshop.Common.Common;
import com.ahmet.drinkshop.SubActivity.DrinkActivity;
import com.ahmet.drinkshop.Interface.IRecyclerItemClickListener;
import com.ahmet.drinkshop.Model.Category;
import com.ahmet.drinkshop.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHplder> {

    private Context mContext;
    private List<Category> mListCategories;
    private LayoutInflater inflater;

    public CategoryAdapter(Context mContext, List<Category> mListCategories) {
        this.mContext = mContext;
        this.mListCategories = mListCategories;

        inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public CategoryHplder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layoutView = inflater.inflate(R.layout.raw_category, parent, false);

        return new CategoryHplder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHplder holder, int position) {

        Picasso.get()
                .load(mListCategories.get(position).getLink())
                .placeholder(R.drawable.drink_shop_bg)
                .into(holder.mImgCategory);
        holder.mTxtCategoryName.setText(mListCategories.get(position).getName());

        holder.setIRecyclerItemClickListener(new IRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Common.currentCategory = mListCategories.get(position);
                // start new Activity
                mContext.startActivity(new Intent(mContext, DrinkActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListCategories.size();
    }

    static class CategoryHplder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.img_category)
        ImageView mImgCategory;
        @BindView(R.id.txt_category_name)
        TextView mTxtCategoryName;

        IRecyclerItemClickListener IRecyclerItemClickListener;

        public CategoryHplder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        public void setIRecyclerItemClickListener(IRecyclerItemClickListener IRecyclerItemClickListener) {
            this.IRecyclerItemClickListener = IRecyclerItemClickListener;
        }

        @Override
        public void onClick(View view) {
            IRecyclerItemClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}
