package com.ahmet.drinkshop.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ahmet.drinkshop.Adapter.CategoryAdapter;
import com.ahmet.drinkshop.Adapter.SliderBannerAdapter;
import com.ahmet.drinkshop.Common.Common;
import com.ahmet.drinkshop.HomeActivity;
import com.ahmet.drinkshop.MainActivity;
import com.ahmet.drinkshop.Model.Banner;
import com.ahmet.drinkshop.Model.Category;
import com.ahmet.drinkshop.Model.CheckUserResponse;
import com.ahmet.drinkshop.Model.Drink;
import com.ahmet.drinkshop.Model.User;
import com.ahmet.drinkshop.R;
import com.ahmet.drinkshop.Retrofit.IDrinkShopAPI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    @BindView(R.id.slider_banner)
    SliderView mSlidingBanner;
    @BindView(R.id.recycler_category)
    RecyclerView mRecyclerCategory;
    @BindView(R.id.swipe_refresh_home)
    SwipeRefreshLayout mSwipeRefreshHome;

    private IDrinkShopAPI mServices;
    // RXJava
    private CompositeDisposable mDisposable;

    private AlertDialog mDialog;

    private static HomeFragment instance;
    public static HomeFragment getInstance(){
        return instance == null ? new HomeFragment() : instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View layoutView = inflater.inflate(R.layout.fragment_home, container, false);

        ButterKnife.bind(this, layoutView);

        init();

        mSwipeRefreshHome.post(() -> {
            // get Banners
            getBanner();

            // get Categorise
            getMenu();

            getToppingList();
        });

        mSwipeRefreshHome.setOnRefreshListener(() -> {
            mSwipeRefreshHome.setRefreshing(true);

            // get Banners
            getBanner();

            // get Categorise
            getMenu();

            getToppingList();
        });

        checkSessionLogin();

        return layoutView;
    }

    private void getBanner() {



        mDisposable.add(mServices.getBanners()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Banner>>() {
                    @Override
                    public void accept(List<Banner> mListBanner) throws Exception {

                        displayImageBanner(mListBanner);
                    }
                }));
    }

    private void getMenu() {


        mDisposable.add(mServices.getMenus()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Category>>() {
                    @Override
                    public void accept(List<Category> mListCategories) throws Exception {

                        CategoryAdapter categoryAdapter = new CategoryAdapter(getActivity(), mListCategories);
                        mRecyclerCategory.setAdapter(categoryAdapter);

                        mSwipeRefreshHome.setRefreshing(false);
                    }
                }));
    }

    private void displayImageBanner(List<Banner> mListBanner) {


        SliderBannerAdapter mBannerAdapter = new SliderBannerAdapter(getActivity(), mListBanner);
        mSlidingBanner.setSliderAdapter(mBannerAdapter);
        mDialog.dismiss();
//        Map<String, String> mMapBanner = new HashMap<>();
//        for (Banner banner : mListBanner)
//            mMapBanner.put(banner.getName(), banner.getLink());
//
//        for (String name : mMapBanner.keySet()){
//
//        }
    }

    private void getToppingList() {

        mDisposable.add(mServices.getDrinks(Common.TOPPING_MENU_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Drink>>() {
                    @Override
                    public void accept(List<Drink> mListDrink) throws Exception {

                        Common.mListTopping = mListDrink;
                    }
                }));

    }

    private void checkSessionLogin() {

        mSwipeRefreshHome.setRefreshing(true);

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null){
            mServices.checkUserExists(user.getPhoneNumber())
                    .enqueue(new Callback<CheckUserResponse>() {
                        @Override
                        public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                            CheckUserResponse checkUserResponse = response.body();
                            if (checkUserResponse.isExists()){
                                // Request Information of current user
                                mServices.getUserInformation(user.getPhoneNumber())
                                        .enqueue(new Callback<User>() {
                                            @Override
                                            public void onResponse(Call<User> call, Response<User> response) {
                                                Common.currentUser = response.body();
                                                mSwipeRefreshHome.setRefreshing(false);
                                            }

                                            @Override
                                            public void onFailure(Call<User> call, Throwable t) {
                                                mSwipeRefreshHome.setRefreshing(false);
                                                Log.e("LOGIN_ERROR", t.getMessage());
                                            }
                                        });
                            }else{
                                startActivity(new Intent(getActivity(), MainActivity.class));
                                getActivity().finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<CheckUserResponse> call, Throwable t) {
                            mSwipeRefreshHome.setRefreshing(false);
                            Log.e("CHECK_USER_ERROR", t.getMessage());
                        }
                    });
        }else{
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();
            Paper.init(getActivity());
            Paper.book().delete(Common.KEY_LOGGED);
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void init() {

        mServices = Common.getAPI();
        mDisposable = new CompositeDisposable();

        mDialog = new SpotsDialog.Builder()
                .setContext(getActivity())
                .setCancelable(false)
                .build();

        mRecyclerCategory.setHasFixedSize(true);
        mRecyclerCategory.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayout.HORIZONTAL));

        mSlidingBanner.startAutoCycle();
        mSlidingBanner.setIndicatorAnimation(IndicatorAnimations.WORM);
        mSlidingBanner.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
    }

    @Override
    public void onDestroy() {
//        mDisposable.dispose();
        super.onDestroy();
    }
}
