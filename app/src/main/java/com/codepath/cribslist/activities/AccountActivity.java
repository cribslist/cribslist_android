package com.codepath.cribslist.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.codepath.cribslist.R;
import com.codepath.cribslist.models.User;
import com.codepath.cribslist.network.CribslistClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class AccountActivity extends AppCompatActivity {
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        CribslistClient.getAccountDetail(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                try {
                    user = new User(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("DEBUG", user.toString());

            }
        });
    }
}
