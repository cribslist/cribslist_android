package com.codepath.cribslist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.cribslist.R;
import com.codepath.cribslist.adapters.ItemAdapter;
import com.codepath.cribslist.constants.API_PARAM;
import com.codepath.cribslist.constants.API_ROUTE;
import com.codepath.cribslist.models.Item;
import com.crashlytics.android.Crashlytics;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
    int numberOfColumns = 2;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    ArrayList<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fabric.with(this, new Crashlytics());
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ItemListing.class);
                startActivity(i);
            }
        });

        mRecyclerView = findViewById(R.id.items);

        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns);
        mRecyclerView.setLayoutManager(layoutManager);
        items = new ArrayList<>();
        mAdapter = new ItemAdapter(items);
        mRecyclerView.setAdapter(mAdapter);
        loadNextDataFromApi(-1);

//        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
//            @Override
//            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                loadNextDataFromApi(page);
//            }
//        };

//        mRecyclerView.addOnScrollListener(scrollListener);
    }

    public void loadNextDataFromApi(int page){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = getRequestParams(page);
        client.get(API_ROUTE.ITEMS, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                items.addAll(Item.fromJSONArray(response));
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getApplicationContext(), getResources().getText(R.string.api_error), Toast.LENGTH_LONG).show();
            }
        });
    }

    public RequestParams getRequestParams(int page){
        RequestParams params = new RequestParams();
        // todo add this functionality later
        if(page == -1){
            return params;
        }
        params.put(API_PARAM.COUNT, 10);
        params.put(API_PARAM.PAGE, page);
        return params;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
