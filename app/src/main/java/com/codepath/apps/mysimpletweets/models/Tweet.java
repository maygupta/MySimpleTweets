package com.codepath.apps.mysimpletweets.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by maygupta on 11/5/15.
 */
public class Tweet {

    public String body;
    public long id;
    public User user;
    public String createdAt;

    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        try {
            tweet.body = jsonObject.getString("body");
            tweet.id = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("createdAt");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
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

}
