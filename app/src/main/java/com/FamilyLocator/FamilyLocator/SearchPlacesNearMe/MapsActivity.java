package com.FamilyLocator.FamilyLocator.SearchPlacesNearMe;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;

import com.FamilyLocator.FamilyLocator.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    CheckBox mCheckBox;
    LinearLayout swap;
    TextView ahospital,abank,aschool,acafe,aatm,apolice,arestaurant,astore;
    private GoogleMap mMap;
    View mMapView;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentLocationmMarker;
    public static final int REQUEST_LOCATION_CODE = 99;
    int PROXIMITY_RADIUS = 10000;
    double latitude,longitude;
    private static final int START_LEVEL = 1;
    private int mLevel;
    private Button mNextLevelButton;
    private InterstitialAd mInterstitialAd;
    private TextView mLevelTextView;

    @Override
    public void onBackPressed() {
        showInterstitial();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
//Load an ad into the AdMob banner view.............................................................

//         Create the InterstitialAd and set the adUnitId (defined in values/strings.xml).

        mInterstitialAd = newInterstitialAd();
        loadInterstitial();
//..................................................................................................

//         Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED)
        {
            if(client == null)
            {
                bulidGoogleApiClient();
            }
            mMap.setMyLocationEnabled(true);

        }
        mCheckBox =findViewById(R.id.checkbox);
        ahospital=findViewById(R.id.thospital);
        abank=findViewById(R.id.tbank);
        aschool=findViewById(R.id.tschool);
        acafe=findViewById(R.id.tcafe);
        aatm=findViewById(R.id.tatm);
        apolice=findViewById(R.id.tpolice);
        arestaurant=findViewById(R.id.trestaurant);
        astore=findViewById(R.id.tstore);
//        ahospital.setVisibility(View.GONE);
//        abank.setVisibility(View.GONE);
//        aschool.setVisibility(View.GONE);
//        acafe.setVisibility(View.GONE);
//        aatm.setVisibility(View.GONE);
//        apolice.setVisibility(View.GONE);
//        arestaurant.setVisibility(View.GONE);
//        astore.setVisibility(View.GONE);

        swap=findViewById(R.id.swap);
        swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (findViewById(R.id.lay1).getVisibility()==View.VISIBLE){
                    findViewById(R.id.lay1).setVisibility(View.GONE);
                //    StartSmartAnimation.startAnimation( findViewById(R.id.lay2) , AnimationType.SlideInUp , 800 , 00 , true );
                    findViewById(R.id.lay2).setVisibility(View.VISIBLE);
                }else {
                    findViewById(R.id.lay2).setVisibility(View.GONE);
                    findViewById(R.id.lay1).setVisibility(View.VISIBLE);
                }
            }
        });





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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bulidGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }


    protected synchronized void bulidGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();

    }

//    @Override
//    public void onBackPressed() {
//        startActivity(new Intent(getApplicationContext(),MainActivity.class));
//        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
//
//    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastlocation = location;
        if(currentLocationmMarker != null)
        {
            currentLocationmMarker.remove();

        }
        Log.d("lat = ",""+latitude);
        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());
