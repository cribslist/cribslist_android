package com.codepath.cribslist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.cribslist.R;
import com.codepath.cribslist.adapters.ListingFragmentPagerAdapter;
import com.codepath.cribslist.animations.VerticalFlipTransformation;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fabric.with(this, new Crashlytics());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupViewPager();
        setupFAB();
    }

    public void setupViewPager(){
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new ListingFragmentPagerAdapter(getSupportFragmentManager(),
                this));
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setPageTransformer(true, new VerticalFlipTransformation());

    }

    public void setupFAB(){
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               launchPostActivity();
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

    private void launchAccountActivity() {
        Intent i = new Intent(this, AccountActivity.class);
        startActivity(i);
    }

    private void launchPostActivity() {
        Intent i = new Intent(this, PostActivity.class);
        startActivity(i);
    }
}
