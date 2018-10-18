package com.codepath.cribslist.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class User {
    private long uid;
    private String name;
    private String email;
    private double latitude;
    private double longitude;
    private String description;
    private double rating;
    private ArrayList<Item> items;
    private String userPhotoURL;

    /*            "id": 12345,
            "name": "Sukwon Lee",
            "email": "slee@hearsaycorp.com",
            "latitude": 3.1,
            "longitude": -1.3,
            "description": "My son is 18 months old.",
            "rating": 9.8,
            "user_photo_url": "https://hsl-pnw-downloadable-files.s3.amazonaws.com/1/012889961ef0486883981e0ba7a9050d.jpg",
            "items": [*/

    public static User fromJSON(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.uid = jsonObject.getLong("id");
        user.name = jsonObject.getString("name");
        user.email = jsonObject.getString("email");
        user.latitude = jsonObject.getDouble("latitude");
        user.longitude = jsonObject.getDouble("longitude");
        user.description= jsonObject.getString("description");
        user.rating = jsonObject.getDouble("rating");
        user.userPhotoURL = jsonObject.getString("user_photo_url");
        JSONArray jsonArray = jsonObject.getJSONArray("items");
        ArrayList<Item> items = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = (JSONObject) jsonArray.get(i);
            Item item = Item.fromJSON(object);
            items.add(item);
        }

        user.items = items;
        return user;
    }
}
