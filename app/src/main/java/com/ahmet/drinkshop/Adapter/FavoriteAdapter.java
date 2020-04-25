package com.ahmet.drinkshop.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmet.drinkshop.R;
import com.ahmet.drinkshop.RoomDatabase.Model.Favorite;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteHolder> {

    private Context mContext;
    private List<Favorite> mLIstFavorite;
    private LayoutInflater inflater;

    public FavoriteAdapter(Context mContext, List<Favorite> mLIstFavorite) {
        this.mContext = mContext;
        this.mLIstFavorite = mLIstFavorite;

        inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public FavoriteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = inflater.inflate(R.layout.raw_favorite, parent, false);
        return new FavoriteHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteHolder holder, int position) {

        holder.mTxtDrinkName.setText(mLIstFavorite.get(position).getName());
        holder.mTxtDrinkPrice.setText(new StringBuilder("$")
                .append(mLIstFavorite.get(position).getPrice()).toString());
        Picasso.get()
                .load(mLIstFavorite.get(position).getImage())
                .placeholder(R.drawable.drink_shop_bg)
                .into(holder.mImgDrink);

        Log.d("IMAGE_LINK", mLIstFavorite.get(position).getImage());

    }

    @Override
    public int getItemCount() {
        return mLIstFavorite.size();
    }

    public void removeItem(int postion){
        mLIstFavorite.remove(postion);
        notifyItemRemoved(postion);
    }

    public void restoreItem(Favorite item, int position){
        mLIstFavorite.add(position, item);
        notifyItemInserted(position);
    }

    public class FavoriteHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.relative_background_favorite)
        public RelativeLayout mRelativeBackground;
        @BindView(R.id.constraint_foreground_favorite)
        public ConstraintLayout mLinearForeground;

        @BindView(R.id.img_drink_favorite)
        ImageView mImgDrink;

        @BindView(R.id.txt_drink_name_favorite)
        TextView mTxtDrinkName;
        @BindView(R.id.txt_drink_price_favorite)
        TextView mTxtDrinkPrice;



        public FavoriteHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}