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

import com.parse.ParseQuery;
import com.parse.ParseUser;



public class   MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    ParseUser currentUser;
    private SwipeRefreshLayout swipeContainer;
    protected PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        RecyclerView rvPosts;
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
        // set the layout manager on the recycler view
        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        // query posts from Parstagram
        queryPosts();
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
        queryPosts();

    }

    private void queryPosts() {
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include(Post.KEY_USER);
        query.include(Post.KEY_IMAGE);
        // limit query to latest 20 items
        query.setLimit(20);
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

}