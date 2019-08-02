package com.codepath.apps.restclienttemplate;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    private RecyclerView rvTweets;
    private TweetsAdapter tweetsAdapter;
    private List<Tweet> tweets;

    private SwipeRefreshLayout  swipeContainer;

    private EndlessRecyclerViewScrollListener scrollListener;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApp.getRestClient(this);

        toolbar = findViewById(R.id.toolbar);

 //       setSupportActionBar(toolbar);

        rvTweets= findViewById(R.id.rvTweets);

        swipeContainer = findViewById(R.id.swipeContainer);

        tweets = new ArrayList<>();

        tweetsAdapter = new TweetsAdapter(this,tweets);

        rvTweets.setLayoutManager(new LinearLayoutManager(this));

        rvTweets.setAdapter(tweetsAdapter);
        scrollListener = new EndlessRecyclerViewScrollListener(new LinearLayoutManager(this)){
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page);
            }
        };

        rvTweets.addOnScrollListener(scrollListener);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("TwitterClient","content is being refresh");
                populateHomeTimeline();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,

                android.R.color.holo_green_light,

                android.R.color.holo_orange_light,

                android.R.color.holo_red_light);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login,menu);
        return true;
    }

    private void loadNextDataFromApi(int page) {
        client.getNextPageOfTweets(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                List<Tweet> tweetToAdd = new ArrayList<>();
                for(int i=0; i<response.length(); i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        Tweet tweet = Tweet.fromJson(jsonObject);

                        tweetToAdd.add(tweet);
//                        tweets.add(tweet);
//                        tweetsAdapter.notifyItemInserted(tweets.size()-1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                tweetsAdapter.clear();
                tweetsAdapter.addAll(tweetToAdd);
                swipeContainer.setRefreshing(false);
                scrollListener.resetState();
            }
        },page);
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("TwitterClient",response.toString());
                List<Tweet> tweetToAdd = new ArrayList<>();
                for(int i=0; i<response.length(); i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        Tweet tweet = Tweet.fromJson(jsonObject);

                        tweetToAdd.add(tweet);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                tweetsAdapter.clear();
                tweetsAdapter.addAll(tweetToAdd);
                swipeContainer.setRefreshing(false);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("TwitterClient",errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("TwitterClient", responseString);
            }
        });

    }
}
