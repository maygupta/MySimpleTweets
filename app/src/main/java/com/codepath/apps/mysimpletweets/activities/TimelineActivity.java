package com.codepath.apps.mysimpletweets.activities;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.clients.TwitterApplication;
import com.codepath.apps.mysimpletweets.clients.TwitterClient;
import com.codepath.apps.mysimpletweets.fragments.ComposeTweetFragment;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.utils.EndlessScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter adapter;
    private ListView lvTweets;
    private SwipeRefreshLayout swipeContainer;
    private User loggedInUser;
    private static final int TWEET_DETAIL_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Set Title's color to Twitter blue
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(TwitterApplication.TwitterColor()));

        // Setup adapter and list view
        lvTweets = (ListView) findViewById(R.id.lvTweets);
        tweets = new ArrayList<>();
        adapter = new TweetsArrayAdapter(this, tweets);
        lvTweets.setAdapter(adapter);

        // Create Twitter client
        client = TwitterApplication.getRestClient();

        // Setup View listeners and event handles
        setupViews();

        // Populate User timeline
        populateTimeline();

        // Populate Current User
        populateLoggedInUser();
    }

    // Populates logged in user
    // If no network, find it from database using user id stored in shared prefs
    private void populateLoggedInUser() {
        client.getUserTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    loggedInUser = User.fromJSON(response.getJSONObject(0).getJSONObject("user"));
                    // Store the user in Shared Preferences
                    SharedPreferences pref = PreferenceManager
                            .getDefaultSharedPreferences(TimelineActivity.this);
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putLong("uid", loggedInUser.uid);
                    edit.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // If no network or failure, lets get the logged in user from share preferences
                SharedPreferences pref =
                                PreferenceManager.getDefaultSharedPreferences(TimelineActivity.this);
                long userId = pref.getLong("uid", -1);
                if (userId != -1) {
                    loggedInUser = User.byUId(userId);
                }
            }
        });
    }

    public void setupViews() {
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                customLoadMoreDataFromApi(page);
                return true;
            }
        });

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
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
                Intent i = new Intent(TimelineActivity.this, TweetDetailActivity.class);
                i.putExtra("tweet", tweet);
                startActivityForResult(i, TWEET_DETAIL_REQUEST);
            }
        });

    }

    // Refreshes timeline, clears the adapter and updates with new ones
    public void fetchTimelineAsync() {
        client.reset();
        client.getHomeTimeline(new JsonHttpResponseHandler() {
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
                Toast.makeText(TimelineActivity.this, "Sorry, Unable to refresh!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Get Result from TweetDetailActivity
    protected void onActivityResult(int requestCode, int resultCode,
             Intent data) {
         if (requestCode == TWEET_DETAIL_REQUEST && resultCode == RESULT_OK) {
            Tweet tweetReply = (Tweet) data.getSerializableExtra("tweet_reply");
             if (tweetReply != null) {
                 adapter.insert(tweetReply, 0);
                 adapter.notifyDataSetChanged();
                 client.incrementTweetsCount();
             }
         }
    }

    // Append more data into the adapter
    public void customLoadMoreDataFromApi(int offset) {
        if (!isNetworkAvailable()) {
            return;
        }
        if (client.getNumberOfPagesLoaded() <= offset) {
            populateTimeline();
        }
    }

    // Send an API request to Twitter
    // Fill the listview
    private void populateTimeline() {

        if (isNetworkAvailable() == false) {
            loadTweetsFromStore();
            return;
        }

        client.getHomeTimeline(new JsonHttpResponseHandler() {
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

    private void postSuccessCallback(JSONArray response) {
        adapter.addAll(Tweet.fromJSONArray(response));
        adapter.notifyDataSetChanged();
        client.updateTweetsCount();
        client.lastId = Tweet.getLastTweetId(response);
    }

    // Load tweets from persisted data store for good UX
    private void loadTweetsFromStore() {
        Toast.makeText(this, "Loading from Database!!", Toast.LENGTH_SHORT).show();
        List<Tweet> queryResults = new Select().from(Tweet.class).orderBy("tweet_id DESC").limit(50).execute();
        adapter.addAll(queryResults);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_compose) {
            FragmentManager manager = getFragmentManager();
            ComposeTweetFragment fragment = new ComposeTweetFragment();
            Bundle args = new Bundle();
            args.putString("username", loggedInUser.name);
            args.putString("screen_name", loggedInUser.getScreeName());
            args.putString("image_url", loggedInUser.profileImageUrl);
            fragment.setArguments(args);
            fragment.setDialogResultHandler(new ComposeTweetFragment.OnDialogResultHandler() {
                @Override
                public void finish(String tweetBody) {
                    // Save the tweet, show it on top
                    client.postTweet(tweetBody, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            adapter.insert(Tweet.fromJSON(response), 0);
                            adapter.notifyDataSetChanged();
                            client.incrementTweetsCount();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("ERROR", "Failed to get tweets homeline");
                        }
                    });
                }
            });
            fragment.show(manager, "ComposeDialog");
        }

        return super.onOptionsItemSelected(item);
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
