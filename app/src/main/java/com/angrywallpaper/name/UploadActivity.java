package com.angrywallpaper.name;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;

import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class UploadActivity extends AppCompatActivity {

    String Storage_Path = "wallpapers";
    public static final String Database_Path = "wallpaper_database";

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView actualImageView, btChooseImage;
    private LinearLayout hideLayout;
    private TextView actualSizeTextView;
    private TextView compressedSizeTextView;
    private File actualImage;
    private File compressedImage;
    private ProgressDialog progressDialog;
    private Button UploadButton;
    private EditText ImageName;
    private Bitmap BitmapLocation;
    private String StringImageName;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        UploadButton = findViewById(R.id.ButtonUploadImage);
        ImageName = findViewById(R.id.ImageNameEditText);

        actualImageView = findViewById(R.id.ShowImageView);
        actualSizeTextView = findViewById(R.id.actual_size);
        compressedSizeTextView = findViewById(R.id.compressed_size);

        hideLayout = findViewById(R.id.hideLayout);
        btChooseImage = findViewById(R.id.ImgChooseImage);

        clearImage();

        btChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        UploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringImageName = ImageName.getText().toString().trim();
                if (StringImageName.isEmpty()) {
                    Snackbar.make(view, "Give Wallpaper a name!", Snackbar.LENGTH_LONG).show();
                } else {
                    progressDialog.setMessage("While we're uploading your image...");
                    progressDialog.show();
                    UploadWallpaper();
                    //UploadImageFileToFirebaseStorage();
                }

            }
        });
    }

    public void chooseImage() {
        hideLayout.setVisibility(View.GONE);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    public void compressImage() {
        if (actualImage == null) {
            ShowSnackBar("Choose an image!");
        } else {
            new Compressor(this)
                    .compressToFileAsFlowable(actualImage)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new io.reactivex.functions.Consumer<File>() {
                        @Override
                        public void accept(File file) throws Exception {
                            compressedImage = file;
                            setCompressedImage();
                            hideLayout.setVisibility(View.VISIBLE);
                        }
                    }, new io.reactivex.functions.Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            ShowSnackBar(throwable.getMessage());
                        }
                    });
        }
    }

    private void setCompressedImage() {
        BitmapLocation = BitmapFactory.decodeFile(compressedImage.getAbsolutePath());
        actualImageView.setImageBitmap(BitmapLocation);
        compressedSizeTextView.setText(String.format("Size : %s", getReadableFileSize(compressedImage.length())));

    }

    private void clearImage() {
        actualImageView.setImageResource(R.drawable.no_image);
        compressedSizeTextView.setText("Size : -");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data == null) {
                ShowSnackBar("Failed to open picture!");
                return;
            }
            try {
                actualImage = FileUtil.from(this, data.getData());
                //BitmapLocation = BitmapFactory.decodeFile(actualImage.getAbsolutePath());
                //actualImageView.setImageBitmap(BitmapLocation);
                actualSizeTextView.setText(String.format("Size : %s", getReadableFileSize(actualImage.length())));

                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Selected image is being compressed...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        compressImage();
                        progressDialog.hide();
                    }
                }, 2000);

            } catch (IOException e) {
                ShowSnackBar("Failed to read picture data!");
                e.printStackTrace();
            }
        }
    }

    private void UploadWallpaper() {
        //storageReference.child(Storage_Path + System.currentTimeMillis() + "." + checkEmpty);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BitmapLocation.compress((Bitmap.CompressFormat.JPEG), 100, baos);
        byte[] data = baos.toByteArray();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference(Storage_Path).child(StringImageName);

        /*StorageReference storageReference2nd = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));
        storageReference2nd.putFile(data);*/
        //s = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + getActivity().GetFileExtension(FilePathUri));
        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                ShowSnackBar("Upload failed, something went wrong!");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
/*
                String ImageUploadId = databaseReference.push().getKey();
                ImageUploadInfo imageUploadInfo = new ImageUploadInfo(StringImageName, taskSnapshot.getDownloadUrl().toString());
                databaseReference.child(ImageUploadId).setValue(imageUploadInfo);
*/

                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                /*Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Toast.makeText(getActivity(), "Link: " + downloadUrl, Toast.LENGTH_SHORT).show();*/

                String TempImageName = ImageName.getText().toString().trim();
                actualImageView.setImageResource(R.drawable.no_image);
                ImageName.setText("");
                @SuppressWarnings("VisibleForTests")
                ImageUploadInfo imageUploadInfo = new ImageUploadInfo(TempImageName, taskSnapshot.getDownloadUrl().toString());
                String ImageUploadId = databaseReference.push().getKey();
                databaseReference.child(ImageUploadId).setValue(imageUploadInfo);
                progressDialog.dismiss();
                Snackbar.make(hideLayout, "Your Wallpaper is up, Thanks!", Snackbar.LENGTH_LONG)
                        .setAction("See", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent homeIntent = new Intent(UploadActivity.this, HomeActivity.class);
                                startActivity(homeIntent);
                            }
                        })
                        .show();
            }
        });
    }
/*
    public void UploadImageFileToFirebaseStorage() {
        if (BitmapLocation != null) {
            progressDialog.setTitle("Image is Uploading...");
            progressDialog.show();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BitmapLocation.compress((Bitmap.CompressFormat.JPEG), 100, baos);
            byte[] data = baos.toByteArray();

            StorageReference storageReference2nd = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + getActivity().GetFileExtension(FilePathUri));
            storageReference2nd.putFile()
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String TempImageName = ImageName.getText().toString().trim();
                            progressDialog.dismiss();
                            Snackbar.make(getView(), "Your Wallpaper is up, Thanks!", Snackbar.LENGTH_LONG).show();
                            actualImageView.setImageResource(R.drawable.no_image);
                            ImageName.setText("");
                            @SuppressWarnings("VisibleForTests")
                            ImageUploadInfo imageUploadInfo = new ImageUploadInfo(TempImageName, taskSnapshot.getDownloadUrl().toString());
                            String ImageUploadId = databaseReference.push().getKey();
                            databaseReference.child(ImageUploadId).setValue(imageUploadInfo);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.setTitle("Image is being uploaded...");
                        }
                    });
        } else {
            Snackbar.make(getView(), "No image...? Are you playin?!", Snackbar.LENGTH_LONG).show();
        }
    }*/

    private int getRandomColor() {
        Random rand = new Random();
        return Color.argb(100, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }

    public String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void ShowSnackBar(String snackBarText) {
        Snackbar.make(hideLayout, snackBarText, Snackbar.LENGTH_LONG).show();
    }
}