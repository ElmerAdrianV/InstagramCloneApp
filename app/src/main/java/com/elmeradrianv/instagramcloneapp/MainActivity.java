package com.elmeradrianv.instagramcloneapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.elmeradrianv.instagramcloneapp.adapters.PostAdapter;

import com.elmeradrianv.instagramcloneapp.database.Post;
import com.parse.ParseQuery;
import com.parse.ParseUser;



public class   MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int NUMBER_POSTS_REQUEST=5;
    ParseUser currentUser;
    private SwipeRefreshLayout swipeContainer;
    protected PostAdapter adapter;
    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;
    private  int currentOffset=NUMBER_POSTS_REQUEST;//Count number of tweets in the timeline
    RecyclerView rvPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createSwipeRefresh();
        getMenuInflater();
        currentUser=ParseUser.getCurrentUser();
        rvPosts = findViewById(R.id.rvPosts);


        // initialize the array that will hold posts and create a PostsAdapter
        adapter = new PostAdapter(this);

        // set the adapter on the recycler view
        rvPosts.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvPosts.setLayoutManager(linearLayoutManager);
        // query posts from Parstagram
        queryPosts(currentOffset);
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page,int totalItemsCount,RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvPosts.addOnScrollListener(scrollListener);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent;
        switch (item.getItemId()){
            case R.id.btnLogout:
                ParseUser.logOut();
                currentUser = ParseUser.getCurrentUser(); // this will now be null
                intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // this makes sure the Back button won't work
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // same as above
                startActivity(intent);
                return true;
            case R.id.btnPost:
                intent  = new Intent(this, NewPostActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void createSwipeRefresh(){
        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(() -> {
            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            fetchFeedAsync();
            swipeContainer.setRefreshing(false);
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void fetchFeedAsync() {
        adapter.clear();
        queryPosts(NUMBER_POSTS_REQUEST);
        currentOffset=NUMBER_POSTS_REQUEST;
    }

    private void queryPosts(int currentLimit) {
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include(Post.KEY_USER);
        query.include(Post.KEY_IMAGE);
        // limit query to latest 20 items
        query.setLimit(currentLimit);
        if(currentLimit-NUMBER_POSTS_REQUEST>0){
            query.setSkip(currentLimit-NUMBER_POSTS_REQUEST); // skip the first 10 results
        }

        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground((posts, e) -> {
            // check for errors
            if (e != null) {
                Log.e(TAG, "Issue with getting posts", e);
                return;
            }
            // save received posts to list and notify adapter of new data
            adapter.addAll(posts);
        });
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
            queryPosts(currentOffset+NUMBER_POSTS_REQUEST);
            int itemCountAdded=adapter.getItemCount()-currentOffset-1;
            adapter.notifyItemRangeInserted(currentOffset,itemCountAdded);
            currentOffset+=NUMBER_POSTS_REQUEST;
    }


}