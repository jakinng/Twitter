package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {
    public static final String TAG = "ComposeActivity";

    EditText etCompose;
    Button btnTweet;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApp.getRestClient(this);

        // Find the multiline textview and the tweet! button by ID
        etCompose = (EditText) findViewById(R.id.etCompose);
        btnTweet = (Button) findViewById(R.id.btnTweet);

        // Add an event listener for when the tweet! button is clicked
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tweetText = etCompose.getText().toString();
                // if the tweet is empty, display an error message
                if (tweetText.length() == 0) {
                    Toast.makeText(ComposeActivity.this, "Your tweet is empty!", Toast.LENGTH_SHORT).show();
                }
                // if the tweet is too long, display an error message
                else if (tweetText.length() > 280) {
                    Toast.makeText(ComposeActivity.this, "Your tweet is too long! The maximum length is 280 characters.", Toast.LENGTH_SHORT).show();
                }
                // otherwise, display the tweet as a toast and publish the tweet
                else {
                    Toast.makeText(ComposeActivity.this, tweetText, Toast.LENGTH_SHORT).show();
                    client.publishTweet(tweetText, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            try {
                                Tweet successfulTweet = Tweet.fromJson(json.jsonObject);
                                Log.d(TAG, "Successful tweet: " + successfulTweet.getBody());
                                // Prepare data intent and add the tweet data
                                Intent intent = new Intent();
                                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(successfulTweet));
                                setResult(RESULT_OK, intent); // set result code and bundle data for result
                                finish(); // closes the activity and passes data from intent to parent
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d(TAG, "The tweet request failed...");
                        }
                    });
                }
            }
        });

        // If this is a reply or not
        if (getIntent().hasExtra("username")) {
            String username = "@" + getIntent().getStringExtra("username") + " ";
            etCompose.setText(username);
        }
    }


}