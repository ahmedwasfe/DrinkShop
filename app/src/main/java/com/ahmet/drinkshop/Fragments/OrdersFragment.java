package com.ahmet.drinkshop.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.ahmet.drinkshop.Adapter.OrderAdapter;
import com.ahmet.drinkshop.Common.Common;
import com.ahmet.drinkshop.Model.Orders;
import com.ahmet.drinkshop.R;
import com.ahmet.drinkshop.Retrofit.IDrinkShopAPI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class OrdersFragment extends Fragment {

    @BindView(R.id.recycler_order)
    RecyclerView mrecyclerOrder;
    @BindView(R.id.btn_navigation_orders)
    BottomNavigationView mBottomNavigationView;

    private CompositeDisposable mDisposable;
    private IDrinkShopAPI mServices;

    private AlertDialog mDialog;

    private static OrdersFragment instance;
    public static OrdersFragment getInstance(){
        return instance == null ? new OrdersFragment() : instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View layoutView = inflater.inflate(R.layout.fragment_orders, container, false);

        ButterKnife.bind(this, layoutView);

        init();

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.nav_new:
                        loadOrderByStatus(Common.currentUser.getPhone(), "0");
                        break;
                    case R.id.nav_cancel:
                        loadOrderByStatus(Common.currentUser.getPhone(), "-1");
                        break;
                    case R.id.nav_processing:
                        loadOrderByStatus(Common.currentUser.getPhone(), "1");
                        break;
                    case R.id.nav_shipping:
                        loadOrderByStatus(Common.currentUser.getPhone(), "2");
                        break;
                    case R.id.nav_shipped:
                        loadOrderByStatus(Common.currentUser.getPhone(), "3");
                        break;
                }

                return true;
            }
        });

        loadOrderByStatus(Common.currentUser.getPhone(), "0");

        return layoutView;
    }

    private void loadOrderByStatus(String phone, String status) {

        if (Common.currentUser != null) {
           // mDialog.show();

            mDisposable.add(mServices.getOrderByStatus(phone, status)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<Orders>>() {
                        @Override
                        public void accept(List<Orders> orders) throws Exception {
                            OrderAdapter orderAdapter = new OrderAdapter(getActivity(), orders);
                            mrecyclerOrder.setAdapter(orderAdapter);
                          //  mDialog.dismiss();
                        }
                    }));
        }else{
            Toast.makeText(getActivity(), "Please login again", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    private void init() {

        mDisposable = new CompositeDisposable();
        mServices = Common.getAPI();

        mrecyclerOrder.setHasFixedSize(false);
        mrecyclerOrder.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayout.VERTICAL));

        mDialog = new SpotsDialog.Builder()
                .setCancelable(true)
                .setContext(getActivity())
                .build();
    }

    @Override
    public void onDestroy() {
        mDisposable.dispose();
        super.onDestroy();
    }
}
