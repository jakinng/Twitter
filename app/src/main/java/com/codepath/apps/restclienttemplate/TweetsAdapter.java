package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import java.util.List;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    public static String TAG = "TweetsAdapter";

    // Pass in the context and a list of tweets
    Context context;
    List<Tweet> tweets;

    TwitterClient client;

    // setup the click listener
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) { this.listener = listener; }

    // Pass in the tweet array into the constructor
    public TweetsAdapter(Context context, List<Tweet> tweets, TwitterClient client) {
        this.context = context;
        this.tweets = tweets;
        this.client = client;
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

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of tweets
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    // Define a viewholder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Displays the @ of the Twitter user + the time when the tweet was created
        public TextView tvScreenName;
        // Displays the name of the Twitter user
        public TextView tvName;
        // Displays the text of the tweet
        public TextView tvBody;
        public ImageView ivProfileImage;
        public ImageView ivImageEntity;
        public Button buttonLiked;
        public Button buttonRetweeted;

        public ViewHolder(View itemView) {
            // Stores itemView in public final member variable that can be used to access the
            // context from any ViewHolder instance
            super(itemView);

            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            ivImageEntity = (ImageView) itemView.findViewById(R.id.ivImageEntity);
            buttonLiked = (Button) itemView.findViewById(R.id.buttonLiked);
            buttonRetweeted = (Button) itemView.findViewById(R.id.buttonRetweeted);

            // Setup the click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(itemView, position);
                        }
                    }
                }
            });
        }

        public void bind(Tweet tweet) {
            tvScreenName.setText(tweet.getHandle());
            tvName.setText(tweet.getUser().getName());
            tvBody.setText(tweet.getBody());
            Glide.with(context).load(tweet.getUser().getProfileImageUrl()).transform(new CircleCrop()).into(ivProfileImage);
            if (tweet.getImageUrl() != null) {
                ivImageEntity.setVisibility(View.VISIBLE);
                int corner_radius = 40;

                Glide.with(context).load(tweet.getImageUrl()).transform(new CenterInside(), new RoundedCorners(corner_radius)).into(ivImageEntity);
            } else {
                ivImageEntity.setVisibility(View.GONE);
            }

            // Set liked button to be true or false
            if (tweet.isLiked()) {
                buttonLiked.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vector_heart, 0, 0, 0);
            } else {
                buttonLiked.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vector_heart_stroke, 0, 0, 0);
            }
            buttonLiked.setText(String.valueOf(tweet.getLikedCount()));
            buttonLiked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tweet.isLiked()) {
                        client.unlikeTweet(tweet.getId(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                buttonLiked.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vector_heart_stroke, 0, 0, 0);
                                tweet.likedCount = tweet.getLikedCount() - 1;
                                buttonLiked.setText(String.valueOf(tweet.getLikedCount()));
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
                                buttonLiked.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vector_heart, 0, 0, 0);
                                tweet.likedCount = tweet.getLikedCount() + 1;
                                buttonLiked.setText(String.valueOf(tweet.getLikedCount()));
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
                buttonRetweeted.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vector_retweet, 0, 0, 0);
            } else {
                buttonRetweeted.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vector_retweet_stroke, 0, 0, 0);
            }
            buttonRetweeted.setText(String.valueOf(tweet.getRetweetedCount()));
            buttonRetweeted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tweet.isRetweeted()) {
                        client.unretweet(tweet.getId(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                buttonRetweeted.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vector_retweet_stroke, 0, 0, 0);
                                tweet.retweetedCount = tweet.getRetweetedCount() - 1;
                                buttonRetweeted.setText(String.valueOf(tweet.getRetweetedCount()));
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
                                buttonRetweeted.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vector_retweet, 0, 0, 0);
                                tweet.retweetedCount = tweet.getRetweetedCount() + 1;
                                buttonRetweeted.setText(String.valueOf(tweet.getRetweetedCount()));
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
}
