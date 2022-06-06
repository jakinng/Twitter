package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    // Pass in the context and a list of tweets
    Context context;
    List<Tweet> tweets;

    // Pass in the tweet array into the constructor
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // For each row, inflate the layout
    @NonNull
    @Override
    public TweetsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View tweet_view = inflater.inflate(R.layout.item_tweet, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(tweet_view);
        return viewHolder;
    }

    // Bind the Tweet at the provided position to the ViewHolder
    @Override
    public void onBindViewHolder(@NonNull TweetsAdapter.ViewHolder holder, int position) {
        // Get the tweet at the specified position
        Tweet tweet = tweets.get(position);

        // Bind the tweet with the viewholder
        holder.bind(tweet);
    }

    // return the number of tweets
    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Define a viewholder
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvScreenName;
        public TextView tvBody;
        public ImageView ivProfileImage;

        public ViewHolder(View itemView) {
            // Stores itemView in public final member variable that can be used to access the
            // context from any ViewHolder instance
            super(itemView);

            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
        }

        public void bind(Tweet tweet) {
            tvScreenName.setText(tweet.getUser().getScreenName());
            tvBody.setText(tweet.getBody());
            Glide.with(context).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);
        }
    }
}
