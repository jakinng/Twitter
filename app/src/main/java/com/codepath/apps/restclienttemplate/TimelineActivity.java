package com.codepath.apps.restclienttemplate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

    public static final String TAG = "TimelineActivity";

    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApp.getRestClient(this);

        // Find the RecyclerView in the activity layout
        rvTweets = (RecyclerView) findViewById(R.id.rvTweets);

        // Initialize the list of tweets and the adapter
        tweets = new ArrayList<Tweet>();
        adapter = new TweetsAdapter(this, tweets);

        // Recycler view setup: layout manager and adapter
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);
        populateHomeTimeLine();
    }

    // Add tweets to the home timeline
    private void populateHomeTimeLine() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess! " + json.toString());

                // Populate tweets with the JSON array
                try {
                    tweets.addAll(Tweet.fromJsonArray(json.jsonArray));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure! " + response, throwable);
            }
        });
    }

    // Inflate the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if present
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    // Performs actions based on the menu items clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Compose icon has been selected
        if (item.getItemId() == R.id.compose) {
            Toast toast = Toast.makeText(this, "Compose a new tweet!", Toast.LENGTH_SHORT);
            toast.show();
            Intent intent = new Intent(this, ComposeActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.logout_button) {
            Toast toast = Toast.makeText(this, "Logging out!", Toast.LENGTH_SHORT);
            toast.show();

            // Navigate back to LoginActivity and forget the login token
            finish();
            client.clearAccessToken();
        }
        return true;
    }
}