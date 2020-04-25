package com.ahmet.drinkshop.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;


import com.ahmet.drinkshop.Adapter.CartAdapter;
import com.ahmet.drinkshop.Common.Common;
import com.ahmet.drinkshop.Common.RecyclerSwipeHelper;
import com.ahmet.drinkshop.Interface.RecyclerSwipeItemHelperListener;
import com.ahmet.drinkshop.MainActivity;
import com.ahmet.drinkshop.R;
import com.ahmet.drinkshop.Retrofit.IDrinkShopAPI;
import com.ahmet.drinkshop.RoomDatabase.Model.Cart;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;
import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment implements RecyclerSwipeItemHelperListener {

    @BindView(R.id.recycler_cart)
    RecyclerView mRecyclerCart;
    @BindView(R.id.constraint_cart)
    ConstraintLayout mConstraintCart;
    @BindView(R.id.btn_place_order)
    Button mBtnPlaceOrder;

    private CartAdapter mCartAdapter;
    private List<Cart> mListLocalCart;

    private CompositeDisposable mDisposable;
    private IDrinkShopAPI mService, mServicePayment;

    private android.app.AlertDialog mDialog;

    private String token, amount, orderAddress, orderComment;
    HashMap<String, String> mMapParams;

    @OnClick(R.id.btn_place_order)
    void palceOrder(){
        placeOrders();
    }

    private static CartFragment instance;
    public static CartFragment getInstance(){
        return instance == null ? new CartFragment() : instance;
    }

    private void placeOrders() {

        if (Common.currentUser != null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Submit Order");
            View layoutView = LayoutInflater.from(getContext())
                    .inflate(R.layout.layout_submit_orders, null, false);
            EditText mInputComment = layoutView.findViewById(R.id.input_comment);
            EditText mInputOtherAddress = layoutView.findViewById(R.id.input_other_address);
            RadioButton mRadioCurrentAddress = layoutView.findViewById(R.id.radio_current_address);
            RadioButton mRadioOtherAddress = layoutView.findViewById(R.id.radio_other_address);

            mRadioCurrentAddress.setChecked(true);

            mRadioCurrentAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        mInputOtherAddress.setEnabled(false);
                }
            });
            mRadioOtherAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        mInputOtherAddress.setEnabled(true);
                }
            });
            builder.setView(layoutView);
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    orderComment = mInputComment.getText().toString();

                    if (mRadioCurrentAddress.isChecked())
                        orderAddress = Common.currentUser.getAddress();
                    else if (mRadioOtherAddress.isChecked())
                        orderAddress = mInputOtherAddress.getText().toString();
                    else
                        orderAddress = "";

                    if (!TextUtils.isEmpty(orderAddress)) {
                        List<Cart> mListCart = Common.mCartRepository.getAllCartItems();
                        submitOrders(mListCart,
                                Common.mCartRepository.sumPrice(),
                                orderComment, orderAddress);
                    }else
                        Toast.makeText(getActivity(), "Please enter order address", Toast.LENGTH_SHORT).show();

                    // Payment
                    DropInRequest dropInRequest = new DropInRequest()
                            .clientToken(token);
                    startActivityForResult(dropInRequest.getIntent(getActivity()),
                            Common.PAYMENT_REQUEST_CODE);

                }
            });

            builder.create();
            builder.show();
        }else{
            // Go to Login
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setTitle("NOT LOGIN ?")
                    .setMessage("Please login account to submit order")
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();;
                        }
                    }).setPositiveButton("LOGIN", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startActivity(new Intent(getActivity(), MainActivity.class));
                            getActivity().finish();
                        }
                    });
            builder.show();
        }
    }

    private void submitOrders(List<Cart> mListCart, float sumPrice, String comment, String orderAddress) {

        if (mListCart.size() > 0) {

            String orderDetails = new Gson().toJson(mListCart);
            mService.addNewOrder(sumPrice, comment, orderAddress, orderDetails, Common.currentUser.getPhone())
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Toast.makeText(getActivity(), "Order submit", Toast.LENGTH_SHORT).show();
                            // Clrar cart
                            Common.mCartRepository.clearCart();
                            mCartAdapter.notifyDataSetChanged();
                            Common.setFragment(HomeFragment.getInstance(), R.id.frame_layout_home,
                                    getActivity().getSupportFragmentManager());
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e("ORDER_SUBMIT_ERROR", t.getMessage());
                        }
                    });
        }
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View layoutView = inflater.inflate(R.layout.fragment_cart, container, false);

        ButterKnife.bind(this, layoutView);

        init();

        getAllItemFromCart();

       // loadToken();

        return layoutView;
    }

    private void loadToken() {

        mDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Common.API_TOKEN_URL, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                mDialog.dismiss();
                mBtnPlaceOrder.setEnabled(false);
                Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                mDialog.dismiss();

                token = responseString;
                Log.d("PUBLIC_TOKEN", token);
                Log.d("PRIVATE_TOKEN", responseString);
                mBtnPlaceOrder.setEnabled(true);

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllItemFromCart();
    }

    private void getAllItemFromCart() {

        List<Cart> mListCart = Common.mCartRepository.getAllCartItems();
        mListLocalCart = mListCart;
        mCartAdapter = new CartAdapter(getActivity(), mListCart);
        mRecyclerCart.setAdapter(mCartAdapter);
    }

    private void init() {

        mDisposable = new CompositeDisposable();
        mService = Common.getAPI();
        mServicePayment = Common.getPaymentAPI();

        mRecyclerCart.setHasFixedSize(true);
        mRecyclerCart.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayout.VERTICAL));

        ItemTouchHelper.SimpleCallback simpleCallback = new
                RecyclerSwipeHelper(0,ItemTouchHelper.START, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(mRecyclerCart);

        mDialog = new SpotsDialog.Builder()
                .setCancelable(true)
                .setContext(getActivity())
                .build();
    }

    @Override
    public void onSwipe(RecyclerView.ViewHolder holder, int direction, int position) {

        if (holder instanceof CartAdapter.CartHolder){
            String name = mListLocalCart.get(holder.getAdapterPosition()).getName();
            Cart deleteItemCart = mListLocalCart.get(holder.getAdapterPosition());
            int index = holder.getAdapterPosition();

            // Delete item from adapter
            mCartAdapter.removeItem(index);
            // Delete item from database
            Common.mCartRepository.deleteItemById(deleteItemCart);

            Snackbar snackbar = Snackbar.make(mConstraintCart,
                    new StringBuilder(name).append("removed from cart").toString(),
                    Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCartAdapter.restoreItem(deleteItemCart, index);
                    Common.mCartRepository.addItemToCart(deleteItemCart);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PAYMENT_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                DropInResult dropInResult = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce paymentNonce = dropInResult.getPaymentMethodNonce();
                String nonce = paymentNonce.getNonce();
                if (Common.mCartRepository.sumPrice() > 0) {
                    amount = String.valueOf(Common.mCartRepository.sumPrice());
                    mMapParams = new HashMap<>();

                    mMapParams.put("amount", amount);
                    mMapParams.put("nonce", nonce);

                    sendPayment();
                } else {
                    Toast.makeText(getActivity(), "Payment amount is 0", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == getActivity().RESULT_CANCELED)
                Toast.makeText(getActivity(), "Payment camcelled", Toast.LENGTH_SHORT).show();
            else {
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.e("PAYMENT_ERROR", error.getMessage());
            }
        }
    }

    private void sendPayment() {
        mServicePayment.payment(mMapParams.get("nonce"), mMapParams.get("amount"))
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.body().toString().contains("Successful")) {
                            Toast.makeText(getActivity(), "Transaction successful", Toast.LENGTH_SHORT).show();
                            // Submit order
                            if (!TextUtils.isEmpty(orderAddress)) {
                                List<Cart> mListCart = Common.mCartRepository.getAllCartItems();
                                submitOrders(mListCart,
                                        Common.mCartRepository.sumPrice(),
                                        orderComment, orderAddress);
                            }else
                                Toast.makeText(getActivity(), "Please enter order address", Toast.LENGTH_SHORT).show();
                        }else
                            Toast.makeText(getActivity(), "Transaction failed", Toast.LENGTH_SHORT).show();

                        Log.d("PAYMENT_INFO", response.body());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("PAYMENT_INFO_ERROR", t.getMessage());
                    }
                });
    }
}


//$gateway = new Braintree\Gateway([
//        'environment' => 'sandbox',
//        'merchantId' => 'vpt2j8sh63fhbn85',
//        'publicKey' => '4bsq895bqx99hf88',
//        'privateKey' => '6e7caa69f40868b4e1e39a450f0bd0b0'
//        ]);