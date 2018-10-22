package com.codepath.cribslist.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.codepath.cribslist.R;
import com.codepath.cribslist.constants.API_ROUTE;
import com.codepath.cribslist.models.Item;
import com.glide.slider.library.Animations.DescriptionAnimation;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.DefaultSliderView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ItemDetail extends AppCompatActivity {
    private SliderLayout mSlider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Intent i = getIntent();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if(ab != null){
            ab.setDisplayShowHomeEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle("");
        }
        Item item = i.getParcelableExtra("item");
        loadItemDetail(item.getUid());
        mSlider = findViewById(R.id.slider);
        setViewText(R.id.location_text, item.getLocation());
        setViewText(R.id.title, item.getTitle());
        setViewText(R.id.description, item.getDescription());
        ArrayList<String> urls = item.getPhotoURLs();
        initSlider(urls);
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

    public void loadItemDetail(long id){
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(String.format(API_ROUTE.ITEM, id), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getApplicationContext(), getResources().getText(R.string.api_error), Toast.LENGTH_LONG).show();
            }
        });
    }

}
