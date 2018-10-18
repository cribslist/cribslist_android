package com.codepath.cribslist.network;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class CribslistClient {
    public static final String BASE_URL = "https://private-bf468f-cribslist.apiary-mock.com/";

    public static void getAccountDetail(JsonHttpResponseHandler handler) {
        String apiUrl = BASE_URL + "account";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(apiUrl, null, handler);
    }
}
