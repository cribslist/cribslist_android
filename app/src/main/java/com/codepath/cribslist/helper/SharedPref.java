package com.codepath.cribslist.helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    private static final SharedPref ourInstance = new SharedPref();

    public static SharedPref getInstance() {
        return ourInstance;
    }

    private static SharedPreferences mSharedPref;

    private static final String MY_PREFS_NAME = "MyPrefsFile";
    private static final String EMAIL_KEY = "email";
    private static final String USER_ID_KEY = "userId";

    private SharedPref() {
    }

    public static void init(Context context)
    {
        if(mSharedPref == null)
            mSharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
    }

    public void setEmail(String email) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString(EMAIL_KEY, email);
        prefsEditor.commit();
    }

    public String getEmail() {
        return mSharedPref.getString(EMAIL_KEY, "");
    }

    public void setUserId(String userId) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString(USER_ID_KEY, userId);
        prefsEditor.commit();
    }

    public String getUserId() {
        return mSharedPref.getString(USER_ID_KEY, "");
    }
}
