package com.codepath.cribslist.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.cribslist.R;
import com.codepath.cribslist.adapters.ItemAdapter;
import com.codepath.cribslist.constants.API_PARAM;
import com.codepath.cribslist.constants.API_ROUTE;
import com.codepath.cribslist.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.cribslist.models.Item;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class Listings extends Fragment {
    int numberOfColumns = 2;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    GridLayoutManager layoutManager;
    ArrayList<Item> items = new ArrayList<>();
    private static final String LISTING_TYPE = "LISTING_TYPE";
    private int listingType;
    private EndlessRecyclerViewScrollListener scrollListener;
    private String searchQuery = "";
    private int currentPage = 0;
    private String fragmentRoute;

    public Listings() {
        // Required empty public constructor
    }

    public static Listings newInstance(int i) {
        Bundle args = new Bundle();
        args.putInt(LISTING_TYPE, i);
        Listings fragment = new Listings();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            listingType = getArguments().getInt(LISTING_TYPE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_listings, container, false);
        mRecyclerView = v.findViewById(R.id.items);

        layoutManager = new GridLayoutManager(getContext(), numberOfColumns);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ItemAdapter(items);
        mRecyclerView.setAdapter(mAdapter);
        fragmentRoute = getRouteForType(listingType);
        search();
        return v;
    }

    public void addItems(JSONArray response){

        items.addAll(Item.fromJSONArray(response));
        mAdapter.notifyDataSetChanged();
    }

    public void search(){
        loadNextDataFromApi(fragmentRoute, currentPage);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                currentPage = currentPage + 1;
                loadNextDataFromApi(fragmentRoute, currentPage);
            }
        };
        mRecyclerView.addOnScrollListener(scrollListener);
    }



    public String getRouteForType(int type){
        switch(type){
            case 0:
                return API_ROUTE.ITEMS;
            case 1:
                //TODO: grab id from user
                return String.format(API_ROUTE.MY_ITEMS, 123);
            default:
                return API_ROUTE.ITEMS;
        }
    }

    public void loadNextDataFromApi(String route, int page){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = getRequestParams(currentPage);
        client.get(route, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                addItems(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getContext(), getResources().getText(R.string.api_error), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setQueryAndSearch(String query){
        searchQuery = query;
        items.clear();
        currentPage = 0;
        search();
    }

    public RequestParams getRequestParams(int page){
        RequestParams params = new RequestParams();
        params.put(API_PARAM.COUNT, 2);
        params.put(API_PARAM.PAGE, page);
        if(listingType == 0 && !"".equals(searchQuery)){
            params.put(API_PARAM.QUERY, searchQuery);
        }
        return params;
    }

}
