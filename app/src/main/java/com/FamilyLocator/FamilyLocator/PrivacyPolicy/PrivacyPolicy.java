package com.FamilyLocator.FamilyLocator.PrivacyPolicy;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.FamilyLocator.FamilyLocator.PhoneAuth.PhoneAuthActivity;
import com.FamilyLocator.FamilyLocator.R;


import net.khirr.android.privacypolicy.PrivacyPolicyDialog;

/**
 * Created by RAJA on 18-05-2018.
 */

public class PrivacyPolicy extends AppCompatActivity {
    @Override
    protected void onResume() {
        policy();
        super.onResume();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_policy);
        getSupportActionBar().hide();

policy();
    }

    public void policy(){
        //  Params: context, termsOfService url, privacyPolicyUrl
        PrivacyPolicyDialog dialog = new PrivacyPolicyDialog(this,
                "",
                "https://rishabhnayak.ml/index.html");

        final Intent intent = new Intent(this, PhoneAuthActivity.class);

        dialog.setOnClickListener(new PrivacyPolicyDialog.OnClickListener() {
            @Override
            public void onAccept(boolean isFirstTime) {
                Log.e("MainActivity", "Policies accepted");
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancel() {
                Log.e("MainActivity", "Policies not accepted");
                Intent intent=new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        dialog.addPoliceLine("This Family Locator App stores Mobile Number in the Database which will not be sold to any third-party companies or Used by us for any Mis-leading things.");
        dialog.addPoliceLine("This Family Locator App stores the location of the users in our Database only for the safety purpose where you or your friends or family members can track each other to know their location and find them easily in any emergency situation. We assure we will not misuse this feature and this serves only for the Safety Purpose.");
        dialog.addPoliceLine("This Family Locator App allows to share your Live Location to anyone for safety purpose to find.");
        dialog.addPoliceLine("We developers will not be responsible for any misuse of this app. We have developed this App to ensure safety purpose as to track your friends & family anytime.");
        dialog.addPoliceLine("This Family Locator App will work only when the App is installed on their respective mobile phone.");
        //  Customizing (Optional)
        dialog.setTitleTextColor(Color.parseColor("#222222"));
        dialog.setAcceptButtonColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        //  Title
        dialog.setTitle("Terms of Service");

        //  {terms}Terms of Service{/terms} is replaced by a link to your terms
        //  {privacy}Privacy Policy{/privacy} is replaced by a link to your privacy policy
        dialog.setTermsOfServiceSubtitle("If you click on {accept}, you acknowledge that it makes the content present and all the content of our <b>Terms of Service</b> and implies that you have read our {privacy}Privacy Policy{privacy}.");

        dialog.show();
    }
}
