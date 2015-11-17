package com.codepath.apps.mysimpletweets.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.activities.ProfileActivity;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.utils.LinkifiedTextView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by maygupta on 11/5/15.
 */
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    private ReplyClickListener replyClickListener;

    private static class ViewHolder {
        LinkifiedTextView tvTweet;
        TextView tvUsername;
        TextView tvScreenName;
        TextView tvTimeAgo;
        TextView tvRTCount;
        TextView tvFavCount;
        ImageView ivProfile;
        ImageView reply;
    }

    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, android.R.layout.simple_list_item_1 ,tweets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        final Tweet tweet = getItem(position);
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet,parent,false);

            viewHolder.tvTweet = (LinkifiedTextView) convertView.findViewById(R.id.tvTweet);

            viewHolder.tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
            viewHolder.tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
            viewHolder.tvTimeAgo = (TextView) convertView.findViewById(R.id.tvTimeAgo);
            viewHolder.tvRTCount = (TextView) convertView.findViewById(R.id.tvRTCount);
            viewHolder.tvFavCount = (TextView) convertView.findViewById(R.id.tvFavCount);

            viewHolder.ivProfile = (ImageView) convertView.findViewById(R.id.ivProfile);
            viewHolder.reply = (ImageView) convertView.findViewById(R.id.ivReply);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvTweet.setText(Html.fromHtml(tweet.body), TextView.BufferType.SPANNABLE);
        Picasso.with(getContext()).load(tweet.user.profileImageUrl).into(viewHolder.ivProfile);
        viewHolder.tvScreenName.setText(tweet.user.getScreeName());
        viewHolder.tvUsername.setText(tweet.user.name);
        viewHolder.tvTimeAgo.setText(tweet.getRelativeTimeAgo());
        viewHolder.tvRTCount.setText(tweet.retweetCount);
        viewHolder.tvFavCount.setText(tweet.favCount);

        viewHolder.ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ProfileActivity.class);
                i.putExtra("user", tweet.user);
                getContext().startActivity(i);
            }
        });

        return convertView;
    }

    public void setReplyListener(ReplyClickListener listener) {
        replyClickListener = listener;
    }

    public interface ReplyClickListener {
        public void onClick(Tweet tweet);
    }


}
