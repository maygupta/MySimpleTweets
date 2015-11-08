package com.codepath.apps.mysimpletweets.models;

import android.text.format.DateUtils;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by maygupta on 11/5/15.
 */
@Table(name = "Tweets")
public class Tweet extends Model implements Serializable {

    @Column(name = "body")
    public String body;

    @Column(name = "tweet_id")
    public long id;

    @Column(name = "User")
    public User user;

    @Column(name = "created_at")
    public String createdAt;

    public String mediaUrl;
    public String retweetCount;
    public String favCount;


    public Tweet() {
        super();
    }

    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        try {
            tweet.body = jsonObject.getString("text");
            tweet.id = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
            JSONObject entities = jsonObject.getJSONObject("entities");
            JSONArray media;
            try {
                media = entities.getJSONArray("media");
            } catch (Exception e) {
                media = null;
            }

            if ( media != null ) {
                tweet.mediaUrl = media.getJSONObject(0).getString("media_url");
            }
            tweet.retweetCount = jsonObject.getString("retweet_count");
            tweet.favCount = jsonObject.getString("favorite_count");
            tweet.save();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray array) {
        ArrayList<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                tweets.add(fromJSON(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tweets;
    }

    public String getRelativeTimeAgo() {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(createdAt).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String[] splits = relativeDate.split(" ");
        String compactTime = "";
        if (splits[1].equals("minutes") || splits[1].equals("minute")) {
            compactTime = splits[0] + "m";
        } else if (splits[1].equals("seconds") || splits[1].equals("second")) {
            compactTime = splits[0] + "s";
        } else if (splits[1].equals("hours") || splits[1].equals("hour")) {
            compactTime = splits[0] + "h";
        } else if (splits[1].equals("days") || splits[1].equals("day")) {
            compactTime = splits[0] + "d";
        }

        return compactTime;
    }

    public static long getLastTweetId(JSONArray response) {
        int size = response.length();
        long retId = 0;
        try {
            retId = response.getJSONObject(size-1).getLong("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return retId;
    }
}

