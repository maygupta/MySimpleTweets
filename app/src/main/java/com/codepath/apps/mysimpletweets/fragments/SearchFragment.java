package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by maygupta on 11/16/15.
 */
public class SearchFragment extends TweetsListFragment {

    public static SearchFragment newInstance(String query) {
        SearchFragment myFragment = new SearchFragment();

        Bundle args = new Bundle();
        args.putString("query", query);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public void fetchTimelineAsync() {
        client.search(getArguments().getString("query"), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                adapter.clear();
                swipeContainer.setRefreshing(false);
                try {
                    postSuccessCallback(response.getJSONArray("statuses"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

        client.search(getArguments().getString("query"), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    postSuccessCallback(response.getJSONArray("statuses"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("ERROR", "Failed to get tweets homeline");
                loadTweetsFromStore();
            }
        });
    }
}
