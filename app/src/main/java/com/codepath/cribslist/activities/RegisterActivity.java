package com.codepath.cribslist.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.actionsheet.ActionSheet;
import com.codepath.cribslist.R;
import com.codepath.cribslist.helper.SharedPref;
import com.codepath.cribslist.models.User;
import com.codepath.cribslist.network.CribslistClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity implements ActionSheet.ActionSheetListener {

    private User mUser;

    private static final String CHOOSE_FROM_LIBRARY = "CHOOSE FROM LIBRARY";
    private static final String TAKE_A_PHOTO = "TAKE A PHOTO";
    private static final String CANCEL = "CANCEL";
    private final String APP_TAG = "MyCustomApp";
    private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final static int PICK_PHOTO_CODE = 1046;
    private String photoFileName = "photo.jpg";
    private File photoFile;
    private final static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setBackgroundDrawableResource(R.drawable.bg_10);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mUser = (User) getIntent().getSerializableExtra("user");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        if (index == 0) {
            onLaunchCamera();
        } else {
            onPickPhoto();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (requestCode == PICK_PHOTO_CODE) {
        if (data != null) {
            Uri photoUri = data.getData();
            // Do something with the photo based on Uri
            Bitmap selectedImage = null;
            File file = null;
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                String uuid = UUID.randomUUID().toString();
                String filePath = getFilesDir().getPath() + "/" + uuid + ".jpg";
                file = new File(filePath);
                OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            photoFile = file;
            // Load the selected image into a preview
            ImageView ivPreview = findViewById(R.id.imageView);
            ivPreview.setImageBitmap(selectedImage);
        }
    } else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
        Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
        // RESIZE BITMAP, see section below
        // Load the taken image into a preview
        ImageView ivPreview = findViewById(R.id.imageView);
        ivPreview.setImageBitmap(takenImage);
    } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
        if (resultCode == RESULT_OK) {
            Place place = PlaceAutocomplete.getPlace(this, data);
            String name = place.getName().toString();
            TextView tvLocation = findViewById(R.id.tvLocation);
            tvLocation.setText(name);
            mUser.setLocation(name);
        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(this, data);
            // TODO: Handle the error.
            Log.i("DEBUG", status.getStatusMessage());
        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.
        }
    }
}

    private void onPickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    private void onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(RegisterActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    private File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    public void onClickAdd(View view) {
        ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle(CANCEL)
                .setOtherButtonTitles(TAKE_A_PHOTO, CHOOSE_FROM_LIBRARY)
                .setCancelableOnTouchOutside(true)
                .setListener(this)
                .show();
    }


    public void onClickRemove(View view) {
        ImageView iv = findViewById(R.id.imageView);
        iv.setImageDrawable(getResources().getDrawable(R.drawable.add_profile));
    }

    public void onClickLocation(View view) {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    public void onClickPostBtn(View view) {
        postImage();
    }

    private void postImage() {
        progress = new ProgressDialog(this);
        progress.setTitle("Loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        CribslistClient.postImage(photoFile, new CribslistClient.PostImageDelegate() {
            @Override
            public void handlePostImage(String path) {
                mUser.setUserPhotoURL(path);
                postUser();
            }
        });
    }

    private void postUser() {
        EditText et = findViewById(R.id.etName);
        mUser.setName(et.getText().toString());

        CribslistClient.addUser(mUser, new CribslistClient.GetUser() {
            @Override
            public void handleGetUser(User user) {
                progress.dismiss();

                SharedPref.getInstance().setEmail(user.getEmail());
                SharedPref.getInstance().setUserId(String.valueOf(user.getUid()));
                launchMainActivity();
            }
        });
    }

    private void launchMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
