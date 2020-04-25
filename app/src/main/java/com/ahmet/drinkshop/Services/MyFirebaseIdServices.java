package com.ahmet.drinkshop.Services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.ahmet.drinkshop.Common.Common;
import com.ahmet.drinkshop.Retrofit.IDrinkShopAPI;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseIdServices extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        if (Common.currentUser != null)
            Common.updateToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage != null)
            Common.showNotification(this,"","",null);
    }
}
