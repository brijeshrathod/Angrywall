package com.angrywallpaper.name;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class InternetActivity extends AppCompatActivity {

    ProgressBar progressBar;
    Button retry_button;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet);

        retry_button = (Button) findViewById(R.id.internet_retry_button);
        progressBar = (ProgressBar) findViewById(R.id.internet_retry_progress);
        linearLayout = (LinearLayout) findViewById(R.id.internetLinearLayout);

        retry_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                retry_button.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                if (isNetworkAvailable()) {
                    Intent homeIntent = new Intent(InternetActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
                    finish();
                } else {
                    progressBar.setVisibility(View.GONE);
                    retry_button.setVisibility(View.VISIBLE);
                    Snackbar.make(linearLayout, "Still no connectivity found!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
