package com.codepath.apps.mysimpletweets.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.activities.TweetDetailActivity;
import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.clients.TwitterApplication;
import com.codepath.apps.mysimpletweets.clients.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.utils.EndlessScrollListener;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maygupta on 11/15/15.
 */
public abstract class TweetsListFragment extends Fragment {

    protected ArrayList<Tweet> tweets;
    protected TwitterClient client;
    protected TweetsArrayAdapter adapter;
    protected ListView lvTweets;
    protected SwipeRefreshLayout swipeContainer;
    protected static final int TWEET_DETAIL_REQUEST = 0;
    protected int VIEW_ID = R.layout.fragment_tweets_list;
    protected int LIST_VIEW_ID = R.id.lvTweets;

    // inflation logic
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(VIEW_ID, container, false);
        lvTweets = (ListView) v.findViewById(LIST_VIEW_ID);
        tweets = new ArrayList<>();
        adapter = new TweetsArrayAdapter(getActivity(), tweets);
        lvTweets.setAdapter(adapter);
        client = TwitterApplication.getRestClient();
        setupViews(v);
        populateTimeline();
        return v;
    }

    // Refreshes timeline, clears the adapter and updates with new ones
    public abstract void fetchTimelineAsync();

    // Append more data into the adapter
    public void customLoadMoreDataFromApi(int offset) {
        if (!isNetworkAvailable()) {
            return;
        }
        populateTimeline();
    }

    public void setupViews(View v) {
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                customLoadMoreDataFromApi(page);
                return true;
            }
        });

        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Set On Tweet click listeners
        lvTweets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tweet tweet = tweets.get(position);
                Intent i = new Intent(getActivity(), TweetDetailActivity.class);
                i.putExtra("tweet", tweet);
                startActivityForResult(i, TWEET_DETAIL_REQUEST);
            }
        });
    }

    protected Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    protected void postSuccessCallback(JSONArray response) {
        adapter.addAll(Tweet.fromJSONArray(response));
        adapter.notifyDataSetChanged();
    }

    // Load tweets from persisted data store for good UX
    protected void loadTweetsFromStore() {
//        Toast.makeText(getActivity(), "Loading from Database!!", Toast.LENGTH_SHORT).show();
        List<Tweet> queryResults = new Select().from(Tweet.class).orderBy("tweet_id DESC").limit(50).execute();
        adapter.addAll(queryResults);
        adapter.notifyDataSetChanged();
    }

    protected abstract void populateTimeline();

}

