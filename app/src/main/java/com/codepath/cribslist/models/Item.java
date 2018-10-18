package com.codepath.cribslist.models;

import com.codepath.cribslist.constants.ITEM_FIELD;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Item {
    private long uid;
    private String title;
    private long price;
    private String description;
    private long sellerID;
    private String location;
    private double latitude;
    private double longitude;
    private String created;
    private ArrayList<String> photoURLs;
    private String category;
    private String thumbnailURL;

    public long getUid() {
        return uid;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getSellerID() {
        return sellerID;
    }

    public String getLocation() {
        return location;
    }

    public String getCreated() {
        return created;
    }

    public String getCategory() {
        return category;
    }

    public ArrayList<String> getPhotoURLs() {
        return photoURLs;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public Item(JSONObject jsonObject) throws JSONException{
        this.uid = jsonObject.getLong(ITEM_FIELD.ID);
        this.title = jsonObject.getString(ITEM_FIELD.TITLE);
        this.description = jsonObject.getString(ITEM_FIELD.DESCRIPTION);
        this.sellerID = jsonObject.getLong(ITEM_FIELD.SELLER);
        this.location = jsonObject.getString(ITEM_FIELD.LOCATION);
        this.created = jsonObject.getString(ITEM_FIELD.CREATED);
        this.category = jsonObject.getString(ITEM_FIELD.CATEGORY);
        JSONArray urls = jsonObject.getJSONArray(ITEM_FIELD.PHOTO_URLS);
        photoURLs = new ArrayList<>();
        for (int i = 0; i < urls.length(); i++){
            photoURLs.add(urls.getString(i));
        }
        thumbnailURL = jsonObject.getString(ITEM_FIELD.THUMBNAIL);

    }

    public static ArrayList<Item> fromJSONArray(JSONArray array) {
        ArrayList<Item> results = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++){
                JSONObject obj = array.getJSONObject(i);
                Item itm = new Item(obj);
                results.add(itm);
            }
            return results;
        } catch (JSONException e){
            e.printStackTrace();
        }
        return results;
    }
}
