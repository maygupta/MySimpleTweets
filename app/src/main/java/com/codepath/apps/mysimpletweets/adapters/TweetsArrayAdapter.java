package com.codepath.apps.mysimpletweets.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by maygupta on 11/5/15.
 */
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    private static class ViewHolder {
        TextView tvTweet;
        TextView tvUsername;
        TextView tvScreenName;
        TextView tvTimeAgo;
        ImageView ivProfile;
    }

    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, android.R.layout.simple_list_item_1 ,tweets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Tweet tweet = getItem(position);
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet,parent,false);

            viewHolder.tvTweet = (TextView) convertView.findViewById(R.id.tvTweet);
            viewHolder.tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
            viewHolder.tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
            viewHolder.tvTimeAgo = (TextView) convertView.findViewById(R.id.tvTimeAgo);
            viewHolder.ivProfile = (ImageView) convertView.findViewById(R.id.ivProfile);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvTweet.setText(tweet.body);
        Picasso.with(getContext()).load(tweet.user.profileImageUrl).into(viewHolder.ivProfile);
        viewHolder.tvScreenName.setText(tweet.user.getScreeName());
        viewHolder.tvUsername.setText(tweet.user.name);
        viewHolder.tvTimeAgo.setText(tweet.getRelativeTimeAgo());

        return convertView;
    }
}
