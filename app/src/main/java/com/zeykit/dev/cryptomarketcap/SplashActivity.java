package com.zeykit.dev.cryptomarketcap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    long delay = 2000;
    private Intent mainIntent;
    private Intent pinnedIntent;
    Timer runSplash;
    TimerTask showSplash;

    SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash_layout);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String activityToLaunch = sharedPreferences.getString("launching_preference", "Main Activity");

        runSplash = new Timer();
        showSplash = new TimerTask() {
            @Override
            public void run() {
                finish();
                if (activityToLaunch.equals("Main activity") || activityToLaunch.equals("Activité principale")) {
                    mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                }
                else if (activityToLaunch.equals("Pinned coins") || activityToLaunch.equals("Monnaies épinglées")){
                    mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    pinnedIntent = new Intent(SplashActivity.this, PinnedCoinsActivity.class);
                    startActivity(pinnedIntent);
                } else {
                    mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                }
            }
        };
        runSplash.schedule(showSplash, delay);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
