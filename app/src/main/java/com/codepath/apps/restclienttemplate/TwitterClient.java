package com.codepath.apps.restclienttemplate;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.FlickrApi;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class TwitterClient extends OAuthBaseClient {
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance();
	public static final String REST_URL = "https://api.twitter.com/1.1/";
	public static final String REST_CONSUMER_KEY = "rJb8htSoIh2yNKyMXL0CtH77a";
	public static final String REST_CONSUMER_SECRET = "ySvdlhwkOZ0Y8JdxelKg8Cta4Io4OnqEqY8Yh7ZF7dhdCSiwr8";

	// Landing page to indicate the OAuth
	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

	public TwitterClient(Context context) {
		super( context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				String.format( REST_CALLBACK_URL_TEMPLATE, context.getString( R.string.intent_host ),
						context.getString( R.string.intent_scheme ), context.getPackageName(), FALLBACK_URL ) );
	}

	// CHANGE THIS
	// DEFINE METHODS for different API endpoints here
	public void getHomeTimeline(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl( "statuses/home_timeline.json" );

		RequestParams params = new RequestParams();
		params.put( "count", 25 );
		params.put( "since_id", 1 );
		client.get( apiUrl, params, handler );
	}

	public void getNextPageOfTweets(AsyncHttpResponseHandler handler, long maxId) {
		String apiUrl = getApiUrl( "statuses/home_timeline.json" );
		RequestParams params = new RequestParams();
		params.put( "count", 25 );
		params.put( "max_id", maxId );
		client.get( apiUrl, params, handler );
	}
}