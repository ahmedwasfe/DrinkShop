package com.ahmet.drinkshop.SubActivity;

import androidx.fragment.app.FragmentActivity;

import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import com.ahmet.drinkshop.Common.Common;
import com.ahmet.drinkshop.Model.Store;
import com.ahmet.drinkshop.R;
import com.ahmet.drinkshop.Retrofit.IDrinkShopAPI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class StoresMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private FusedLocationProviderClient mFusedClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private CompositeDisposable mDisposable;
    private IDrinkShopAPI mServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mServices = Common.getAPI();
        mDisposable = new CompositeDisposable();

        buildLocationRequest();
        buildLocationCallBack();
        mFusedClient = LocationServices.getFusedLocationProviderClient(this);

        // Start update location
        mFusedClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private void buildLocationCallBack() {

        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                // Add a marker in Current User and move the camera
                LatLng currentUserLocation = new LatLng(-locationResult.getLastLocation().getLatitude(),
                        locationResult.getLastLocation().getLongitude());
                mMap.addMarker(new MarkerOptions().position(currentUserLocation).title(Common.currentUser.getName()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentUserLocation, 17.0f));

                getStoresLocation(locationResult.getLastLocation());
            }
        };
    }

    private void getStoresLocation(Location lastLocation) {

        mDisposable.add(mServices.getStoresLocations(String.valueOf(lastLocation.getLatitude()),
                String.valueOf(lastLocation.getLongitude()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Store>>() {
                    @Override
                    public void accept(List<Store> mListStores) throws Exception {
                        for (Store store : mListStores){
                            LatLng storeLocation = new LatLng(store.getLatitude(), store.getLongitude());
                            mMap.addMarker(new MarkerOptions()
                                            .position(storeLocation)
                                            .title(store.getName())
                                            .snippet(new StringBuilder("Distance: ")
                                                    .append(store.getDistance_in_km())
                                                    .append(" km").toString())
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_store)));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("STORES_LOCATION_ERROR", throwable.getMessage().toString());
                    }
                }));
    }

    private void buildLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setSmallestDisplacement(10f);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mFusedClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    @Override
    protected void onPause() {
        mFusedClient.removeLocationUpdates(mLocationCallback);
        super.onPause();
    }

    @Override
    protected void onStop() {
        mFusedClient.removeLocationUpdates(mLocationCallback);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mDisposable.dispose();
        super.onDestroy();
    }
}
