package com.FamilyLocator.FamilyLocator.SplashScreen;

import android.content.Intent;

import com.FamilyLocator.FamilyLocator.R;
import com.daimajia.androidanimations.library.Techniques;
import com.FamilyLocator.FamilyLocator.PrivacyPolicy.PrivacyPolicy;

import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

/**
 * Created by RAJA on 11-05-2018.
 */

public class SplashScreen extends AwesomeSplash {
    //DO NOT OVERRIDE onCreate()!
    //if you need to start some services do it in initSplash()!

    @Override
    public void initSplash(ConfigSplash configSplash) {

			/* you don't have to override every property */
        getSupportActionBar().hide();
        //Customize Circular Reveal
        configSplash.setBackgroundColor(R.color.splash); //any color you want form colors.xml
        configSplash.setAnimCircularRevealDuration(0); //int ms
        configSplash.setRevealFlagX(Flags.REVEAL_RIGHT);  //or Flags.REVEAL_LEFT
        configSplash.setRevealFlagY(Flags.REVEAL_BOTTOM); //or Flags.REVEAL_TOP

        //Choose LOGO OR PATH; if you don't provide String value for path it's logo by default

        //Customize Logo
      //  configSplash.setLogoSplash(R.drawable.lti1); //or any other drawable
        configSplash.setAnimLogoSplashDuration(0); //int ms
        configSplash.setAnimLogoSplashTechnique(Techniques.FadeIn); //choose one form Techniques (ref: https://github.com/daimajia/AndroidViewAnimations)

        //Customize Path
//        configSplash.setPathSplash(SyncStateContract.Constants.DROID_LOGO); //set path String
//        configSplash.setOriginalHeight(400); //in relation to your svg (path) resource
//        configSplash.setOriginalWidth(400); //in relation to your svg (path) resource
//        configSplash.setAnimPathStrokeDrawingDuration(3000);
//        configSplash.setPathSplashStrokeSize(3); //I advise value be <5
//        configSplash.setPathSplashStrokeColor(R.color.accent); //any color you want form colors.xml
//        configSplash.setAnimPathFillingDuration(3000);
//        configSplash.setPathSplashFillColor(R.color.Wheat); //path object filling color


        //Customize Title
        configSplash.setTitleSplash("Family Locator");
        configSplash.setTitleTextColor(R.color.black);
        configSplash.setTitleTextSize(30f); //float value
        configSplash.setAnimTitleDuration(2000);
        configSplash.setAnimTitleTechnique(Techniques.FadeIn);
       // configSplash.setTitleFont("fonts/3.ttf"); //provide string to your font located in assets/fonts/

    }

    @Override
    public void animationsFinished() {

        //transit to another activity here
        //or do whatever you want
        startActivity(new Intent(getApplicationContext(), PrivacyPolicy.class));
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }
}