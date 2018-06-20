package com.angrywallpaper.name;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Element adsElement = new Element();
        adsElement.setTitle("All the Characters of  wallpapers are taken from clash of clan and clash royale fan kit. The content in this app is not affiliated with, sponsored, or specifically approved by any company. This application is mainly for entertainment and fun purpose for all clan of clan and clash royale fans");


        View aboutPage= new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.abcd)
                .setDescription("Angryclash")
                .addItem(new Element().setTitle("Version 1.0"))
                .addGroup("Contact With Me")
                .addEmail("angrybrijesh44@gmail.com")
                .addWebsite("http://angryclash.blogspot.com")
                .addInstagram("angry_clash")
                .addFacebook("Angryclash")
                .addTwitter("clash_angry")
                .addGroup("DISCLAIMER:")
                .addItem(adsElement)
                .addGroup("CREDIT:")
                .addItem(new Element().setTitle("Wallpaper background by Unsplesh.com, Mr.lucky_visuals and Binit_joshi"))
                .addItem(createCopyright())
                .create();

        setContentView(aboutPage);
    }

    private Element createCopyright() {


        Element copyright=new Element();
        final String copyrightString= String.format("CopyRight %d By AngryClash", Calendar.getInstance().get(Calendar.YEAR));
        copyright.setTitle(copyrightString);
        copyright.setIcon(R.mipmap.ic_launcher);
        copyright.setGravity(Gravity.CENTER);
        copyright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(SecondActivity.this,copyrightString,Toast.LENGTH_SHORT).show();

            }
        });

        return copyright;
    }
}
