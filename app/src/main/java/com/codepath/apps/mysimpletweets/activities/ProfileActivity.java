package com.codepath.apps.mysimpletweets.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.fragments.UserTimelineFragment;
import com.codepath.apps.mysimpletweets.models.User;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    User user;
    ImageView ivBackground;
    ImageView ivProfile;
    TextView tvUsername;
    TextView tvScreenName;
    TextView tvTweetsCount;
    TextView tvFollowersCount;
    TextView tvFollowingCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Hide action bar for Profile timeline
        getSupportActionBar().hide();

        // Get the User object passed in Intent
        user = (User) getIntent().getSerializableExtra("user");

        // Setup views and fill the data
        setupViews();
        loadDataInViews();

        if (savedInstanceState == null ) {
            // Load the tweets fragment for the User
            UserTimelineFragment fragment = UserTimelineFragment.newInstance(user.screenName);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, fragment);
            ft.commit();
        }
    }

    private void loadDataInViews() {
        Picasso.with(this).load(Uri.parse(user.backgroundUrl)).into(ivBackground);
        Picasso.with(this).load(Uri.parse(user.profileImageUrl)).into(ivProfile);
        tvUsername.setText(user.name);
        tvScreenName.setText(user.getScreeName());
        tvFollowingCount.setText(Html.fromHtml("FOLLOWERS <br>" + user.followers));
        tvFollowersCount.setText(Html.fromHtml("FOLLOWING <br>" + user.following));
        tvTweetsCount.setText(Html.fromHtml("TWEETS <br>" + user.statusCount));
    }

    private void setupViews() {
        ivBackground = (ImageView) findViewById(R.id.ivBackground);
        ivProfile = (ImageView) findViewById(R.id.ivProfile);
        tvUsername = (TextView) findViewById(R.id.tvUsername);
        tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        tvTweetsCount = (TextView) findViewById(R.id.tvTweetsCount);
        tvFollowersCount = (TextView) findViewById(R.id.tvFollowersCount);
        tvFollowingCount = (TextView) findViewById(R.id.tvFollowingCount);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
