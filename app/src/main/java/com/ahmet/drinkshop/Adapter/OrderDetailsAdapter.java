package com.ahmet.drinkshop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmet.drinkshop.Model.Orders;
import com.ahmet.drinkshop.R;
import com.ahmet.drinkshop.RoomDatabase.Model.Cart;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.OrderDetailsHolder> {

    private Context mContext;
    private List<Cart> mListOrderDetails;
    private LayoutInflater inflater;

    public OrderDetailsAdapter(Context mContext, List<Cart> mListOrderDetails) {
        this.mContext = mContext;
        this.mListOrderDetails = mListOrderDetails;

        inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public OrderDetailsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = inflater.inflate(R.layout.raw_order_details, parent, false);
        return new OrderDetailsHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailsHolder holder, int position) {

        Picasso.get()
                .load(mListOrderDetails.get(position).getImage())
                .placeholder(R.drawable.drink_shop_bg)
                .into(holder.mImgOrder);

        holder.mTxtOrderPrice.setText(new StringBuilder("$").append(mListOrderDetails.get(position).getPrice()));
        holder.mTxtOrderName.setText(new StringBuilder(mListOrderDetails.get(position).getName())
                .append(" x")
                .append(mListOrderDetails.get(position).getAmount())
                .append(" ")
                .append(mListOrderDetails.get(position).getSize() == 0 ? " Size M" : " Size L"));

        holder.mTxtOrderSugar.setText(new StringBuilder("Sugar: ")
                .append(mListOrderDetails.get(position).getSugar()).append("%").append("\n")
                .append("Ice: ").append(mListOrderDetails.get(position).getIce()).append("%")
                .toString());

    }

    @Override
    public int getItemCount() {
        return mListOrderDetails.size();
    }

    class OrderDetailsHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img_order_detail)
        ImageView mImgOrder;
        @BindView(R.id.txt_name_order_detail)
        TextView mTxtOrderName;
        @BindView(R.id.txt_sugar_order_detail)
        TextView mTxtOrderSugar;
        @BindView(R.id.txt_price_order_detail)
        TextView mTxtOrderPrice;


        public OrderDetailsHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}