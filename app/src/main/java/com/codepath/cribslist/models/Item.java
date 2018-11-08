package com.codepath.cribslist.models;

import android.location.Address;
import android.os.Parcel;
import android.os.Parcelable;

import com.codepath.cribslist.constants.ITEM_FIELD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Item implements Parcelable {
    private long id;
    private String title;
    private long price;
    private String description;
    private Long sellerID;
    private String location;
    private double latitude;
    private double longitude;
    private String created;
    private ArrayList<Integer> category;
    private String thumbnailURL;
    private ArrayList<String> photoURLs = new ArrayList<>();

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public long getPrice() {
        if(price == 0){
            return 25;
        }
        return price;
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

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getCreated() {
        return created;
    }

    public ArrayList<Integer> getCategory() {
        return category;
    }

    public ArrayList<String> getPhotoURLs() {
        return photoURLs;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }


    public Item(){}

    public Item(String title, long price, String description,
                Long sellerID, String location, double latitude,
                double longitude, String created, ArrayList<Integer> category,
                String thumbnailURL, ArrayList<String> photoURLs) {
        this.title = title;
        this.price = price;
        this.description = description;
        this.sellerID = sellerID;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.created = created;
        this.category = category;
        this.thumbnailURL = thumbnailURL;
        this.photoURLs = photoURLs;
    }

    public Item(JSONObject jsonObject) throws JSONException{
        this.id = jsonObject.getLong(ITEM_FIELD.ID);
        this.title = jsonObject.getString(ITEM_FIELD.TITLE);
        this.description = jsonObject.getString(ITEM_FIELD.DESCRIPTION);
        this.sellerID = jsonObject.getLong(ITEM_FIELD.SELLER);
        this.location = jsonObject.getString(ITEM_FIELD.LOCATION);
        this.created = jsonObject.getString(ITEM_FIELD.CREATED);
        JSONArray categories = jsonObject.getJSONArray(ITEM_FIELD.CATEGORY);
        ArrayList<Integer> resultCategory = new ArrayList<>();
        for (int i = 0; i < categories.length(); i++) {
            resultCategory.add(categories.getInt(i));
        }
        this.category = resultCategory;

        JSONArray urls = jsonObject.getJSONArray(ITEM_FIELD.PHOTO_URLS);
        ArrayList<String> resultUrls = new ArrayList<>();
        for (int i = 0; i < urls.length(); i++){
            resultUrls.add(urls.getString(i));
        }
        this.photoURLs = resultUrls;

        this.thumbnailURL = jsonObject.getString(ITEM_FIELD.THUMBNAIL);

    }

    public static ArrayList<Item> fromJSONArray(JSONArray array) {
        ArrayList<Item> results = new ArrayList<>();

            for (int i = 0; i < array.length(); i++){
                try {
                JSONObject obj = array.getJSONObject(i);
                Item itm = new Item(obj);
                results.add(itm);
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            return results;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(title);
        out.writeLong(price);
        out.writeString(description);
        out.writeLong(sellerID);
        out.writeString(location);
        out.writeDouble(latitude);
        out.writeDouble(longitude);
        out.writeString(created);
        out.writeList(category);
        out.writeString(thumbnailURL);
        out.writeStringList(photoURLs);
    }

    private Item(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.price = in.readLong();
        this.description = in.readString();
        this.sellerID = in.readLong();
        this.location = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.created = in.readString();
        this.category = new ArrayList<>();
        in.readList(this.category, Integer.class.getClassLoader());
        this.thumbnailURL = in.readString();
        in.readStringList(photoURLs);
    }

    public void setLocationName(String locationName) {
        this.location = locationName;
    }

    public void setLocationFull(Address address){
        this.longitude = address.getLongitude();
        this.latitude = address.getLatitude();
        this.location = address.getLocality();
    }

    public void setLocationFull(Address address, String name){
        this.longitude = address.getLongitude();
        this.latitude = address.getLatitude();
        this.location = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Item> CREATOR
            = new Parcelable.Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

}
