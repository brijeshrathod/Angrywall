package com.angrywallpaper.name;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import de.hdodenhof.circleimageview.CircleImageView;

public class SplashActivity extends AppCompatActivity {

    ProgressBar progressBarCircle;
    CircleImageView circleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slidein);

        circleImageView = findViewById(R.id.xxxAppIcon);
        progressBarCircle = findViewById(R.id.splashProgress);

        progressBarCircle.setAnimation(animation);

        ObjectAnimator.ofInt(progressBarCircle, "progress", 100)
                .setDuration(2500)
                .start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (isNetworkAvailable()) {
                    Intent homeIntent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
                    finish();
                } else {
                    Intent NoInternetIntent = new Intent(SplashActivity.this, InternetActivity.class);
                    startActivity(NoInternetIntent);
                    finish();
                }

            }

            //I've given a delay of 3 seconds :)
        }, 3000);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
