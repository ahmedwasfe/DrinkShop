package com.ahmet.drinkshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmet.drinkshop.Fragments.CartFragment;
import com.ahmet.drinkshop.Fragments.HomeFragment;
import com.ahmet.drinkshop.Fragments.OrdersFragment;
import com.ahmet.drinkshop.Model.CheckUserResponse;
import com.ahmet.drinkshop.Model.User;
import com.ahmet.drinkshop.RoomDatabase.DataSource.CartRepository;
import com.ahmet.drinkshop.RoomDatabase.DataSource.FavoriteRepository;
import com.ahmet.drinkshop.RoomDatabase.LocalDatabase.CartDataSource;
import com.ahmet.drinkshop.RoomDatabase.LocalDatabase.FavoriteDataSource;
import com.ahmet.drinkshop.RoomDatabase.LocalDatabase.RoomDatabase;
import com.ahmet.drinkshop.Common.Common;
import com.ahmet.drinkshop.Common.ProcessRequestBody;
import com.ahmet.drinkshop.Interface.UploadCallBack;
import com.ahmet.drinkshop.Model.Drink;
import com.ahmet.drinkshop.Retrofit.IDrinkShopAPI;
import com.ahmet.drinkshop.Fragments.FavoriteFragment;
import com.ahmet.drinkshop.SubActivity.SearchActivity;
import com.ahmet.drinkshop.SubActivity.StoresMapsActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.nex3z.notificationbadge.NotificationBadge;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements UploadCallBack {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;

    TextView mTxtUserName;
    TextView mTxtUserPhone;


    private NotificationBadge badgeCartCount;
    private ImageView mImgCart;

    private CircleImageView mImgUser;
    private ProgressBar mProgressBar;

    private ActionBarDrawerToggle mDrawerToggle;

    private IDrinkShopAPI mServices;
    // RXJava
    private CompositeDisposable mDisposable;

    private AlertDialog mDialog;

    private Uri mFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);


        if (getIntent() != null) {

            boolean isLogin = getIntent().getBooleanExtra(Common.IS_LOGIN, false);
            if (isLogin) {

                FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                Paper.init(HomeActivity.this);
                Paper.book().write(Common.KEY_LOGGED, user.getPhoneNumber());

            }
        }

        Common.setFragment(HomeFragment.getInstance(), R.id.frame_layout_home,
                getSupportFragmentManager());

        initNavigationView();


        // Save newest Topping list
        getToppingList();

        // init Cart Databse
        initCartDatabase();

        // If user already logged , Just login again (Session still live)
        checkSessionLogin();

    }

    private void checkSessionLogin() {

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            mServices.checkUserExists(user.getPhoneNumber())
                    .enqueue(new Callback<CheckUserResponse>() {
                        @Override
                        public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                            CheckUserResponse checkUserResponse = response.body();
                            if (checkUserResponse.isExists()) {
                                // Request Information of current user
                                mServices.getUserInformation(user.getPhoneNumber())
                                        .enqueue(new Callback<User>() {
                                            @Override
                                            public void onResponse(Call<User> call, Response<User> response) {
                                                Common.currentUser = response.body();
                                                // If we not login, of course Common.currentUser is null
                                                if (Common.currentUser != null) {

                                                    mTxtUserName.setText(Common.currentUser.getName());
                                                    mTxtUserPhone.setText(Common.currentUser.getPhone());
                                                    if (!TextUtils.isEmpty(Common.currentUser.getUserImg())) {
                                                        Picasso.get()
                                                                .load(new StringBuilder(Common.BASE_URL)
                                                                        .append("images/")
                                                                        .append(Common.currentUser.getUserImg()).toString())
                                                                .into(mImgUser);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<User> call, Throwable t) {
                                                Log.e("LOGIN_ERROR", t.getMessage());
                                            }
                                        });
                            } else {
                                startActivity(new Intent(HomeActivity.this, MainActivity.class));
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<CheckUserResponse> call, Throwable t) {
                            Log.e("CHECK_USER_ERROR", t.getMessage());
                        }
                    });
        }else{
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();
            Paper.init(this);
            Paper.book().delete(Common.KEY_LOGGED);
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    private void initCartDatabase() {

            Common.mRoomDatabase = RoomDatabase.getInstance(this);
            Common.mCartRepository = CartRepository.getInstance(CartDataSource.getInstance(Common.mRoomDatabase.cartDAO()));
            Common.mFavoriteRepository = FavoriteRepository.getInstance(FavoriteDataSource.getInstance(Common.mRoomDatabase.favoriteDAO()));

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

    @Override
    protected void onDestroy() {
        mDisposable.dispose();
        super.onDestroy();
    }

    private void initNavigationView() {

        mServices = Common.getAPI();
        mDisposable = new CompositeDisposable();

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.nav_home) {
                    Common.setFragment(HomeFragment.getInstance(), R.id.frame_layout_home,
                            getSupportFragmentManager());
                    mDrawerLayout.closeDrawers();
                } else if (menuItem.getItemId() == R.id.nav_cart) {
                    Common.setFragment(CartFragment.getInstance(), R.id.frame_layout_home,
                            getSupportFragmentManager());
                    mDrawerLayout.closeDrawers();
                } else if (menuItem.getItemId() == R.id.nav_favorite) {
                    if (Common.currentUser != null) {
                        Common.setFragment(FavoriteFragment.getInstance(), R.id.frame_layout_home,
                                getSupportFragmentManager());
                        mDrawerLayout.closeDrawers();
                    }else
                        Toast.makeText(HomeActivity.this, "Please login to use this feature", Toast.LENGTH_SHORT).show();
                } else if (menuItem.getItemId() == R.id.nav_order) {
                    if (Common.currentUser != null) {
                        Common.setFragment(OrdersFragment.getInstance(), R.id.frame_layout_home,
                                getSupportFragmentManager());
                        mDrawerLayout.closeDrawers();
                    }else
                        Toast.makeText(HomeActivity.this, "Please login to use this feature", Toast.LENGTH_SHORT).show();
                } else if (menuItem.getItemId() == R.id.nav_logout)
                    signOut();
                else if (menuItem.getItemId() == R.id.nav_stores)
                    startActivity(new Intent(HomeActivity.this, StoresMapsActivity.class));

                return true;
            }
        });


        View headerView = mNavigationView.getHeaderView(0);
        mTxtUserName = headerView.findViewById(R.id.txt_user_name);
        mTxtUserPhone = headerView.findViewById(R.id.txt_user_phone);
        mImgUser = headerView.findViewById(R.id.img_user);
        mProgressBar = headerView.findViewById(R.id.progress_upload_image);

        mImgUser.setOnClickListener(v -> {
            if (Common.currentUser != null)
                chooseImage();
        });


    }

    private void signOut() {

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Sign Out");
        builder.setMessage("Do you want to sign out ?");
        builder.setNegativeButton("sign out", (dialog, which) -> {

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();
            Paper.init(this);
            Paper.book().delete(Common.KEY_LOGGED);
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
        builder.setPositiveButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create();
        builder.show();
    }

    private void chooseImage() {
        startActivityForResult(Intent.createChooser(FileUtils.createGetContentIntent(), "Choose Image"),
                Common.STOARGE_REQUEST_GALLARY_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_action_bar, menu);
        MenuItem menuItem = menu.findItem(R.id.action_cart);
        badgeCartCount = menuItem.getActionView().findViewById(R.id.badge_cart_count);
        mImgCart = menuItem.getActionView().findViewById(R.id.img_cart_count);

        mImgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.setFragment(CartFragment.getInstance(), R.id.frame_layout_home,
                        getSupportFragmentManager());
                mDrawerLayout.closeDrawers();

            }
        });
        updateCartCount();
        return super.onCreateOptionsMenu(menu);
    }

    private void updateCartCount() {

        if (badgeCartCount == null) return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Common.mCartRepository.getCountCartItems() == 0)
                    // badgeCartCount.setVisibility(View.GONE);
                    badgeCartCount.setText("0");
                else {
                    badgeCartCount.setVisibility(View.VISIBLE);
                    badgeCartCount.setText(String.valueOf(Common.mCartRepository.getCountCartItems()));
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        if (mDrawerToggle.onOptionsItemSelected(item))
            return true;

        if (item.getItemId() == R.id.action_cart)
            return true;
        else if (item.getItemId() == R.id.action_search) {
            startActivity(new Intent(this, SearchActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartCount();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Common.STOARGE_REQUEST_GALLARY_CODE) {
                if (data != null) {
                    mFileUri = data.getData();
                    if (mFileUri != null && !mFileUri.getPath().isEmpty()) {
                        mImgUser.setImageURI(mFileUri);
                        uploadFileToServer();
                    } else
                        Toast.makeText(this, "Can not upload file to server", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void uploadFileToServer() {

        mProgressBar.setVisibility(View.VISIBLE);

        if (mFileUri != null) {
            File file = FileUtils.getFile(this, mFileUri);
            String fileName = new StringBuilder(Common.currentUser.getPhone())
                    .append(FileUtils.getExtension(file.toString()))
                    .toString();

            ProcessRequestBody requestFile = new ProcessRequestBody(file, this);
            MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", fileName, requestFile);
            MultipartBody.Part userPhone = MultipartBody.Part.createFormData("phone", Common.currentUser.getPhone());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    mServices.uploadFile(userPhone, body)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    Toast.makeText(HomeActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                                    mProgressBar.setVisibility(View.GONE);
                                    Log.d("BODY", response.body());
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    mProgressBar.setVisibility(View.GONE);
                                    Log.e("UPLOAD", t.getMessage());
                                }
                            });
                }
            }).start();
        }
    }

    @Override
    public void progressUpdate(int pertantage) {

    }
}
