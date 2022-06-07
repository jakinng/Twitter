package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ComposeActivity extends AppCompatActivity {
    EditText etCompose;
    Button btnTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

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
                }
            }
        });
    }
}