package com.codepath.cribslist.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.cribslist.R;
import com.codepath.cribslist.adapters.ListingFragmentPagerAdapter;
import com.codepath.cribslist.animations.VerticalFlipTransformation;
import com.codepath.cribslist.fragments.Listings;
import com.codepath.cribslist.models.Item;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import jp.wasabeef.blurry.Blurry;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private EditText search;
    private FloatingActionButton fab;
    private int currentPageIndex = 0;
    private View baseView;
    private ImageView blurryView;
    private View searchBarView;
    ListingFragmentPagerAdapter adapter;
    Listings listingFragment;
    private static final String LISTINGS_FRAGMENT = "LISTINGS_FRAGMENT";
    private static final int POST_ACTIVITY_REQUEST_CODE = 8000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        baseView = findViewById(R.id.base);
        blurryView = findViewById(R.id.blurry);
        searchBarView = findViewById(R.id.search_bar_view);
        search = findViewById(R.id.search_input);
        search.setImeOptions(EditorInfo.IME_ACTION_DONE);
        search.setImeActionLabel("Search", KeyEvent.KEYCODE_ENTER);
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    searchListings();
                    v.clearFocus();
                    return true;
                }
                return false;
            }
        });
        Fabric.with(this, new Crashlytics());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        setupViewPager();
        setupFAB();
    }

    public void setupViewPager(){
        viewPager = findViewById(R.id.viewpager);
        adapter = new ListingFragmentPagerAdapter(getSupportFragmentManager(),
                this);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setPageTransformer(true, new VerticalFlipTransformation());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                updateFAB(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        listingFragment = (Listings) adapter.getRegisteredFragment(0);
    }

    public void searchListings(){
        listingFragment = (Listings) adapter.getRegisteredFragment(0);
        if(listingFragment!= null && search != null){
            String query = search.getText().toString();
            listingFragment.setQueryAndSearch(query);
            hideSearchBar();
        }
    }

    public void updateFAB(int i){
        currentPageIndex = i;
        updateFABIcon();
    }

    public void updateFABIcon(){
        if(fab != null){
            switch (currentPageIndex) {
                case 0:
                    fab.setImageResource(R.drawable.ic_search_white_24dp);
                    return;
                case 1:
                    fab.setImageResource(R.drawable.ic_add_white_24dp);
                    return;
            }
        }
    }

    public void setupFAB(){
        fab = findViewById(R.id.fab);
        updateFABIcon();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (currentPageIndex){
                    case 0:
                        showSearchBar();
                        return;
                    case 1:
                        launchPostActivity();
                        return;
                }

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            launchAccountActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == POST_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Item item = data.getParcelableExtra("item");
            Listings myItems = (Listings) adapter.getRegisteredFragment(1);
            myItems.addItem(item);
        }
    }

    private void launchAccountActivity() {
        Intent i = new Intent(this, AccountActivity.class);
        startActivity(i);
    }

    private void launchPostActivity() {
        Intent i = new Intent(this, PostActivity.class);
        startActivityForResult(i,POST_ACTIVITY_REQUEST_CODE);
    }

    public void hideSearchBar(View v){
        hideSearchBar();
    }

    public void handleDismissSearch(View v){
        search.setText("");
        hideSearchBar();
    }


    private void hideSearchBar(){
        blurryView.setAnimation(null);
        searchBarView.setVisibility(View.INVISIBLE);
        AlphaAnimation animation1 = new AlphaAnimation(1.0f, 0.2f);
        animation1.setDuration(100);
        blurryView.startAnimation(animation1);
        blurryView.setVisibility(View.INVISIBLE);
        hideSoftKeyboard(search);
    }

    private void showSearchBar(){
        blurryView.setVisibility(View.VISIBLE);
        AlphaAnimation animation1 = new AlphaAnimation(0.0f, 1.0f);
        animation1.setDuration(600);
        blurryView.setAnimation(animation1);
        searchBarView.setVisibility(View.VISIBLE);
        showSoftKeyboard(search);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        Blurry.with(MainActivity.this)
                .radius(30)
                .color(Color.argb(35, 255, 255, 255))
                .capture(baseView)
                .into(blurryView);


    }
    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        }

    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }

}
