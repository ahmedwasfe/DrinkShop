package com.ahmet.drinkshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ahmet.drinkshop.Common.Common;
import com.ahmet.drinkshop.Model.CheckUserResponse;
import com.ahmet.drinkshop.Model.User;
import com.ahmet.drinkshop.Retrofit.IDrinkShopAPI;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.root_layout)
    ConstraintLayout mConstraintRoot;

    private List<AuthUI.IdpConfig> mListProviderPhone;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @OnClick(R.id.btn_continue)
    void mContinue() {
        // startLoginPage(LoginType.PHONE);

        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(mListProviderPhone)
                .build(), Common.PHONE_NUMBER_REQUEST_CODE);
    }

    private IDrinkShopAPI mSerivces;

    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        checkPermission();

        init();

        mListProviderPhone = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null)
                checkUser(user);
        };


    }


    private void checkUser(FirebaseUser user) {

        mSerivces.checkUserExists(user.getPhoneNumber())
                .enqueue(new Callback<CheckUserResponse>() {
                    @Override
                    public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {

                        CheckUserResponse userResponse = response.body();

                        if (userResponse.isExists()) {

                            // Fetch information
                            mSerivces.getUserInformation(user.getPhoneNumber())
                                    .enqueue(new Callback<User>() {
                                        @Override
                                        public void onResponse(Call<User> call, Response<User> response) {
                                            // is User already exists , just start new Activity
                                            mDialog.dismiss();

                                            Common.currentUser = response.body();
                                            // Update token
                                            getToken();
                                           // Log.d("TOKEN", token);
                                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                            intent.putExtra(Common.IS_LOGIN, true);
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onFailure(Call<User> call, Throwable t) {
                                            Log.d("USER_PHONE", t.getMessage());
                                            Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });


                        } else {
                            // else need Register
                            // Toast.makeText(MainActivity.this, "asdsafsdf", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                            showRegisterDialog(user.getPhoneNumber());
                        }
                    }

                    @Override
                    public void onFailure(Call<CheckUserResponse> call, Throwable t) {
//                        Log.d("ERROR_URL", t.getMessage());
                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void init() {

        mSerivces = Common.getAPI();

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Please Wait...")
                .setCancelable(false)
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Common.PHONE_NUMBER_REQUEST_CODE && resultCode == RESULT_OK) {

            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
            IdpResponse idpResponse = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK){

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.i("CURRENT_USER", user.getPhoneNumber());

            }else {

                Toast.makeText(this, "failed_to_sign_in(", Toast.LENGTH_SHORT).show();
                Log.e("FAILED_TO_SIGN_IN", idpResponse.getError().getMessage());

            }

        } else {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }

    }


    private void showRegisterDialog(final String phone) {

        BottomSheetDialog sheetDialog = new BottomSheetDialog(this);


        View layoutView = getLayoutInflater()
                .inflate(R.layout.layout_new_account, null);

        TextInputEditText mInputName = layoutView.findViewById(R.id.input_name);
        TextInputEditText mInputAddress = layoutView.findViewById(R.id.input_address);
        TextInputEditText mInputBithrthdate = layoutView.findViewById(R.id.input_birthdate);
        Button mCreateNewAccount = layoutView.findViewById(R.id.btn_new_account);

        // mInputBithrthdate.addTextChangedListener(new PatternedTextWatcher("####-##-##"));
        // Event
        mCreateNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialog.show();


                String name = mInputName.getText().toString();
                String address = mInputAddress.getText().toString();
                String birthdate = mInputBithrthdate.getText().toString();


                if (TextUtils.isEmpty(name)) {
                    mInputName.setError("Please enter your name");
                    return;
                }

                if (TextUtils.isEmpty(address)) {
                    mInputAddress.setError("Please enter your address");
                    return;
                }

                if (TextUtils.isEmpty(birthdate)) {
                    mInputBithrthdate.setError("Please enter your Birthdate");
                    return;
                }


                mSerivces.registerNewUser(phone, name, birthdate, address)
                        .enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {

                                mDialog.dismiss();
                                User user = response.body();
                                if (TextUtils.isEmpty(user.getError_msg())) {
                                    Snackbar.make(mConstraintRoot, "User Register Successful", Snackbar.LENGTH_SHORT).show();
                                    Common.currentUser = response.body();
                                    // start new Activity
                                    // Update token
                                    getToken();
                                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                    intent.putExtra(Common.IS_LOGIN, true);
                                    startActivity(intent);
                                    finish();

                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                Log.d("ERROR_URL", t.getMessage());
                                mDialog.dismiss();
                            }
                        });
                // mDialog.show();
            }
        });

        // mDialog.show();

        sheetDialog.setContentView(layoutView);
        sheetDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        if (mAuthStateListener != null)
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        super.onStop();
    }

    private void printKeyHash() {

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES
            );
            for (Signature signature : packageInfo.signatures) {
                MessageDigest digest = MessageDigest.getInstance("SHA");
                digest.update(signature.toByteArray());
                Log.d("KEYHASH", Base64.encodeToString(digest.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    private void checkPermission(){

        Dexter.withActivity(this)
                .withPermissions(new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION})
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
//            ActivityCompat.requestPermissions(this, new String[]{
//                    Manifest.permission.READ_EXTERNAL_STORAGE
//            },Common.STOARGE_REQUEST_PERMISSION_CODE);
    }

   private void getToken(){

        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        Common.updateToken(task.getResult().getToken());
                        Log.d("TOKEN", task.getResult().getToken());
                    }
                });
   }
}

