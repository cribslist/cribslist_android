package com.codepath.cribslist.activities;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;
import com.codepath.cribslist.R;
import com.codepath.cribslist.constants.ItemCategory;
import com.codepath.cribslist.helper.DispatchGroup;
import com.codepath.cribslist.models.Item;
import com.codepath.cribslist.models.LatLng;
import com.codepath.cribslist.network.CribslistClient;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.DefaultSliderView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.thomashaertel.widget.MultiSpinner;

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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

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
    private final static int REQUEST_FINE_LOCATION = 10;

    SliderLayout mSlider;
    EditText etTitle;
    EditText etPrice;
    TextView tvLocation;
    EditText etDescription;
    MultiSpinner mSpinner;

    private ArrayList<File> mImages;
    private ArrayList<Integer> mCategory;
    private LatLng mLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        setupViews();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(TITLE_TEXT);

        mImages = new ArrayList<>();
        mCategory = new ArrayList<>();
        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);

        mLatLng = new LatLng(37.7749,-122.4194);

        setupSpinner();
        getUserLocation();
    }

    private void setupViews() {
        etTitle = findViewById(R.id.etTitle);
        etPrice = findViewById(R.id.etPrice);
        tvLocation = findViewById(R.id.tvLocation);
        etDescription = findViewById(R.id.etDescription);
        mSlider = findViewById(R.id.slider);
        mSpinner = findViewById(R.id.spinnerMulti);
    }

    private void setupSpinner() {
        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        mAdapter.addAll(ItemCategory.getArray());

        mSpinner.setAdapter(mAdapter, false, new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(boolean[] selected) {
                ArrayList<Integer> category = mCategory;
                for (int i = 0; i < selected.length; i++) {
                    boolean isSelected = selected[i];
                    if (isSelected == true) {
                        category.add(i);
                    } else {
                        category.remove(Integer.valueOf(i));
                    }
                }

                Set<Integer> hs = new HashSet<>();
                hs.addAll(category);
                category.clear();
                category.addAll(hs);

                mCategory = category;
            }
        });

        boolean[] selectedItems = new boolean[mAdapter.getCount()];
        mSpinner.setSelected(selectedItems);
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

    private void removeAllImagesFromSlider() {
        mSlider.removeAllSliders();
        mImages.clear();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                mImages.add(photoFile);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PICK_PHOTO_CODE &&
                data != null &&
                data.getClipData() != null) {
            ClipData mClipData = data.getClipData();
            for (int i = 0; i < mClipData.getItemCount(); i++) {
                ClipData.Item item = mClipData.getItemAt(i);
                Uri uri = item.getUri();
                File file = null;
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    String uuid = UUID.randomUUID().toString();
                    String filePath = getFilesDir().getPath() + "/" + uuid + ".jpg";
                    file = new File(filePath);
                    OutputStream os = new BufferedOutputStream(new FileOutputStream(file));;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mImages.add(file);
            }
        }

        mSlider.removeAllSliders();

        for (File image: mImages) {
            DefaultSliderView sliderView = new DefaultSliderView(this);
            sliderView
                    .image(image)
                    .setBackgroundColor(Color.WHITE)
                    .setProgressBarVisible(true);
            mSlider.addSlider(sliderView);
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

    public void onClickRemove(View view) {
        removeAllImagesFromSlider();
    }

    // MARK: Location

    private void getUserLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(this);
            locationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            getAddress(location);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("MapDemoActivity", "Error trying to get last GPS location");
                            e.printStackTrace();
                        }
                    });
            return;
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_CONTACTS)) {
            notifyNoAccess();
            return;
        }

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_FINE_LOCATION);

    }

    private void getAddress(Location location) {
        if (location == null) {
            tvLocation.setText("San Francisco");
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);
            Address obj = addresses.get(0);
            tvLocation.setText(obj.getLocality());
            mLatLng.lat = location.getLatitude();
            mLatLng.lon = location.getLongitude();
//            itemForSale.setLocationFull(obj);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUserLocation();

                } else {
                    notifyNoAccess();
                }
            }
        }
    }

    public void notifyNoAccess(){
        Toast.makeText(this,
                R.string.permission_message,
                Toast.LENGTH_SHORT).show();
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
        String title = etTitle.getText().toString();
        int price = 0;
        try {
            price = Integer.parseInt(etPrice.getText().toString());
        } catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }
        String description = etDescription.getText().toString();
        String location = tvLocation.getText().toString();

        Date now = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String nowString = simpleDateFormat.format(now);

        final Item item = new Item(title, price, description,
                null, location, mLatLng.lat, mLatLng.lon,
                nowString, mCategory, null, paths);

        CribslistClient.postItem(item, new CribslistClient.PostItemDelegate() {
            @Override
            public void handlePostItem() {
                Log.d("DEBUG", "successfully posted");
                finish();
            }
        });
    }
}
