package com.FamilyLocator.FamilyLocator.MoNumberSearchLocation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.FamilyLocator.FamilyLocator.R;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.Objects;

/**
 * Created by RAJA on 15-04-2018.
 */

public class InsertMoNumber extends AppCompatActivity {
    EditText mo_no;
    TextView search;
    LinearLayout current;
    String number,no,real_no;
    TextView circle,operator;
    DatabaseReference firebaseDatabase;
    RewardedVideoAd mAd;

    @Override
    protected void onResume() {
        super.onResume();
        mAd.show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.getInstance();
        setContentView(R.layout.insert_mo_no);
        firebaseDatabase= FirebaseDatabase.getInstance().getReference();
        mo_no=findViewById(R.id.mo_no);
        search=findViewById(R.id.search);
        current=findViewById(R.id.current);
        operator=findViewById(R.id.operator);
        circle=findViewById(R.id.circle);
        final Intent intent=getIntent();
        no=intent.getStringExtra("phoneNo");
        mAd= MobileAds.getRewardedVideoAdInstance(this);
        mAd.loadAd(getString(R.string.video_ad_unit_id),new AdRequest.Builder().build());
        AdView adView = (AdView) findViewById(R.id.adView3);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);
        System.out.println(no+"..............................");
if (no!=null){
    no_from_contact();
    volley(real_no);
}
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mo_no.getText().toString().length()==10) {
                    number = mo_no.getText().toString();
                    //   Toast.makeText(InsertMoNumber.this, number, Toast.LENGTH_SHORT).show();

                    volley(number);
                }else {
                    mo_no.setError("Mobile number should be 10 digits");
                }
            }
        });
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
         current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                     try {
                         String latitude = (String) dataSnapshot.child(mo_no.getText().toString()).child("latitude").getValue();
                         String longitude = (String) dataSnapshot.child(mo_no.getText().toString()).child("longitude").getValue();

//                        Float lat=Float.valueOf(latitude);
//                        Float lon=Float.valueOf(longitude);
//                        if (latitude==null||longitude==null){

//                        }
//                        else {
                        if (latitude!=null){

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:"+latitude+","+longitude+"?q="+latitude+","+longitude+"(\""+mo_no.getText().toString()+"\":Last Location)"));
                        startActivity(intent);
                     //   Toast.makeText(getApplicationContext(), latitude + " " + longitude, Toast.LENGTH_SHORT).show();
                              }
                              else {
                            builder.setMessage("You can track his/her exact location if this app is installed on their device.Do you want to share this app?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            shareTextUrl();
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
                    }catch (Exception e){
                         builder.setMessage("You can track his/her exact location if this app is installed on their device.Do you want to share this app?")
                                 .setCancelable(false)
                                 .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                     public void onClick(DialogInterface dialog, int id) {
                                         shareTextUrl();
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
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
    }


    public void volley(String a){
//putting entered value into URL
        findViewById(R.id.progress).setVisibility(View.VISIBLE);
        mo_no.setText(a);
          String URL = "https://api.datayuge.com/v1/lookup/" + a;
        final StringRequest request = new StringRequest(URL, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                if(response!=null){
                    findViewById(R.id.progress).setVisibility(View.GONE);
                    findViewById(R.id.data).setVisibility(View.VISIBLE);
                    findViewById(R.id.location).setVisibility(View.VISIBLE);
                    operator.setVisibility(View.VISIBLE);
                    findViewById(R.id.clickhere).setVisibility(View.VISIBLE);
                }
                Gson gson=new Gson();
                Data data=gson.fromJson(response,Data.class);
                String circle1=data.getCircle();
                String operator1=data.getOperator();
                circle.setText(circle1);
                operator.setText(operator1);
                try {
                    if (data.getMsg() != null) {
                        findViewById(R.id.progress).setVisibility(View.GONE);
                     operator.setVisibility(View.GONE);
                     circle.setText(data.getMsg());
                     circle.setTextSize(20f);
                     circle.setAllCaps(true);
                     findViewById(R.id.clickhere).setVisibility(View.GONE);
                        findViewById(R.id.location).setVisibility(View.GONE);
                    }
                }catch (Exception e){

                }
            }
        },
//Error listener:Server error
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        findViewById(R.id.progress).setVisibility(View.GONE);
                       // Toast.makeText(getApplicationContext(), "Server error Please retry", Toast.LENGTH_SHORT).show();
                    }
                });
        //Adding request Queue
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }
    public void no_from_contact(){
        char[] timestamp=new char[no.length()];
        for (int i=0;i<no.length();i++){
            timestamp[i]=no.charAt(i);
        }
        System.out.println(timestamp.length+"yhi hai...................");
        // System.out.println(timestamp[3]);
        real_no="";
        if (timestamp.length==13) {
            for (int j = 12; j >= 3; j--) {
                // System.out.println(timestamp[j]);
                String a = String.valueOf(timestamp[j]);
                real_no = a + real_no;
            }
        }
        else if (timestamp.length==16) {
            for (int j = 15 ; j >= 3; j--) {
                // System.out.println(timestamp[j]);
                String a = String.valueOf(timestamp[j]);
                  if (Objects.equals(a, " ")){

                }else {
                    real_no = a + real_no;
                }

            }
        }
        else if (timestamp.length==15) {
            for (int j = 14 ; j >= 3; j--) {
                // System.out.println(timestamp[j]);
                String a = String.valueOf(timestamp[j]);
                if (Objects.equals(a, " ")){

                }else {
                    real_no = a + real_no;
                }

            }
        }
        else if (timestamp.length==12) {
            for (int j = 11 ; j >= 0; j--) {
                // System.out.println(timestamp[j]);
                String a = String.valueOf(timestamp[j]);
                if (Objects.equals(a, " ")){

                }
                else if (Objects.equals(a,"-")){

                }
                else {
                    real_no = a + real_no;
                   }

            }
        }
        else if (timestamp.length==11) {
            for (int j = 10 ; j >= 0; j--) {
                // System.out.println(timestamp[j]);
                String a = String.valueOf(timestamp[j]);
                if (Objects.equals(a, " ")){

                }
                else if (Objects.equals(a,"-")){

                }
                else {
                    real_no = a + real_no;
                }

            }
        }
        else if (timestamp.length==10){
            System.out.println(no);
             real_no=no;

        }
        else {
            Toast.makeText(this, "Please select Any Valid Mobile Number", Toast.LENGTH_SHORT).show();
        }
        System.out.println(real_no);
    }

//    @Override
//    public void onBackPressed() {
//        Intent intent =new Intent(getApplicationContext(),MainActivity.class);
//        startActivity(intent);
//        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
//    }
    private void shareTextUrl() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Track location of your Family & Friends using Family Locator App.");
        share.putExtra(Intent.EXTRA_TEXT, "Hi,I would like you to download the Family Locator App," +
                "So i can find where you are currently and Navigate to you." +
                "Also you can use this App to find or locate your friends,family and also your lost mobile.\n" +
                "https://drive.google.com/open?id=1D-ICG_1sPqvke3MbyrGV0TWqd81sWOQ1");

        startActivity(Intent.createChooser(share, "Share link!"));
    }
}
