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
<<<<<<< HEAD
    private String category;
    private ArrayList<String> photoURLs;

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

    private String thumbnailURL;
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
=======
    private ArrayList<Integer> category;
    private ArrayList<String> photoURLs;
    private String thumbnailURL;

    public static Item fromJSON(JSONObject jsonObject) throws JSONException {
        Item item = new Item();
        item.uid = jsonObject.getLong("id");
        item.title = jsonObject.getString("title");
        item.description= jsonObject.getString("description");
        item.price= jsonObject.getLong("price");
        item.sellerID = jsonObject.getLong("seller");
        item.created = jsonObject.getString("created");
        item.thumbnailURL = jsonObject.getString("thumbnail_url");

        JSONArray jsonCategory = jsonObject.getJSONArray("category");
        ArrayList<Integer> category = new ArrayList<>();
        for (int i = 0; i < jsonCategory.length(); i++) {
            category.add((Integer) jsonCategory.get(i));
        }
        item.category = category;

        JSONArray jsonPhotoURLs = jsonObject.getJSONArray("photo_urls");
        ArrayList<String> photoURLs = new ArrayList<>();
        for (int i = 0; i < jsonPhotoURLs.length(); i++) {
            photoURLs.add((String) jsonPhotoURLs.get(i));
        }
        item.photoURLs = photoURLs;
        return item;
    }
}
>>>>>>> Update models with parsing logic. Add UserActivity to load user account.
