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

    // setup the click listener
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) { this.listener = listener; }

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

        public ViewHolder(View itemView) {
            // Stores itemView in public final member variable that can be used to access the
            // context from any ViewHolder instance
            super(itemView);

            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            ivImageEntity = (ImageView) itemView.findViewById(R.id.ivImageEntity);

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
            Glide.with(context).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);
            if (tweet.getImageUrl() != null) {
                ivImageEntity.setVisibility(View.VISIBLE);
                Glide.with(context).load(tweet.getImageUrl()).into(ivImageEntity);
            } else {
                ivImageEntity.setVisibility(View.GONE);
            }
        }
    }
}
