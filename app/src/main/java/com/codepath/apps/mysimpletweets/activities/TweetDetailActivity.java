package com.codepath.apps.mysimpletweets.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.clients.TwitterApplication;
import com.codepath.apps.mysimpletweets.clients.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

public class TweetDetailActivity extends AppCompatActivity {

    Tweet tweet;
    EditText etReply;
    TextView tvCharCount;
    private static final int MAX_CHARACTERS_COUNT = 140;
    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        // Set Title color
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(TwitterApplication.TwitterColor()));

        Intent intent = getIntent();
        tweet = (Tweet) intent.getSerializableExtra("tweet");
        populateDetailView(intent);

        client = TwitterApplication.getRestClient();
    }

    private void populateDetailView(Intent intent) {
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivTweetProfileImage);
        ImageView ivMedia= (ImageView) findViewById(R.id.ivMedia);
        TextView tvUsername = (TextView) findViewById(R.id.tvUsername);
        TextView tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        TextView tvTweetBody = (TextView) findViewById(R.id.tvTweetBody);
        TextView tvTimeAgo = (TextView) findViewById(R.id.tvTimeAgo);
        tvCharCount = (TextView) findViewById(R.id.tvCharCount);
        etReply = (EditText) findViewById(R.id.etReply);

        Picasso.with(this).load(tweet.user.profileImageUrl).into(ivProfileImage);
        if (tweet.mediaUrl != null) {
            Picasso.with(this).load(tweet.mediaUrl).into(ivMedia);
        }

        tvUsername.setText(tweet.user.name);
        tvScreenName.setText(tweet.user.getScreeName());
        tvTweetBody.setText(tweet.body);
        tvTimeAgo.setText(tweet.getRelativeTimeAgo());
        etReply.setHint(getResources().getString(R.string.reply_to) + " " + tweet.user.name);

        etReply.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateCounter(s.length());
            }
        });

        Button reply = (Button) findViewById(R.id.btReply);
        reply.setOnClickListener(new ReplyListener());
    }

    private void updateCounter(int length) {
        tvCharCount.setText(String.valueOf(MAX_CHARACTERS_COUNT - length));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tweet_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ReplyListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            client.replyToTweet(tweet.id, getTweetReplyBody(), new JsonHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    finish();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Tweet replyTweet = Tweet.fromJSON(response);
                    Intent i = new Intent();
                    i.putExtra("tweet_reply", replyTweet);
                    setResult(Activity.RESULT_OK, i);
                    finish();
                }
            });
        }
    }

    private String getTweetReplyBody() {
        return tweet.user.getScreeName() + " " + etReply.getText();
    }

}
