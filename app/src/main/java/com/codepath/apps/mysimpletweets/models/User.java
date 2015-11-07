package com.codepath.apps.mysimpletweets.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by maygupta on 11/5/15.
 */
public class User {

    public String name;
    public long uid;
    public String screenName;
    public String profileImageUrl;

    public static User fromJSON(JSONObject jsonObject) {
        User u = new User();
        try {
            u.name = jsonObject.getString("name");
            u.uid = jsonObject.getLong("id");
            u.screenName = jsonObject.getString("screen_name");
            u.profileImageUrl = jsonObject.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return u;
    }

    public String getScreeName() {
        return "@" + this.screenName;
    }
}
