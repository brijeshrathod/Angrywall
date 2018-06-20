package com.angrywallpaper.name;

import android.app.Activity;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.widget.Button;
import android.view.View.OnClickListener;

import android.R.layout;


import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

/**
 * Created by Angry on 03/20/2018.
 */

public class FragSettings extends Fragment {

    Activity context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        context= getActivity();
        View view = inflater.inflate(R.layout.frag_settings, container, false);
        return view;
    }

   @Override
   public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
     super.onViewCreated(view, savedInstanceState);

        //You can add your variables here.
        //Add your own items, events or objects here.

    }

    public void onStart() {

        super.onStart();
        Button sdssadsdsd=(Button)context.findViewById(R.id.sdssadsdsd);
        sdssadsdsd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
             Intent intent=new Intent(context,SecondActivity.class);

                startActivity(intent);
            }
        });

    }
}
