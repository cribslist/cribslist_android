package com.codepath.cribslist.models;

import com.codepath.cribslist.constants.COMMENT_FIELD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Comment {
    private String comment_id;

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setThread_id(long thread_id) {
        this.thread_id = thread_id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public String getText() {
        return text;
    }

    public long getThread_id() {
        return thread_id;
    }

    private Long user_id;

    public void setUsername(String username) {
        this.username = username;
    }

    private String username;
    private String text;
    private long thread_id;
    public Comment(String username, String text){
        this.username = username;
        this.text = text;
    }
    public Comment(JSONObject jsonObject) throws JSONException {
        this.comment_id = jsonObject.getString(COMMENT_FIELD.COMMENT_ID);
        this.user_id = jsonObject.getLong(COMMENT_FIELD.USER_ID);
        this.username = jsonObject.getString(COMMENT_FIELD.USERNAME);
        this.text = jsonObject.getString(COMMENT_FIELD.TEXT);
        this.thread_id = jsonObject.getLong(COMMENT_FIELD.THREAD_ID);
    }

    public static ArrayList<Comment> fromJSONArray(JSONArray comment) {
        ArrayList<Comment> results = new ArrayList<>();
            for (int i = 0; i < comment.length(); i++){
                try {
                    JSONObject obj = comment.getJSONObject(i);
                    Comment itm = new Comment(obj);
                    results.add(itm);
                } catch (JSONException e){
                    e.printStackTrace();

                }
            }
        return results;
    }
}
