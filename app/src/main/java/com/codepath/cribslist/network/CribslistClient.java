package com.codepath.cribslist.network;

import android.util.Log;

import com.codepath.cribslist.models.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;

public class CribslistClient {
    public interface GetAccountDelegate {
        void handleGetAccount(User user);
    }

    public interface PostImageDelegate {
        void handlePostImage(String id, String path);
    }

    public static final String BASE_URL = "https://cribslist.herokuapp.com/";

    public static void getAccountDetail(final GetAccountDelegate delegate) {
        String apiUrl = BASE_URL + "account";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(apiUrl, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                try {
                    User user = new User(response);
                    delegate.handleGetAccount(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void postImage(File image, final PostImageDelegate delegate){
        if (image == null) {
            return;
        }

        String apiUrl = BASE_URL + "image_upload";

        RequestParams params = new RequestParams();
        try {
            params.put("file", image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(apiUrl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String id = response.getString("_id");
                    String path = response.getString("path");
                    String url = BASE_URL + path;

                    delegate.handlePostImage(id, url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }
        });
    }

}
