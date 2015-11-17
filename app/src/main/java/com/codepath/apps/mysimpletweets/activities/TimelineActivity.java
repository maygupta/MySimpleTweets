package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.clients.TwitterApplication;
import com.codepath.apps.mysimpletweets.clients.TwitterClient;
import com.codepath.apps.mysimpletweets.fragments.ComposeTweetFragment;
import com.codepath.apps.mysimpletweets.fragments.HomeTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.MentionsTimelineFragment;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

public class TimelineActivity extends AppCompatActivity {

    private static final int TWEET_DETAIL_REQUEST = 0;
    TwitterClient client;
    User loggedInUser;
    HomeTimelineFragment tweetsListFragment;
    LinearLayout linlaHeaderProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Set Title's color to Twitter blue
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(TwitterApplication.TwitterColor()));
        actionBar.setTitle(R.string.title_activity_timeline);
        actionBar.setLogo(R.drawable.ic_launcher);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);

        // Get the twitter rest client
        client = TwitterApplication.getRestClient();

        ViewPager vpPager = (ViewPager) findViewById(R.id.viewpager);
        vpPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));

        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(vpPager);

        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);

        populateLoggedInUser();
    }

    // Get Result from TweetDetailActivity
    protected void onActivityResult(int requestCode, int resultCode,
             Intent data) {
         if (requestCode == TWEET_DETAIL_REQUEST && resultCode == RESULT_OK) {
            Tweet tweetReply = (Tweet) data.getSerializableExtra("tweet_reply");
             if (tweetReply != null) {
                 tweetsListFragment.fetchTimelineAsync();
             }
         }
    }


    public void showProgressBar() {
        // Show progress item
        linlaHeaderProgress.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        // Hide progress item
        linlaHeaderProgress.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent i = new Intent(TimelineActivity.this, SearchActivity.class);
                i.putExtra("query", query);
                startActivity(i);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

     private void populateLoggedInUser() {
         showProgressBar();
        client.getUserInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                loggedInUser = User.fromJSON(response);
                // Store the user in Shared Preferences
                SharedPreferences pref = PreferenceManager
                        .getDefaultSharedPreferences(TimelineActivity.this);
                SharedPreferences.Editor edit = pref.edit();
                edit.putLong("uid", loggedInUser.uid);
                edit.commit();
                hideProgressBar();
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
                hideProgressBar();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_compose) {
            android.app.FragmentManager manager = getFragmentManager();
            final ComposeTweetFragment fragment = new ComposeTweetFragment();
            Bundle args = new Bundle();
            args.putString("username", loggedInUser.name);
            args.putString("screen_name", loggedInUser.getScreeName());
            args.putString("image_url", loggedInUser.profileImageUrl);
            args.putString("fragment_title", "Reply");
            fragment.setArguments(args);
            fragment.setDialogResultHandler(new ComposeTweetFragment.OnDialogResultHandler() {
                @Override
                public void finish(String tweetBody) {
                    showProgressBar();
                    // Save the tweet, show it on top
                    client.postTweet(tweetBody, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            tweetsListFragment.fetchTimelineAsync();
                            hideProgressBar();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("ERROR", "Failed to get tweets homeline");
                            hideProgressBar();
                        }
                    });
                }
            });
            fragment.show(manager, "ComposeDialog");
        }

        return super.onOptionsItemSelected(item);
    }

    public void onProfileViewClick(MenuItem item) {
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("user", loggedInUser);
        startActivity(i);
    }

    public class TweetsPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 2;
        private String tabTitles[] = {"Home", "Mentions"};

        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if ( position == 0) {
                return new HomeTimelineFragment();
            } else if (position == 1) {
                return new MentionsTimelineFragment();
            } else {
                return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }

}
