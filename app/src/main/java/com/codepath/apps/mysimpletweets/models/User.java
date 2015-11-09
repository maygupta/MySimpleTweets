package com.codepath.apps.mysimpletweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by maygupta on 11/5/15.
 */
@Table(name = "Users")
public class User extends Model implements Serializable {

    @Column(name = "username")
    public String name;

    @Column(name = "uid")
    public long uid;

    @Column(name = "screen_name")
    public String screenName;

    @Column(name = "profile_url")
    public String profileImageUrl;

    public User(){
        super();
    }

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
        u.save();
        return u;
    }

    // Record Finders
	public static User byUId(long id) {
		return new Select().from(User.class).where("uid = ?", id).executeSingle();
	}

    public String getScreeName() {
        return "@" + this.screenName;
    }

}
