package com.angrywallpaper.name;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.jorgecastilloprz.FABProgressCircle;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FragWallpaper extends Fragment {



    DatabaseReference databaseReference;
    Dialog imageDialog;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    ProgressDialog progressDialog;
    LinearLayoutManager layoutManager;
    List<ImageUploadInfo> list = new ArrayList<>();
    ImageView info_imageView, imageBackButton;
    TextView info_textView;
    String myImageName, myImageUrl, isImageReady;
    Bitmap imageViewToBitmap;
    RelativeLayout info_relativeLayout;
    View mySuperView;

    FABProgressCircle btsetWallOut, btsaveWallOut, btshareWallOut;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_wallpaper, container, false);

        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading Images From Server.");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.setMessage("Looks like your Internet is too slow!");
            }
        }, 10000);

        databaseReference = FirebaseDatabase.getInstance().getReference("wallpaper_database");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ImageUploadInfo imageUploadInfo = postSnapshot.getValue(ImageUploadInfo.class);
                    list.add(imageUploadInfo);
                }
                adapter = new RecyclerViewAdapter(getActivity(), list);
                recyclerView.setAdapter(adapter);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {

                        myImageName = list.get(position).getImageName();
                        myImageUrl = list.get(position).getImageURL();
                        //getBitmapFromURL(myImageUrl);
                        setupMyDialog();
                    }
                })
        );

        return view;
    }

    //Starts the dialog session
    private void setupMyDialog() {

        imageDialog = new Dialog(getActivity(), R.style.CustomDialogTheme);
        //imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(imageDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        imageDialog.setContentView(R.layout.wallpaper_info);

        info_relativeLayout = imageDialog.findViewById(R.id.info_relativeLayout);
        mySuperView = imageDialog.findViewById(R.id.mySuperView);

        info_imageView = imageDialog.findViewById(R.id.info_ImageView);
        info_textView = imageDialog.findViewById(R.id.info_ImageName);

        btsetWallOut = imageDialog.findViewById(R.id.btsetWallOut);
        btsaveWallOut = imageDialog.findViewById(R.id.btsaveWallOut);
        btshareWallOut = imageDialog.findViewById(R.id.btshareWallOut);
        imageBackButton = imageDialog.findViewById(R.id.imageBackButton);

        info_textView.setText(myImageName);
        Glide.with(getActivity())
                .load(myImageUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        isImageReady = "no";
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        //finalDrawable = resource;
                        isImageReady = "yes";
                        imageViewToBitmap = ((BitmapDrawable) resource).getBitmap();
                        return false;
                    }
                })
                .into(info_imageView);
/*
        if (imageViewToBitmap != null) {

            Palette.from(imageViewToBitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    int defaultValue = 0x000000;
                    int vibrant = palette.getVibrantColor(defaultValue);
                *//*
                int vibrantLight = palette.getLightVibrantColor(defaultValue);
                int vibrantDark = palette.getDarkVibrantColor(defaultValue);
                int muted = palette.getMutedColor(defaultValue);
                int mutedLight = palette.getLightMutedColor(defaultValue);
                int mutedDark = palette.getDarkMutedColor(defaultValue);*//*

                    mySuperView.setBackgroundColor(vibrant);
                }
            });
        } else {
            Toast.makeText(getActivity(), "Empty", Toast.LENGTH_SHORT).show();
        }
        */

        btsetWallOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btsetWallOut.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SetAsWallpaper();
                    }
                }, 3000);
            }
        });

        btsaveWallOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btsaveWallOut.show();
                if (isImageReady.equals("yes")) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            saveImage(imageViewToBitmap, myImageName);
                        }
                    },2500);

                } else {
                    ShowSnackBar("Sorry, image not fully loaded!");
                }
            }
        });

        btshareWallOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btshareWallOut.show();
                if (isImageReady.equals("yes")) {
                    shareImage();
                }
            }
        });

        imageBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageDialog.dismiss();
            }
        });
        imageDialog.show();
        imageDialog.getWindow().setAttributes(lp);
    }

    private void SetAsWallpaper() {
        WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getActivity());
        try {
            myWallpaperManager.setBitmap(imageViewToBitmap);
            btsetWallOut.hide();

        } catch (IOException e) {
            ShowSnackBar("Sorry, there was an error!");
            btsetWallOut.hide();
            e.printStackTrace();
        }
    }

    private void saveImage(Bitmap finalBitmap, String image_name) {
        String root = Environment.getExternalStorageDirectory().toString();
        String app_name = getString(R.string.app_name);
        File myDir = new File(root + "/" + app_name);
        myDir.mkdirs();
        String fname = image_name + ".png";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            btsaveWallOut.hide();
        } catch (Exception e) {
            ShowSnackBar("Error while saving image...");
        }
    }

    private void shareImage(){
        //Bitmap b =BitmapFactory.decodeResource(getResources(),R.drawable.userimage);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        imageViewToBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),
                imageViewToBitmap, "Title", null);
        Uri imageUri =  Uri.parse(path);
        share.putExtra(Intent.EXTRA_STREAM, imageUri);
        startActivity(Intent.createChooser(share, "Share Image Via"));
        btshareWallOut.hide();
    }

    private void ShowSnackBar(String snackBarText) {
        Snackbar.make(getView(), snackBarText, Snackbar.LENGTH_LONG).show();
    }
}