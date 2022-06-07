package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;

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
                if (tweetText.length() == 0) {
                    Toast.makeText(ComposeActivity.this, "Your tweet is empty!", Toast.LENGTH_SHORT).show();
                } else if (tweetText.length() > 280) {
                    Toast.makeText(ComposeActivity.this, "Your tweet is too long! The maximum length is 280 characters.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ComposeActivity.this, tweetText, Toast.LENGTH_SHORT).show();
                    client.publishTweet(tweetText, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            try {
                                Tweet successfulTweet = Tweet.fromJson(json.jsonObject);
                                Log.d(TAG, successfulTweet.getBody());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d(TAG, "The tweet request failed...oh no...");
                        }
                    });
                }
            }
        });
    }


}