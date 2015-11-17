package com.codepath.apps.mysimpletweets.fragments;

import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by maygupta on 11/12/15.
 */
public class HomeTimelineFragment extends TweetsListFragment {

    // Refreshes timeline, clears the adapter and updates with new ones
    @Override
    public void fetchTimelineAsync() {
        client.getHomeTimeline(-1, new JsonHttpResponseHandler() {
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


        long maxId = -1;
        if(tweets.size() > 0) {
            maxId = tweets.get(tweets.size() - 1).id;
        }

        client.getHomeTimeline(maxId, new JsonHttpResponseHandler() {
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

