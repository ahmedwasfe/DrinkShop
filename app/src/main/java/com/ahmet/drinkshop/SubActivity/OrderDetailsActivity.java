package com.ahmet.drinkshop.SubActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmet.drinkshop.Adapter.OrderDetailsAdapter;
import com.ahmet.drinkshop.Common.Common;
import com.ahmet.drinkshop.R;
import com.ahmet.drinkshop.Retrofit.IDrinkShopAPI;
import com.ahmet.drinkshop.RoomDatabase.Model.Cart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailsActivity extends AppCompatActivity {


    @BindView(R.id.txt_order_detail_id)
    TextView mTxtOrderId;
    @BindView(R.id.txt_order_detail_address)
    TextView mTxtOrderAddress;
    @BindView(R.id.txt_order_detail_price)
    TextView mTxtOrderPrice;
    @BindView(R.id.txt_order_detail_comment)
    TextView mTxtOrderComment;
    @BindView(R.id.txt_order_detail_status)
    TextView mTxtOrderStatus;

    @BindView(R.id.recycler_order_details)
    RecyclerView mRecyclerOederDetails;

    @OnClick(R.id.btn_cancel_order)
    void btnCancelOrder(){
        cancelOrder();
    }

    private CompositeDisposable mDisposable;
    private IDrinkShopAPI mServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        ButterKnife.bind(this);

        loadOrder();
        loadOrderDetails();

        init();
    }

    private void loadOrderDetails() {

        List<Cart> mListOrderDetails = new Gson().fromJson(Common.currentOrder.getOrderDetails(),
                new TypeToken<List<Cart>>() {}.getType());
        OrderDetailsAdapter orderDetailsAdapter = new OrderDetailsAdapter(this, mListOrderDetails);
        mRecyclerOederDetails.setAdapter(orderDetailsAdapter);
    }

    private void loadOrder() {

        mTxtOrderId.setText(new StringBuilder("#").append(Common.currentOrder.getOrderId()));
        mTxtOrderAddress.setText(Common.currentOrder.getOrderAddress());
        mTxtOrderComment.setText(Common.currentOrder.getOrderComment());
        mTxtOrderPrice.setText(new StringBuilder("$").append(Common.currentOrder.getOrderPrice()));
        mTxtOrderStatus.setText(new StringBuilder("Order Status : ")
                .append(Common.convertCodeToStatus(Common.currentOrder.getOrderStatus())));

    }

    private void cancelOrder(){

        mServices.cancelOrder(String.valueOf(Common.currentOrder.getOrderId()),
                              Common.currentUser.getPhone())
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Toast.makeText(OrderDetailsActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                        if (response.body().contains("ORDER has been cancelled"))
                            finish();

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("CANCEL_ORDER_ERROR", t.getMessage());
                    }
                });
    }

    private void init() {

        mServices = Common.getAPI();
        mDisposable = new CompositeDisposable();

        mRecyclerOederDetails.setHasFixedSize(true);
        mRecyclerOederDetails.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayout.VERTICAL));
    }

    @Override
    protected void onDestroy() {
        mDisposable.dispose();
        super.onDestroy();
    }
}
