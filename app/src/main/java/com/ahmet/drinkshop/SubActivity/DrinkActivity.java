package com.ahmet.drinkshop.SubActivity;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ahmet.drinkshop.Adapter.DrinkAdapter;
import com.ahmet.drinkshop.Common.Common;
import com.ahmet.drinkshop.Model.Drink;
import com.ahmet.drinkshop.R;
import com.ahmet.drinkshop.Retrofit.IDrinkShopAPI;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DrinkActivity extends AppCompatActivity {

    @BindView(R.id.recycler_drink)
    RecyclerView mRecyclerDrink;
    @BindView(R.id.img_category_drink)
    ImageView mImgCategoryDrink;
    @BindView(R.id.txt_category_drink_name)
    TextView mTxtCategoryDrinkName;
    @BindView(R.id.swipe_refresh_product)
    SwipeRefreshLayout mSwipeRefreshProduct;


    private IDrinkShopAPI mServices;
    // RXJava
    CompositeDisposable mDisposable;

    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink);

        ButterKnife.bind(this);

        init();



        mSwipeRefreshProduct.post(new Runnable() {
            @Override
            public void run() {

                mSwipeRefreshProduct.setRefreshing(true);
                loadDrinks(Common.currentCategory.getID());
                loadCategoryData();
            }
        });

        mSwipeRefreshProduct.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshProduct.setRefreshing(true);
                loadDrinks(Common.currentCategory.getID());
                loadCategoryData();
            }
        });

    }

    private void loadCategoryData() {

        Picasso.get()
                .load(Common.currentCategory.getLink())
                .placeholder(R.drawable.banner_drink)
                .into(mImgCategoryDrink);
        mTxtCategoryDrinkName.setText(Common.currentCategory.getName());
    }

    private void loadDrinks(String categoryId) {

        mDisposable.add(mServices.getDrinks(categoryId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(mListDrink -> displayDrink(mListDrink)));

    }

    private void displayDrink(List<Drink> mListDrink) {

        DrinkAdapter drinkAdapter = new DrinkAdapter(this, mListDrink);
        mRecyclerDrink.setAdapter(drinkAdapter);
        mSwipeRefreshProduct.setRefreshing(false);
    }

    private void init() {

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .build();

        mDisposable = new CompositeDisposable();
        mServices = Common.getAPI();

        mRecyclerDrink.setHasFixedSize(true);
        mRecyclerDrink.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayout.VERTICAL));
    }

    @Override
    protected void onDestroy() {
        mDisposable.dispose();
        super.onDestroy();
    }
}
