package com.ahmet.drinkshop.Adapter;

import android.content.Context;
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

import com.ahmet.drinkshop.RoomDatabase.Model.Cart;
import com.ahmet.drinkshop.Common.Common;
import com.ahmet.drinkshop.R;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartHolder> {

    private Context mContext;
    private List<Cart> mListCart;
    private LayoutInflater inflater;

    public CartAdapter(Context mContext, List<Cart> mListCart) {
        this.mContext = mContext;
        this.mListCart = mListCart;

        inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = inflater.inflate(R.layout.raw_cart, parent, false);
        return new CartHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartHolder holder, int position) {

        Picasso.get()
                .load(mListCart.get(position).getImage())
                .placeholder(R.drawable.drink_shop_bg)
                .into(holder.mImgItem);
        holder.mQuantityItem.setNumber(String.valueOf(mListCart.get(position).getAmount()));
        holder.mTxtItemPrice.setText(new StringBuilder("$").append(mListCart.get(position).getPrice()));
        holder.mItemName.setText(new StringBuilder(mListCart.get(position).getName())
                                    .append(" x")
                                    .append(mListCart.get(position).getAmount())
                                    .append(mListCart.get(position).getSize() == 0 ? " Size M" : " Size L"));

        holder.mTxtItemSugar.setText(new StringBuilder("Sugar: ")
              .append(mListCart.get(position).getSugar()).append("%").append("\n")
              .append("Ice: ").append(mListCart.get(position).getIce()).append("%")
              .toString());



        // Get Price of one cup with all options
        double oneCupPrice = mListCart.get(position).getPrice() / mListCart.get(position).getAmount();

        // Auto Save item when user change quantity
        holder.mQuantityItem.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Cart cart = mListCart.get(position);
                cart.setAmount(newValue);
                cart.setPrice(Math.round(oneCupPrice * newValue));

                Common.mCartRepository.updateItemFromCart(cart);

                holder.mTxtItemPrice.setText(new StringBuilder("$")
                                    .append(mListCart.get(position).getPrice()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListCart.size();
    }

    public void removeItem(int position){
        mListCart.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Cart item, int position){
        mListCart.add(position, item);
        notifyItemInserted(position);
    }

    public class CartHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.relative_background_cart)
        public RelativeLayout mRelativeBackground;
        @BindView(R.id.constraint_foreground)
        public ConstraintLayout mConstraintForeground;

        ImageView mImgItem;
        TextView mItemName, mTxtItemPrice, mTxtItemSugar;
        ElegantNumberButton mQuantityItem;

        public CartHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            mImgItem = itemView.findViewById(R.id.img_item_cart);
            mItemName = itemView.findViewById(R.id.txt_name_item_cart);
            mTxtItemPrice = itemView.findViewById(R.id.txt_sugar_item_cart);
            mTxtItemSugar = itemView.findViewById(R.id.txt_price_item_cart);
            mQuantityItem = itemView.findViewById(R.id.btn_cart_quantity_count);
        }
    }
}