package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class User {
    public static String TAG = "User";

    public String name;
    public String screenName;
    public String profileImageUrl;
    public long userId;

    // empty constructor needed by the Parceler library
    public User() {
    }

    public User(String name, String screenName, String profileImageUrl, long userId) {
        this.name = name;
        this.screenName = screenName;
        this.profileImageUrl = profileImageUrl;
        this.userId = userId;
    }

    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url_https").replace("_normal.", ".");
        user.userId = jsonObject.getInt("id");
        Log.d(TAG, String.valueOf(user.userId));
        return user;
    }

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
