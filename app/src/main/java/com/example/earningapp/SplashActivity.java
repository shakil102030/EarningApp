package com.example.earningapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.os.Handler;
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_TIME = 3000;
    private ProgressBar progressBarSplash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressBarSplash = findViewById(R.id.progressBarSplashId);
        ProgressPlay();
        if(isNetworkAvailable())
            new Handler(getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, SPLASH_TIME);
         else {
            //Notify user they aren't connected
            new Handler(getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    new SweetAlertDialog(SplashActivity.this)
                            .setContentText("You aren't connected to the internet.")
                            .setConfirmText("Ok")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                }
                            })
                            .show();
                }
            }, SPLASH_TIME);
            finish();

        }
    }
    private void ProgressPlay() {
        ObjectAnimator.ofInt(progressBarSplash, "progress", 100)
                .setDuration(5000)
                .start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

}
