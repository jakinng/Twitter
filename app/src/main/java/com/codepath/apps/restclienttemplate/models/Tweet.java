package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
public class Tweet {
    public static final String TAG = "Tweet";
    public static final int SECOND_MILLIS = 1000;
    public static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    public static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    public static final int DAY_MILLIS = 24 * HOUR_MILLIS;

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
            Log.d(TAG, tweet.body);
        } else {
            tweet.body = jsonObject.getString("text");
            Log.d(TAG, "NO FULL BODY " + tweet.body);
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
//        Log.d(TAG, Tweet.getRelativeTimeAgo(tweet.createdAt));
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

    // provide the relative time when the Tweet was tweeted, rather than the raw json date format
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try {
            long time = sf.parse(rawJsonDate).getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            // return the relative timestamps
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (ParseException e) {
            Log.d(TAG, "getRelativeTimeAgo failed");
            e.printStackTrace();
        }
        return "";
    }

    // Provide the handle + time created of the user and the tweet
    public String getHandle() {
        return "@" + user.getScreenName() + " · " + getRelativeTimeAgo(createdAt);
    }
}
