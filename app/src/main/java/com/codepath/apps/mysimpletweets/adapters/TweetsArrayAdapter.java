package com.codepath.apps.mysimpletweets.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.codepath.apps.mysimpletweets.models.Tweet;

import java.util.List;

/**
 * Created by maygupta on 11/5/15.
 */
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, android.R.layout.simple_list_item_1 ,tweets);
    }
}