package com.codepath.cribslist.network;

import android.util.Log;

import com.codepath.cribslist.helper.SharedPref;
import com.codepath.cribslist.models.Comment;
import com.codepath.cribslist.models.Item;
import com.codepath.cribslist.models.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class CribslistClient {
    public interface GetAccountDelegate {
        void handleGetAccount(User user);
    }

    public interface PostImageDelegate {
        void handlePostImage(String path);
    }

    public interface PostItemDelegate {
        void handlePostItem();
    }

    public interface GetComments {
        void handleGetComments(ArrayList<Comment> comments);
    }

    public static final String BASE_URL = "https://cribslist.herokuapp.com/";

    public static void getAccountDetail(final GetAccountDelegate delegate) {
        String apiUrl = BASE_URL + "account/" + SharedPref.getInstance().getUserId();

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

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    User user = new User(response.getJSONObject(0));
                    delegate.handleGetAccount(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    public static void postImage(File image, final PostImageDelegate delegate){
        if (image == null) {
            return;
        }

        String apiUrl = BASE_URL + "image_upload";

        RequestParams params = new RequestParams();
        params.setForceMultipartEntityContentType(true);

        try {
            params.put("file", image, "image/jpg");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(apiUrl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String path = response.getString("path");
                    delegate.handlePostImage(path);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throw new AssertionError(responseString);
            }
        });
    }

    public static void postItem(Item item, final PostItemDelegate delegate) {
        if (item == null) {
            return;
        }

        String apiUrl = BASE_URL + "items";

        RequestParams params = new RequestParams();
        params.put("title", item.getTitle());
        params.put("price", item.getPrice());
        params.put("description", item.getDescription());
        params.put("seller", item.getSellerID());
        params.put("location", item.getLocation());
        params.put("latitude", item.getLatitude());
        params.put("longitude", item.getLongitude());
        params.put("created", item.getCreated());
        params.put("category", item.getCategory().toString());
        ArrayList<String> urls = item.getPhotoURLs();
        if (urls.size() > 0) {
            String urlString = "[";
            for (String url: urls) {
                String current = "\"" + url + "\"" + ",";
                urlString = urlString + current;
            }
            urlString = removeLastComma(urlString);
            urlString = urlString + "]";
            params.put("photo_urls", urlString);
        }

        params.put("thumbnail_url", item.getThumbnailURL());
        params.setForceMultipartEntityContentType(true);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(apiUrl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                delegate.handlePostItem();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throw new AssertionError(responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    public static void getCommentsForId(String threadId, final GetComments getComments){
        ArrayList<Comment> comments = new ArrayList<>();
        AsyncHttpClient client = new AsyncHttpClient();
        String url = BASE_URL + "/comments/" + threadId;
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                getComments.handleGetComments(Comment.fromJSONArray(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throw new AssertionError(responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private static String removeLastComma(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == ',') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }
}
