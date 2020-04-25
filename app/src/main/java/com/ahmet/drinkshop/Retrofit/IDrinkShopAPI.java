package com.ahmet.drinkshop.Retrofit;

import com.ahmet.drinkshop.Model.Banner;
import com.ahmet.drinkshop.Model.Category;
import com.ahmet.drinkshop.Model.CheckUserResponse;
import com.ahmet.drinkshop.Model.Drink;
import com.ahmet.drinkshop.Model.Orders;
import com.ahmet.drinkshop.Model.Store;
import com.ahmet.drinkshop.Model.User;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface IDrinkShopAPI {

    // Check if user exists or not
    @FormUrlEncoded
    @POST("checkuser.php")
    Call<CheckUserResponse> checkUserExists(@Field("phone") String phone);

    // Create new Account to user
    @FormUrlEncoded
    @POST("register.php")
    Call<User> registerNewUser(@Field("phone") String phone,
                               @Field("name") String name,
                               @Field("birthdate") String birthdate,
                               @Field("address") String address);

    // get Information for current user
    @FormUrlEncoded
    @POST("getuser.php")
    Call<User> getUserInformation(@Field("phone") String phone);

    // load all data for Banner
    @GET("getbanner.php")
    Observable<List<Banner>> getBanners();

    //load all data for category
    @GET("getmenu.php")
    Observable<List<Category>> getMenus();

    // get Drink base category Id
    @FormUrlEncoded
    @POST("getdrink.php")
    Observable<List<Drink>> getDrinks(@Field("categoryId") String categoryId);

    // Upload file to server
    @Multipart
    @POST("uploadUserImg.php")
    Call<String> uploadFile(@Part MultipartBody.Part phone, @Part MultipartBody.Part file);

    // Searching for drink
    @GET("searchdrinks.php")
    Observable<List<Drink>> searchingForDrink();

    // Add new Order
    @FormUrlEncoded
    @POST("addNewOrder.php")
    Call<String> addNewOrder(@Field("price") float price,
                             @Field("comment") String comment,
                             @Field("address") String address,
                             @Field("orderDetails") String orderDetails,
                             @Field("phone") String phone);

    // Payment Mothod
    @FormUrlEncoded
    @POST("braintree/checkout.php")
    Call<String> payment(@Field("nonce") String nonce,
                             @Field("amount") String amount);

    // Get order base phone and status
    @FormUrlEncoded
    @POST("getOrdersByPhoneAndStatus.php")
    Observable<List<Orders>> getOrderByStatus(@Field("phone") String userPhone,
                                              @Field("status") String status);

    // Update Token
    @FormUrlEncoded
    @POST("updateToken.php")
    Call<String> updateToken(@Field("user") String userPhone,
                             @Field("token") String token,
                             @Field("isServerToken") String isServerToken);

    // Cancel Order
    @FormUrlEncoded
    @POST("cancelOrder.php")
    Call<String> cancelOrder(@Field("orderId") String orderId,
                             @Field("userPhone") String userPhone);

    // get Stores locations
    @FormUrlEncoded
    @POST("getStores.php")
    Observable<List<Store>> getStoresLocations(@Field("latitude") String latitude,
                                               @Field("Longitude") String Longitude);


}
