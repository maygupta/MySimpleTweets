package com.codepath.apps.mysimpletweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

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

    @Column(name = "media_url")
    public String mediaUrl;

    @Column(name = "retweet_count")
    public String retweetCount;

    @Column(name = "favorite_count")
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
            if (isNewRecord(tweet)) {
                tweet.save();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    private static boolean isNewRecord(Tweet t) {
        if (byTweetId(t.id) != null) {
            return false;
        }
        return true;
    }

    // Record Finders
	public static Tweet byTweetId(long id) {
		return new Select().from(Tweet.class).where("tweet_id = ?", id).executeSingle();
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

        long dateMillis = 0;
        try {
            dateMillis = sf.parse(createdAt).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String timePresenter = "";

        long time = System.currentTimeMillis();
        long createdAt = time - dateMillis;

        int hours = (int) ((createdAt / (1000*60*60)) % 24);
        int minutes = (int) ((createdAt / (1000*60)) % 60);
        int seconds = (int) ((createdAt / (1000)) % 60);
        int days = (int) (hours / 24);
        int weeks = (int) (days / 7);

        if (weeks > 0) {
          timePresenter = weeks + "w";
        } else if ( days > 0 ){
            timePresenter = days + "d";
        } else if (hours > 0) {
            timePresenter = hours + "h";
        } else if ( minutes > 0) {
            timePresenter = minutes + "m";
        } else if ( seconds > 0) {
            timePresenter = seconds + "s";
        }

        return timePresenter;
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

