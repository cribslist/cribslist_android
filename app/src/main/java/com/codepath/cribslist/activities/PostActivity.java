package com.codepath.cribslist.activities;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;
import com.codepath.cribslist.R;
import com.codepath.cribslist.helper.DispatchGroup;
import com.codepath.cribslist.models.Item;
import com.codepath.cribslist.network.CribslistClient;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.DefaultSliderView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PostActivity extends AppCompatActivity implements ActionSheet.ActionSheetListener {
    private static final String TITLE_TEXT = "New Item";
    private static final String CHOOSE_FROM_LIBRARY = "CHOOSE FROM LIBRARY";
    private static final String TAKE_A_PHOTO = "TAKE A PHOTO";
    private static final String CANCEL = "CANCEL";

    private final String APP_TAG = "MyCustomApp";
    private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final static int PICK_PHOTO_CODE = 1046;
    private String photoFileName = "photo.jpg";
    private File photoFile;

    private ArrayList<File> mImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(TITLE_TEXT);

        mImages = new ArrayList<>();

        SliderLayout slider = findViewById(R.id.slider);
        slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
    }

    // Trigger gallery selection for a photo
    private void onPickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PHOTO_CODE);
    }

    private void onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(PostActivity.this, "com.codepath.fileprovider", photoFile);
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

    private void addBitmapToSlider(Bitmap bitmap) {
        SliderLayout slider = findViewById(R.id.slider);
        DefaultSliderView sliderView = new DefaultSliderView(this);

        long unixTime = System.currentTimeMillis();
        String filePath = getFilesDir().getPath() + "/" + unixTime + ".txt";
        File file = new File(filePath);
        OutputStream os;

        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sliderView
                .image(file)
                .setBackgroundColor(Color.WHITE)
                .setProgressBarVisible(true);

        slider.addSlider(sliderView);

        mImages.add(file);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                addBitmapToSlider(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PICK_PHOTO_CODE) {
            if (data != null && data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                for (int i = 0; i < mClipData.getItemCount(); i++) {
                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        addBitmapToSlider(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
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

    public void onClickAdd(View view) {
        ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle(CANCEL)
                .setOtherButtonTitles(TAKE_A_PHOTO, CHOOSE_FROM_LIBRARY)
                .setCancelableOnTouchOutside(true)
                .setListener(this)
                .show();
    }

    public void onClickPostBtn(View view) {
        postImages();
    }

    // MARK: Service call

    private void postImages() {
        final DispatchGroup group = new DispatchGroup();
        final ArrayList<String> paths = new ArrayList<>();

        for (File file: mImages) {
            group.enter();
            CribslistClient.postImage(file, new CribslistClient.PostImageDelegate() {
                @Override
                public void handlePostImage(String path) {
                    Log.d("DEBUG_", path);
                    paths.add(path);
                    group.leave();
                }
            });
        }

        group.notify(new Runnable() {
            @Override
            public void run() {
                postItem(paths);
            }
        });
    }

    private void postItem(ArrayList<String> paths) {
        EditText etTitle = findViewById(R.id.etTitle);
        EditText etPrice = findViewById(R.id.etPrice);
        EditText etLocation = findViewById(R.id.etLocation);
        EditText etDescription = findViewById(R.id.etDescription);

        String title = etTitle.getText().toString();
        int price = 0;
        try {
            price = Integer.parseInt(etPrice.getText().toString());
        } catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }
        String description = etDescription.getText().toString();
        String location = etLocation.getText().toString();

        Date now = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String nowString = simpleDateFormat.format(now);

        final Item item = new Item(title, price, description,
                null/*Long.parseLong("1540263890986")*/, location, 0, 0,
                nowString, null, null, paths);

        CribslistClient.postItem(item, new CribslistClient.PostItemDelegate() {
            @Override
            public void handlePostItem() {
                Log.d("DEBUG_", item.getTitle() + "posted");
                finish();
            }
        });
    }
}
