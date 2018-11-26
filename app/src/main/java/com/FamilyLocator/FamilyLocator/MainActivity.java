package com.FamilyLocator.FamilyLocator;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.FamilyLocator.FamilyLocator.ForceUpdate.ForceUpdateChecker;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.FamilyLocator.FamilyLocator.MoNumberSearchLocation.InsertMoNumber;
import com.FamilyLocator.FamilyLocator.PhoneAuth.UserInformation;
import com.FamilyLocator.FamilyLocator.SearchPlacesNearMe.MapsActivity;
import com.podcopic.animationlib.library.AnimationType;
import com.podcopic.animationlib.library.StartSmartAnimation;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity implements LocationListener,ForceUpdateChecker.OnUpdateNeededListener{
    private static final int RESULT_PICK_CONTACT = 85500;
    DatabaseReference firebaseDatabase;
    String number;
    String   mobile_number;
    protected LocationManager locationManager;
    protected Context context;
    private Toolbar mTopToolbar;
    ProgressBar progressBar;



    @Override
    protected void onResume() {

        displayLocationSettingsRequest(getApplicationContext());
        ForceUpdateChecker.with(this).onUpdateNeeded(this).check();
        super.onResume();

    }


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.getInstance();
        setContentView(R.layout.activity_beginning);
        firebaseDatabase=FirebaseDatabase.getInstance().getReference();

        mTopToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        mTopToolbar.inflateMenu(R.menu.menu);
        mTopToolbar.setTitle("Family Locator");
        progressBar=findViewById(R.id.progress);



        //animations
        StartSmartAnimation.startAnimation( findViewById(R.id.searchcard) , AnimationType.SlideInDown , 800 , 00 , true );
        StartSmartAnimation.startAnimation( findViewById(R.id.down) , AnimationType.SlideInUp , 800 , 00 , true );
//detect internet and show the data
        if(isNetworkStatusAvialable (getApplicationContext())) {
          //  Toast.makeText(getApplicationContext(), "Internet detected", Toast.LENGTH_SHORT).show();
        } else {
// Instantiate an AlertDialog.Builder with its constructor

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("NO INTERNET");
            alertDialogBuilder.setMessage("PLEASE CHECK YOUR INTERNET CONNECTION");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
//                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                }
            });
            alertDialogBuilder.show();
        }
//
//
//....intent check high accuracy....................................................................
//        accuracyCheck();
//..................................................................................................

//number intent(retrieving mobile number from Phone Auth Activity)..................................
        number=getIntent().getStringExtra("number");
        SharedPreferences sp = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (number!=null) {
        editor.putString("mo_number", number);
        editor.apply();
        }
//..................................................................................................
//ads.........banner

        AdView adView = findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);
//on click listeners................................................................................
        LinearLayout button=findViewById(R.id.place_near_me);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
               // overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });

        LinearLayout select=findViewById(R.id.select_contactlist);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
               // overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });

       LinearLayout search=findViewById(R.id.search_no);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), InsertMoNumber.class);
                startActivity(intent);
//                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });

//        findViewById(R.id.rate).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("https://drive.google.com/open?id=1D-ICG_1sPqvke3MbyrGV0TWqd81sWOQ1"));
//                startActivity(intent);
//            }
//        });
        findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Track location of your Family & Friends using Family Locator App.");
                String shareBodyText = "Hi,I would like you to download the Family Locator App," +
                        "So i can find where you are currently and Navigate to you." +
                        "Also you can use this App to find or locate your friends,family and also your lost mobile.\n" +
                        "https://drive.google.com/open?id=1D-ICG_1sPqvke3MbyrGV0TWqd81sWOQ1";
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText);
                startActivity(Intent.createChooser(sharingIntent, "Share Google Play Link"));
            }
        });


//..................................................................................................


//permission method.................................................................................





//..................................................................................................

//current location (location manager)...............................................................
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//..................................................................................................

    }

//Location mode method..............................................................................
    private int getLocationMode(Context context) throws Settings.SettingNotFoundException {
        return Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);

    }
//..................................................................................................



//menu item.........................................................................................


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch(item.getItemId()){
//            case R.id.logoutMenu:{
//
//            }
//            case R.id.profileMenu:
//                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
//        }
//        return super.onOptionsItemSelected(item);
//    }
//..................................................................................................

//on back pressed...................................................................................
    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent=new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
//..................................................................................................

//current location changed (user)...................................................................
    @Override
    public void onLocationChanged(Location location) {

            SharedPreferences sp = getSharedPreferences("pref", MODE_PRIVATE);
            mobile_number = sp.getString("mo_number", number);
            if (mobile_number!=null){
                String lat = String.valueOf(location.getLatitude());
                String lon = String.valueOf(location.getLongitude());

             UserInformation userInformation = new UserInformation(lat, lon);
                firebaseDatabase.child(mobile_number).setValue(userInformation);
                System.out.println("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
                findViewById(R.id.activity_main).setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }else {
               // Toast.makeText(context, "mobile no is null", Toast.LENGTH_SHORT).show();
            }


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

//..................................................................................................

//Contact picker.......................................................................................
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // check whether the result is ok
    if (resultCode == RESULT_OK) {
        // Check for the request code, we might be usign multiple startActivityForReslut
        switch (requestCode) {
            case RESULT_PICK_CONTACT:
                contactPicked(data);
                break;
        }
    } else {
        Log.e("MainActivity", "Failed to pick contact");
    }
}
    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            String phoneNo = null ;
            String name = null;
            // getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();
            //Query the content uri
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            // column index of the phone number
            int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            // column index of the contact name
            int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            phoneNo = cursor.getString(phoneIndex);
            name = cursor.getString(nameIndex);
            // Set the value to the textviews
            Intent intent=new Intent(getApplicationContext(), InsertMoNumber.class);
            intent.putExtra("phoneNo",phoneNo);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//..................................................................................................
//check internet connection
public static boolean isNetworkStatusAvialable (Context context) {
    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (connectivityManager != null)
    {
        NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
        if(netInfos != null)
        {
            return netInfos.isConnected();
        }
    }
    return false;
}

//high accuracy check
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        Snackbar.make(findViewById(android.R.id.content), "Family Locator is getting your location.", Snackbar.LENGTH_LONG)
                                .show();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    @Override
    public void onUpdateNeeded(final String updateUrl) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("New version available")
                .setMessage("Please, update app to new version to continue.")
                .setCancelable(false)
                .setPositiveButton("Update",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                redirectStore(updateUrl);
                            }
                        }).create();
        dialog.show();
    }
    private void redirectStore(String updateUrl) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

//..................................................................................................

}


