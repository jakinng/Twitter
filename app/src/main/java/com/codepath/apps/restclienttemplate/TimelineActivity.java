package com.codepath.apps.restclienttemplate;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

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

    private SwipeRefreshLayout swipeContainer;

    // Launcher for the composeActivity
    ActivityResultLauncher<Intent> composeActivityResultLauncher;

    // Endless scroll
    private EndlessRecyclerViewScrollListener scrollListener;

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

        // Setup the click listener
        adapter.setOnItemClickListener(new TweetsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                // Open detail view of tweet
                Toast.makeText(TimelineActivity.this, "Tweet at position " + position + " clicked!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TimelineActivity.this, TweetDetailActivity.class);
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweets.get(position)));
                startActivity(intent);
            }
        });

        // Recycler view setup: layout manager and adapter
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        // Retain an instance so that you can call resetState() for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                loadNextDataFromApi(page);
            }
        };

        rvTweets.addOnScrollListener(scrollListener);
        rvTweets.setAdapter(adapter);

        // Display tweets on timeline
        populateHomeTimeLine();
//        populateHomeTimeLineTest();

        // Set up the swipe refresh container
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh the list in helper method
                fetchTimelineAsync(0);
            }
        });

        // Configure the refreshing colors
        // TODO : change these colors to look better
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Setup the launcher for the compose activity upon creation
        composeActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
//                            Intent data = result.getData();
                            Tweet tweet = Parcels.unwrap(result.getData().getParcelableExtra(Tweet.class.getSimpleName()));
                            tweets.add(0, tweet);
                            adapter.notifyItemInserted(0);
                            rvTweets.smoothScrollToPosition(0);
                        }
                    }
                });
    }

    // Append the next page of data into the adapter
    // Sends out a network request and appends new data items to the adapter
    private void loadNextDataFromApi(int page) {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    adapter.addAll(Tweet.fromJsonArray(json.jsonArray));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "Load more data error: " + throwable.toString());
            }
        },
                10000);
    }

    private void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                adapter.clear();
                try {
                    adapter.addAll(Tweet.fromJsonArray(json.jsonArray));
                    // Reset endless scroll listener after refresh
                    scrollListener.resetState();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "Fetch timeline error: " + throwable.toString());
            }
        });
    }

    // Add tweets to the home timeline
    private void populateHomeTimeLine() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess! " + json.toString());
                // Populate tweets with the JSON array
                try {
                    // automatically notifies adapter that dataset has changed
                    adapter.addAll(Tweet.fromJsonArray(json.jsonArray));
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

    private void populateHomeTimeLineTest() {
        List<Tweet> fakeTweets = new ArrayList<Tweet>();
        fakeTweets.add(new Tweet("hi im jakin", "Wed Oct 10 20:19:24 +0000 2018", new User("Jakin Ng", "jakinng", "https://twitter.com/CarlBovisNature/status/1521887921888018442/photo/1"), null, 105018));
        adapter.addAll(fakeTweets);
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

            // Start the compose activity for a result
            startComposeActivity();
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

    // Uses launcher to start a new ComposeActivity that can return a result (the body of the composed tweet) to the parent activity (this TimelineActivity)
    public void startComposeActivity() {
        Intent intent = new Intent(this, ComposeActivity.class);

        //Launch activity to get result
        composeActivityResultLauncher.launch(intent);
    }
}