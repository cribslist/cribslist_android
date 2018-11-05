package com.codepath.cribslist.network;

import android.util.Log;

import com.codepath.cribslist.helper.SharedPref;
import com.codepath.cribslist.models.Comment;
import com.codepath.cribslist.models.Item;
import com.codepath.cribslist.models.User;
import com.codepath.cribslist.models.UserGenerator;
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

    public interface GetUser {
        void handleGetUser(User user);
    }

    public interface DeleteItem{
        void handleDeleteItem();
    }

    public static final String BASE_URL = "http://cribslist.herokuapp.com/";

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
        params.put("seller", SharedPref.getInstance().getUserId());
        params.put("location", item.getLocation());
//        params.put("latitude", item.getLatitude());
//        params.put("longitude", item.getLongitude());
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
        String url = BASE_URL + "comments/" + threadId;
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

    public static void addComment(Comment c, String threadId){
        AsyncHttpClient client = new AsyncHttpClient();
        String url = BASE_URL + "comments/" + threadId;
        RequestParams params = new RequestParams();
        params.put("user_id", c.getUser_id());
        params.put("username", c.getUsername());
        params.put("text", c.getText());
        params.put("thread_id", threadId);
        params.setForceMultipartEntityContentType(true);
        client.post(url, params, new JsonHttpResponseHandler() {
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

    public static void getUserForId(final long uid, final GetUser getUserCb){
        AsyncHttpClient client = new AsyncHttpClient();
        String url = BASE_URL + "accounts/" + String.valueOf(uid);
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    User user = new User(response);
                    getUserCb.handleGetUser(user);
                } catch(JSONException e){
                    e.printStackTrace();
                    User user = UserGenerator.getRandomUser(uid);
                    getUserCb.handleGetUser(user);
                }

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
                // if no user is found then we create a fake one
                User user = UserGenerator.getRandomUser(uid);
                // this adds the fake user to the API so user wont keep changing per item
                addUser(user, getUserCb);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                User user = UserGenerator.getRandomUser(uid);
                getUserCb.handleGetUser(user);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                User user = UserGenerator.getRandomUser(uid);
                getUserCb.handleGetUser(user);
            }
        });
    }

    public static void addUser(User user){
        addUser(user, null);
    }

    public static void addUser(final User user, final GetUser getUser){
        AsyncHttpClient client = new AsyncHttpClient();
        String url = BASE_URL + "account";
        RequestParams params = new RequestParams();
        params.put("id", user.getUid());
        params.put("name", user.getName());
        params.put("email", user.getEmail());
        params.put("location", user.getLocation());
        params.put("user_photo_url", user.getUserPhotoURL());
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                    if(response != null){
                        getUser.handleGetUser(user);
                    }
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

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {

            }
        });
    }

    public static void deleteItem(long itemId, final DeleteItem deleteItem){
        AsyncHttpClient client = new AsyncHttpClient();
        String url = BASE_URL + "item/" + String.valueOf(itemId);
        client.delete(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                deleteItem.handleDeleteItem();
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


}
