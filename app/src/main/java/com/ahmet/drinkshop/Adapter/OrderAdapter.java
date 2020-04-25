package com.ahmet.drinkshop.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmet.drinkshop.Common.Common;
import com.ahmet.drinkshop.Interface.IRecyclerItemClickListener;
import com.ahmet.drinkshop.Model.Orders;
import com.ahmet.drinkshop.R;
import com.ahmet.drinkshop.SubActivity.OrderDetailsActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderHolder> {

    private Context mContext;
    private List<Orders> mListOrder;
    private LayoutInflater inflater;

    public OrderAdapter(Context mContext, List<Orders> mListOrder) {
        this.mContext = mContext;
        this.mListOrder = mListOrder;

        inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = inflater.inflate(R.layout.raw_order, parent, false);
        return new OrderHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHolder holder, int position) {

        holder.mTxtOrderId.setText(new StringBuilder("#")
                  .append(mListOrder.get(position).getOrderId())
                   .toString());

        holder.mTxtOrderPrice.setText(new StringBuilder("$")
                .append(mListOrder.get(position).getOrderPrice())
                .toString());

        holder.mTxtOrderComment.setText(mListOrder.get(position).getOrderComment());
        holder.mTxtOrderAddress.setText(mListOrder.get(position).getOrderAddress());

        holder.mTxtOrderStatus.setText(new StringBuilder("Order Status : ")
                .append(Common.convertCodeToStatus(mListOrder.get(position).getOrderStatus()))
                .toString());

        holder.setiRecyclerItemClickListener(new IRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Common.currentOrder = mListOrder.get(position);
                mContext.startActivity(new Intent(mContext, OrderDetailsActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListOrder.size();
    }

    class OrderHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.txt_order_id)
        TextView mTxtOrderId;
        @BindView(R.id.txt_order_address)
        TextView mTxtOrderAddress;
        @BindView(R.id.txt_order_price)
        TextView mTxtOrderPrice;
        @BindView(R.id.txt_order_comment)
        TextView mTxtOrderComment;
        @BindView(R.id.txt_order_status)
        TextView mTxtOrderStatus;

        private IRecyclerItemClickListener iRecyclerItemClickListener;

        public OrderHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        public void setiRecyclerItemClickListener(IRecyclerItemClickListener iRecyclerItemClickListener) {
            this.iRecyclerItemClickListener = iRecyclerItemClickListener;
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }
}