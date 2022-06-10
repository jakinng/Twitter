package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

public class TweetDetailActivity extends AppCompatActivity {

    public static String TAG = "TweetDetailActivity";

    private TextView tvName;
    private TextView tvScreenName;
    private TextView tvBody;
    private ImageView ivProfileImage;
    private ImageView ivAttachedImage;
    private TextView title;
    private ImageButton buttonLiked;
    private ImageButton buttonRetweeted;

    private Tweet tweet;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        client = TwitterApp.getRestClient(this);

        // Add the toolbar instead of the actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Remove default title text
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Get access to the custom title view
        title = (TextView) toolbar.findViewById(R.id.toolbarTitle);

        // TODO : figure out how to center the title
        title.setGravity(Gravity.CENTER | Gravity.BOTTOM);
        title.setText("Tweet");

        tvName = (TextView) findViewById(R.id.tvName);
        tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        tvBody = (TextView) findViewById(R.id.tvBody);
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        ivAttachedImage = (ImageView) findViewById(R.id.ivAttachedImage);
        buttonLiked = (ImageButton) findViewById(R.id.buttonLiked);
        buttonRetweeted = (ImageButton) findViewById(R.id.buttonRetweeted);

        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        tvName.setText(tweet.getUser().getName());
        tvScreenName.setText(tweet.getAtScreenName());
        tvBody.setText(tweet.getBody());
        Glide.with(this).load(tweet.getUser().getProfileImageUrl()).transform(new CircleCrop()).into(ivProfileImage);

        int corner_radius = 40;
        Glide.with(this).load(tweet.getImageUrl()).transform(new CenterInside(), new RoundedCorners(corner_radius)).into(ivAttachedImage);

        // Set liked button to be true or false
        if (tweet.isLiked()) {
            buttonLiked.setImageResource(R.drawable.ic_vector_heart);
        }
        buttonLiked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tweet.isLiked()) {
                    client.unlikeTweet(tweet.getId(), new JsonHttpResponseHandler() {
                                               @Override
                                               public void onSuccess(int statusCode, Headers headers, JSON json) {
                                                   buttonLiked.setImageResource(R.drawable.ic_vector_heart_stroke);
                                               }

                                               @Override
                                               public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                                   Log.d(TAG, "Like tweet error: " + throwable.toString());
                                               }
                                           });
                } else {
                    client.likeTweet(tweet.getId(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            buttonLiked.setImageResource(R.drawable.ic_vector_heart);
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d(TAG, "Like tweet error: " + throwable.toString());
                        }
                    });
                }
                tweet.liked = !tweet.isLiked();
            }
        });

        // Set retweeted button to be true or false
        if (tweet.isRetweeted()) {
            buttonRetweeted.setImageResource(R.drawable.ic_vector_retweet);
        }
        buttonRetweeted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "I CLICKED IT!!!");
                if (tweet.isRetweeted()) {
                    client.unretweet(tweet.getId(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            buttonRetweeted.setImageResource(R.drawable.ic_vector_retweet_stroke);
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d(TAG, "Retweet error: " + throwable.toString());
                        }
                    });
                } else {
                    client.retweet(tweet.getId(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            buttonRetweeted.setImageResource(R.drawable.ic_vector_retweet);
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d(TAG, "Like tweet error: " + throwable.toString());
                        }
                    });
                }
                tweet.retweeted = !tweet.isRetweeted();
            }
        });
    }
}