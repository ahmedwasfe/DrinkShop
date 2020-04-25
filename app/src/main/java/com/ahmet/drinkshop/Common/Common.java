package com.ahmet.drinkshop.Common;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.ahmet.drinkshop.HomeActivity;
import com.ahmet.drinkshop.MainActivity;
import com.ahmet.drinkshop.Model.Orders;
import com.ahmet.drinkshop.R;
import com.ahmet.drinkshop.Retrofit.PaymentClient;
import com.ahmet.drinkshop.RoomDatabase.DataSource.CartRepository;
import com.ahmet.drinkshop.RoomDatabase.DataSource.FavoriteRepository;
import com.ahmet.drinkshop.RoomDatabase.LocalDatabase.RoomDatabase;
import com.ahmet.drinkshop.Model.Category;
import com.ahmet.drinkshop.Model.Drink;
import com.ahmet.drinkshop.Model.User;
import com.ahmet.drinkshop.Retrofit.IDrinkShopAPI;
import com.ahmet.drinkshop.Retrofit.RetrofitClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Common {

    public static final String KEY_LOGGED = "LOGGED";
    public static String IS_LOGIN = "IsLogin";

    public static final int STOARGE_REQUEST_PERMISSION_CODE = 1880;
    public static final int PHONE_NUMBER_REQUEST_CODE = 1881;
    public static final int STOARGE_REQUEST_GALLARY_CODE = 1882;
    public static final int PAYMENT_REQUEST_CODE = 1883;
    private static final int NOTIFICATION_ID = 8;

    // In Emulator localhost = 10.0.2.2
   // public static String BASE_URL = "http://10.0.2.2/drinkshop/";
    public static String BASE_URL = "https://ahmed-abu-mandil.000webhostapp.com/drinkshop/";
    public static String API_TOKEN_URL = "http://10.0.2.2/drinkshop/braintree/main.php";
  //  public static String BASE_URL = "https://ahmed-abu-mandil.000webhostapp.com/drinkshop/";

    public static final String TOPPING_MENU_ID = "7";
    public static double mToppingPrice = 0.0;

    public static User currentUser;
    public static Category currentCategory;
    public static Orders currentOrder;

    public static List<Drink> mListTopping = new ArrayList();
    public static List<String> mListToppingAdded = new ArrayList<>();

    // Hold filed
    // -1 : no choose (error) 0 : M, 1 : L
    public static int mSizeOfCup = -1;
    // -1 : no choose (error)
    public static int mSugar = -1;
    public static int mIce = -1;


    // Cart Database
    public static RoomDatabase mRoomDatabase;
    public static CartRepository mCartRepository;
    public static FavoriteRepository mFavoriteRepository;

    public static IDrinkShopAPI getAPI(){
        return RetrofitClient.getClient(BASE_URL).create(IDrinkShopAPI.class);
    }

    public static IDrinkShopAPI getPaymentAPI(){
        return PaymentClient.getPayment(BASE_URL).create(IDrinkShopAPI.class);
    }

    public static void setFragment(Fragment fragment, int id, FragmentManager fragmentManager){
        fragmentManager.beginTransaction()
                .replace(id, fragment)
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .commit();
    }

    public static String convertCodeToStatus(int orderStatus) {

        switch (orderStatus){
            case 0:
                return "Placed";
            case 1:
                return "Processing";
            case 2:
                return "Shipping";
            case 3:
                return "Shipped";
            case -1:
                return "Canceled";
            default:
                return "Order";
        }
    }

    public static void updateToken(String token) {

        IDrinkShopAPI mService = getAPI();
        mService.updateToken(Common.currentUser.getPhone(), token,"0")
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d("USER_TOKEN", response.body());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("TOKEN_ERROR", t.getMessage());
                    }
                });
    }

    public static void showNotification(Context mContext, String title, String message, Intent intent){

        String NOTIFICATION_CHANNEL = "Drink_Shop_Channel";
        String name = "drink shop";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL, name, importance);
            channel.setDescription("Drink Shop app");
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[] {0, 1000, 500, 1000});


            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notifcationBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher));

        Notification notification = notifcationBuilder.build();
        manager.notify(NOTIFICATION_ID, notification);

    }
}
