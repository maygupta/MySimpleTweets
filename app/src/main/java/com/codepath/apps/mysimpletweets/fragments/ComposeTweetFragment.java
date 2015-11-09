package com.codepath.apps.mysimpletweets.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.squareup.picasso.Picasso;

/**
 * Created by maygupta on 11/7/15.
 */
public class ComposeTweetFragment extends DialogFragment {

    private static final int MAX_CHARACTERS_COUNT = 140;
    private OnDialogResultHandler dialogResultHandler;
    private View view;
    private int position;
    Bundle args;
    TextView tvCharacterCount;

    public interface OnDialogResultHandler {
        void finish(String text);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        args = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Set title for this dialog
        getDialog().setTitle(R.string.compose_tweet);
        view = inflater.inflate(R.layout.compose_tweet_fragment, null);
        view.findViewById(R.id.btTweetSave).setOnClickListener(new SaveListener());
        view.findViewById(R.id.btCancel).setOnClickListener(new CancelListener());
        EditText etComposeTweetBody = (EditText) view.findViewById(R.id.etComposeTweetBody);
        tvCharacterCount = (TextView) view.findViewById(R.id.tvCharacterCount);
        tvCharacterCount.setText(String.valueOf(MAX_CHARACTERS_COUNT));
        etComposeTweetBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                updateCounter(s.length());
            }
        });

        populateDialog();
        return view;
    }

    private void updateCounter(int length) {
        tvCharacterCount.setText(String.valueOf(MAX_CHARACTERS_COUNT - length));
    }

    private void populateDialog() {
        ImageView ivComposeProfileImage = (ImageView) view.findViewById(R.id.ivComposeProfileImage);
        TextView tvScreenName = (TextView) view.findViewById(R.id.tvScreeName);
        TextView tvUsername = (TextView) view.findViewById(R.id.tvUsername);

        tvScreenName.setText(args.getString("screen_name"));
        tvUsername.setText(args.getString("username"));
        Picasso.with(getActivity()).load(args.getString("image_url")).into(ivComposeProfileImage);
    }

    private String getTweetBody() {
        EditText etComposeTweetBody = (EditText) view.findViewById(R.id.etComposeTweetBody);
        return etComposeTweetBody.getText().toString();
    }

    public void setDialogResultHandler(OnDialogResultHandler handler){
        dialogResultHandler = handler;
    }

    /**
     * LISTENERS FOR ACTION BUTTONS
     **/
    private class SaveListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            dialogResultHandler.finish(getTweetBody());
            ComposeTweetFragment.this.dismiss();
        }
    }

    private class CancelListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            ComposeTweetFragment.this.dismiss();
        }
    }
}
