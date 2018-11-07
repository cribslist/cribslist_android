package com.codepath.cribslist.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.cribslist.R;
import com.codepath.cribslist.models.Item;
import com.codepath.cribslist.models.User;
import com.codepath.cribslist.network.CribslistClient;
import com.glide.slider.library.Animations.DescriptionAnimation;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.DefaultSliderView;

import java.util.ArrayList;

import jp.wasabeef.blurry.Blurry;

public class ItemDetail extends AppCompatActivity implements CribslistClient.GetUser, CribslistClient.DeleteItem {
    private SliderLayout mSlider;
    ImageView bgImage;
    private DrawerLayout mDrawer;
    private Button inquire;
    private ImageView blurry;
    private View base;
    private Item item;
    private ImageView profpic;
    private User user;
    private Boolean isOwnListing;
    private NavigationView navMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        inquire = findViewById(R.id.inquire);
        blurry = findViewById(R.id.blurry);
        base = findViewById(R.id.base);
        Intent i = getIntent();
        profpic = findViewById(R.id.prof);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mDrawer = findViewById(R.id.drawer_layout);
        navMenu = findViewById(R.id.nvView);
        mDrawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {

            }

            @Override
            public void onDrawerClosed(@NonNull View view) {
                closeDrawer();
            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });
        mDrawer.setScrimColor(getResources().getColor(android.R.color.transparent));

        setSupportActionBar(toolbar);
        initToolBar();

        ChangeBounds bounds = new ChangeBounds();
        bounds.setDuration(200);
        bounds.setInterpolator(new AccelerateDecelerateInterpolator());
        getWindow().setSharedElementEnterTransition(bounds);
        item = i.getParcelableExtra("item");
        isOwnListing = i.getBooleanExtra("isOwnListing", false);
        if(isOwnListing){
          initOwnListing();
        } else {
          initListing();
        }
        inquire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer();
            }
        });
        mSlider = findViewById(R.id.slider);
        setViewText(R.id.location_text, item.getLocation());
        setViewText(R.id.title, item.getTitle());
        setViewText(R.id.description, item.getDescription());
        ArrayList<String> urls = item.getPhotoURLs();
        initSlider(urls);
        bgImage = findViewById(R.id.bgImg);
        Bitmap bg = getBgBitmap(getResources().getDrawable(R.drawable.bg));
        Blurry.with(ItemDetail.this).radius(50).animate(1000).from(bg).into(bgImage);

    }

    private void initToolBar(){
        ActionBar ab = getSupportActionBar();
        if(ab != null){
            ab.setDisplayShowHomeEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle("");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade();
            fade.excludeTarget(R.id.toolbar, true);
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            fade.excludeTarget(android.R.id.navigationBarBackground, true);

            getWindow().setEnterTransition(fade);
            getWindow().setExitTransition(fade);
        }
    }

    private void initOwnListing(){
        inquire.setText(R.string.options);
        navMenu.getMenu().clear();
        navMenu.inflateMenu(R.menu.own_detail_menu);
    }

    private void initListing(){
        loadItemAuthorDetail();
    }



    private void openDrawer(){
        blurry.setVisibility(View.VISIBLE);
        Blurry.with(ItemDetail.this)
                .radius(30)
                .capture(mDrawer)
                .into(blurry);
        mSlider.stopAutoCycle();
        mDrawer.openDrawer(GravityCompat.END);
    }

    private void closeDrawer(){
        blurry.setVisibility(View.GONE);
        mSlider.startAutoCycle();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void setViewText(int id, String text){
        TextView textView = findViewById(id);
        textView.setText(text);
    }

    public void initSlider(ArrayList<String> listUrl){
        RequestOptions requestOptions = new RequestOptions().fitCenter();

        for (int i = 0; i < listUrl.size(); i++) {
            DefaultSliderView sliderView = new DefaultSliderView(this);
            sliderView
                    .image(listUrl.get(i))
                    .setRequestOption(requestOptions)
                    .setBackgroundColor(Color.WHITE)
                    .setProgressBarVisible(true);

            mSlider.addSlider(sliderView);
        }

        // set Slider Transition Animation
        mSlider.setPresetTransformer(SliderLayout.Transformer.Tablet);

        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSlider.setCustomAnimation(new DescriptionAnimation());
        mSlider.setDuration(4000);

    }

    public Bitmap getBgBitmap(Drawable drawable) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        Bitmap mutableBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);

        return mutableBitmap;
    }

    public void loadItemAuthorDetail(){
        CribslistClient.getUserForId(item.getSellerID(), this);
    }
    @Override
    public void handleGetUser(User user){
        this.user = user;
        String url = user.getUserPhotoURL();
        if(url != null){
            Glide.with(ItemDetail.this).load(url).apply(RequestOptions.circleCropTransform()).into(profpic);
        }

    }

    public void showComments(MenuItem menuItem) {
        Intent intent = new Intent(ItemDetail.this, Comments.class);
        intent.putExtra("thread_id", String.valueOf(item.getId()));
        intent.putExtra("title", String.valueOf(item.getTitle()));
        startActivity(intent);
    }

    public void writeEmail(MenuItem menuItem) {
        String listingOwnerEmail = user.getEmail();
        if(listingOwnerEmail == null){
            listingOwnerEmail = "cribslist_anonymous5435@gmail.com";
        }
        String title = item.getTitle();
        String mailto = "mailto:" + listingOwnerEmail +
                "?subject=" + Uri.encode("Re:"+ title) +
                "&body=" + Uri.encode("Hello " + user.getName() + ",\n" +
                "I am interested in your listing \""+ title + "\" and would like to see it...");

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse(mailto));

        try {
            startActivity(emailIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(ItemDetail.this, "Oops! No email app found", Toast.LENGTH_LONG).show();
        }
    }

    public void flagItem(MenuItem item) {
        mDrawer.closeDrawers();
        Toast.makeText(ItemDetail.this, "Content Flagged and sent for review", Toast.LENGTH_LONG).show();
    }

    public void deleteListing(MenuItem menuItem) {
        CribslistClient.deleteItem(item.getId(), this);
    }

    public void handleDeleteItem(){
        Toast.makeText(ItemDetail.this, "Deteled listing", Toast.LENGTH_LONG).show();
        onBackPressed();
    }

    public void onClickImageView(View view) {
        Intent i = new Intent(this, AccountActivity.class);
        i.putExtra("userId", user.getUid());
        startActivity(i);
    }
}
