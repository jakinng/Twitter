package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class TweetDetailActivity extends AppCompatActivity {

    private TextView tvName;
    private TextView tvScreenName;
    private TextView tvBody;
    private ImageView ivProfileImage;
    private ImageView ivAttachedImage;

    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

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

        Tweet tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        tvName.setText(tweet.getUser().getName());
        tvScreenName.setText(tweet.getAtScreenName());
        tvBody.setText(tweet.getBody());
        Glide.with(this).load(tweet.getUser().getProfileImageUrl()).transform(new CircleCrop()).into(ivProfileImage);

        int corner_radius = 40;
        Glide.with(this).load(tweet.getImageUrl()).transform(new CenterInside(), new RoundedCorners(corner_radius)).into(ivAttachedImage);

    }
}