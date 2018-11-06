package com.codepath.cribslist.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private long uid;
    private String name;
    private String email;

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public void setUserPhotoURL(String userPhotoURL) {
        this.userPhotoURL = userPhotoURL;
    }

    private String location;
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

    public double getRating() {
        return rating;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public String getUserPhotoURL() {
        return userPhotoURL;
    }
    public User(long uid){
        this.uid = uid;
    }
    public User(long uid, String email){
        this.uid = uid;
        this.email = email;
    }
    public User(JSONObject jsonObject) throws JSONException {
        this.uid = jsonObject.getLong("id");
        this.name = jsonObject.getString("name");
        this.email = jsonObject.getString("email");
        this.location = jsonObject.getString("location");
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
