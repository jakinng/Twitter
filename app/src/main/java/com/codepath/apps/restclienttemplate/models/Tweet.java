package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Tweet {
    public static final String TAG = "Tweet";

    public String body;
    public String createdAt;
    public User user;
    public String imageUrl;

    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public String getImageUrl() { return imageUrl; }

    // empty constructor needed by the Parceler library
    public Tweet() {

    }

    // returns a Tweet object, given a JSON object describing that tweet
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        // put in the full text String if possible for the body fo the tweet
        if (jsonObject.has("full_text")) {
            tweet.body = jsonObject.getString("full_text");
        } else {
            tweet.body = jsonObject.getString("text");
        }
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));

        // Add the URLs of attached images if possible
        JSONObject entities = jsonObject.getJSONObject("entities");
        if (entities.has("media")) {
            JSONArray media = entities.getJSONArray("media");
            String mediaUrl = media.getJSONObject(0).getString("media_url_https");
            tweet.imageUrl = mediaUrl;
//            Log.d(TAG, mediaUrl);
        } else {
//            Log.d(TAG, "No media attached to this tweet!");
        }
        return tweet;
    }

    // given an array of tweets in JSON form, return a list of Tweet objects
    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<Tweet>();
        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }
}
