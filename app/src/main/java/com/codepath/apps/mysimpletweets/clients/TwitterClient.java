package com.codepath.apps.mysimpletweets.clients;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = "076E6mrrPPfcsbf3yzoYMu1eW";
	public static final String REST_CONSUMER_SECRET = "w4GHRxNfnW4KPtQ3J9hv6Me4PknD14aUOAqSDFOr5xyFsPoFVT";
	public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets"; // Change this (here and in manifest)
	public int tweetsCount = 25;

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */

	// Home Timeline ENDPOINT
	public void getHomeTimeline(long maxId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", tweetsCount);
		params.put("since_id", 1);
		if (maxId > 0) {
			params.put("max_id", maxId);
		}
		client.get(apiUrl, params, handler);
	}

	// User Timeline ENDPOINT
	public void getUserTimeline(long maxId, String name, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		RequestParams params = new RequestParams();
		if (name!= null && !name.isEmpty()) {
			params.put("screen_name", name);
		}
		if (maxId > 0) {
			params.put("max_id", maxId);
		}
		client.get(apiUrl, params, handler);
	}

	public void postTweet(String tweetBody, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", tweetBody);
		client.post(apiUrl, params, handler);
	}

	public void replyToTweet(long id, String replyBody, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", replyBody);
		params.put("in_reply_to_status_id", id);
		client.post(apiUrl, params, handler);
	}

	public void getMentionsTimeline(long maxId, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		RequestParams params = new RequestParams();
		if (maxId > 0) {
			params.put("max_id", maxId);
		}
		params.put("count", tweetsCount);
		client.get(apiUrl, params, handler);
	}

	public void getUserInfo(JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		client.get(apiUrl, handler);
	}
}