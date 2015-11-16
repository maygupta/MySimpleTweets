package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by maygupta on 11/15/15.
 */
public class UserTimelineFragment extends TweetsListFragment {

    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment userTimelineFragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        userTimelineFragment.setArguments(args);
        return userTimelineFragment;
    }

    @Override
    public void fetchTimelineAsync() {
        String userScreenName = getArguments().getString("screen_name");
        client.getUserTimeline(-1, userScreenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                adapter.clear();
                swipeContainer.setRefreshing(false);
                postSuccessCallback(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("ERROR", "Failed to get tweets homeline");
                swipeContainer.setRefreshing(false);
                Toast.makeText(getActivity(), "Sorry, Unable to refresh!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void populateTimeline() {
        if (isNetworkAvailable() == false) {
            loadTweetsFromStore();
            return;
        }
        String userScreenName = getArguments().getString("screen_name");
        long maxId = -1;
        if(tweets.size() > 0) {
            maxId = tweets.get(tweets.size() - 1).id;
        }


        client.getUserTimeline(maxId, userScreenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                postSuccessCallback(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("ERROR", "Failed to get tweets homeline");
                loadTweetsFromStore();
            }
        });
    }
}