//        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,12));
        if(client != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        }

    }

    public void onClick(View v)
    {
        Object dataTransfer[] = new Object[2];
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();

        switch(v.getId())
        {
            case R.id.hopistal:
                mMap.clear();
                String hospital = "hospital";
                String url = getUrl(latitude, longitude, hospital);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Showing Nearby Hospitals", Toast.LENGTH_SHORT).show();
//                ahospital.setVisibility(View.VISIBLE);
//                abank.setVisibility(View.GONE);
//                aschool.setVisibility(View.GONE);
//                acafe.setVisibility(View.GONE);
//                aatm.setVisibility(View.GONE);
//                apolice.setVisibility(View.GONE);
//                arestaurant.setVisibility(View.GONE);
//                astore.setVisibility(View.GONE);
                break;


            case R.id.school:
                mMap.clear();
                String school = "school";
                url = getUrl(latitude, longitude, school);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Showing Nearby Schools", Toast.LENGTH_SHORT).show();
//                ahospital.setVisibility(View.GONE);
//                abank.setVisibility(View.GONE);
//                aschool.setVisibility(View.VISIBLE);
//                acafe.setVisibility(View.GONE);
//                aatm.setVisibility(View.GONE);
//                apolice.setVisibility(View.GONE);
//                arestaurant.setVisibility(View.GONE);
//                astore.setVisibility(View.GONE);
                break;
            case R.id.restaurant:
                mMap.clear();
                String resturant = "restuarant";
                url = getUrl(latitude, longitude, resturant);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Showing Nearby Restaurants", Toast.LENGTH_SHORT).show();
//                ahospital.setVisibility(View.GONE);
//                abank.setVisibility(View.GONE);
//                aschool.setVisibility(View.GONE);
//                acafe.setVisibility(View.GONE);
//                aatm.setVisibility(View.GONE);
//                apolice.setVisibility(View.GONE);
//                arestaurant.setVisibility(View.VISIBLE);
//                astore.setVisibility(View.GONE);
                break;
            case R.id.atm:
                mMap.clear();
                String atm = "atm";
                url = getUrl(latitude, longitude, atm);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Showing Nearby ATM's", Toast.LENGTH_SHORT).show();
//                ahospital.setVisibility(View.GONE);
//                abank.setVisibility(View.GONE);
//                aschool.setVisibility(View.GONE);
//                acafe.setVisibility(View.GONE);
//                aatm.setVisibility(View.VISIBLE);
//                apolice.setVisibility(View.GONE);
//                arestaurant.setVisibility(View.GONE);
//                astore.setVisibility(View.GONE);
                break;
            case R.id.bank:
                mMap.clear();
                String bank = "bank";
                url = getUrl(latitude, longitude, bank);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Showing Nearby bank", Toast.LENGTH_SHORT).show();
//                ahospital.setVisibility(View.GONE);
//                abank.setVisibility(View.VISIBLE);
//                aschool.setVisibility(View.GONE);
//                acafe.setVisibility(View.GONE);
//                aatm.setVisibility(View.GONE);
//                apolice.setVisibility(View.GONE);
//                arestaurant.setVisibility(View.GONE);
//                astore.setVisibility(View.GONE);
                break;
            case R.id.cafe:
                mMap.clear();
                String cafe = "cafe";
                url = getUrl(latitude, longitude, cafe);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Showing Nearby cafe", Toast.LENGTH_SHORT).show();
//                ahospital.setVisibility(View.GONE);
//                abank.setVisibility(View.GONE);
//                aschool.setVisibility(View.GONE);
//                acafe.setVisibility(View.VISIBLE);
//                aatm.setVisibility(View.GONE);
//                apolice.setVisibility(View.GONE);
//                arestaurant.setVisibility(View.GONE);
//                astore.setVisibility(View.GONE);
                break;
            case R.id.police:
                mMap.clear();
                String police = "police";
                url = getUrl(latitude, longitude, police);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Showing Nearby police", Toast.LENGTH_SHORT).show();
//                ahospital.setVisibility(View.GONE);
//                abank.setVisibility(View.GONE);
//                aschool.setVisibility(View.GONE);
//                acafe.setVisibility(View.GONE);
//                aatm.setVisibility(View.GONE);
//                apolice.setVisibility(View.VISIBLE);
//                arestaurant.setVisibility(View.GONE);
//                astore.setVisibility(View.GONE);
                break;
            case R.id.store:
                mMap.clear();
                String store = "store";
                url = getUrl(latitude, longitude, store);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Showing Nearby store", Toast.LENGTH_SHORT).show();
//                ahospital.setVisibility(View.GONE);
//                abank.setVisibility(View.GONE);
//                aschool.setVisibility(View.GONE);
//                acafe.setVisibility(View.GONE);
//                aatm.setVisibility(View.GONE);
//                apolice.setVisibility(View.GONE);
//                arestaurant.setVisibility(View.GONE);
//                astore.setVisibility(View.VISIBLE);
                break;
        }
    }


    private String getUrl(double latitude , double longitude , String nearbyPlace)
    {

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+"AIzaSyC7RNulku1jeT5wcOkFiX4xENLZlXc5Wm0");

        Log.d("MapsActivity", "url = "+googlePlaceUrl.toString());
        return googlePlaceUrl.toString();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

//ads...............................................................................................
private InterstitialAd newInterstitialAd() {
    InterstitialAd interstitialAd = new InterstitialAd(this);
    interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
    interstitialAd.setAdListener(new AdListener() {
        @Override
        public void onAdLoaded() {
        //    Toast.makeText(MapsActivity.this, "ad loaded", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdFailedToLoad(int errorCode) {
            loadInterstitial();
        }

        @Override
        public void onAdClosed() {
            // Proceed to the next level.
        }
    });
    return interstitialAd;
}

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and reload the ad.
            mInterstitialAd.show();
    }

    private void loadInterstitial() {
        // Disable the next level button and load the ad.
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        mInterstitialAd.loadAd(adRequest);
    }

}
