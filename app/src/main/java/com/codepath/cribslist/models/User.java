package com.codepath.cribslist.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class User {
    private long uid;
    private String name;
    private String email;
    private String location;
    private String description;
    private double rating;
    private ArrayList<Item> items;
    private String userPhotoURL;

    public long getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public double getRating() {
        return rating;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public String getUserPhotoURL() {
        return userPhotoURL;
    }

    public User(JSONObject jsonObject) throws JSONException {
        this.uid = jsonObject.getLong("id");
        this.name = jsonObject.getString("name");
        this.email = jsonObject.getString("email");
        this.location = jsonObject.getString("location");
        this.description= jsonObject.getString("description");
        this.rating = jsonObject.getDouble("rating");
        this.userPhotoURL = jsonObject.getString("user_photo_url");
        JSONArray jsonArray = jsonObject.getJSONArray("items");
        ArrayList<Item> items = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = (JSONObject) jsonArray.get(i);
            Item item = new Item(object);
            items.add(item);
        }

        this.items = items;
    }
}
