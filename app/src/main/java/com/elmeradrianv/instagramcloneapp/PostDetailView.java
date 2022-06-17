package com.elmeradrianv.instagramcloneapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.elmeradrianv.instagramcloneapp.adapters.CommentAdapter;
import com.elmeradrianv.instagramcloneapp.adapters.PostAdapter;
import com.elmeradrianv.instagramcloneapp.auxiliars.EndlessRecyclerViewScrollListener;
import com.elmeradrianv.instagramcloneapp.database.Comment;
import com.elmeradrianv.instagramcloneapp.database.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;


public class PostDetailView extends AppCompatActivity {
    public static final String TAG = PostDetailView.class.getSimpleName();
    private static final int NUMBER_COMMENTS_REQUEST=5;
    Post post;
    private SwipeRefreshLayout swipeContainer;
    protected CommentAdapter adapter;
    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;
    private  int currentOffset=NUMBER_COMMENTS_REQUEST;//Count number of comments in the timeline
    RecyclerView rvComments;
    EditText etComment;
    ImageButton btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail_view);

        TextView tvDescription = findViewById(R.id.tvDescription);
        TextView tvUsernameDescription = findViewById(R.id.tvUsernameDescription);
        TextView tvTimeAgo=findViewById(R.id.tvTimeAgo);
        ImageView ivProfileUserPost = findViewById(R.id.ivProfileUserPost);


        ImageView ivPhotoCurrentUser=findViewById(R.id.ivProfilePhotoCurrentUser);
        etComment= findViewById(R.id.etComment);
        btnSend=findViewById(R.id.btnSend);
        String postId =  getIntent().getStringExtra(Post.class.getSimpleName());

        try {
            post = Post.getPost(postId);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        tvDescription.setText(post.getDescription());
        Post.formatDescription(tvDescription);
        tvUsernameDescription.setText(post.getUser().getUsername());
        tvTimeAgo.setText(PostAdapter.calculateTimeAgo(post.getCreatedAt(),PostDetailView.this));
        setImage(post.getUser().getParseFile("profilePhoto").getUrl(),ivProfileUserPost);
        setImage(ParseUser.getCurrentUser().getParseFile("profilePhoto").getUrl(),ivPhotoCurrentUser);
        rvComments=findViewById(R.id.rvComments)
;        // initialize the array that will hold posts and create a PostsAdapter
        adapter = new CommentAdapter(this);

        // set the adapter on the recycler view
        rvComments.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvComments.setLayoutManager(linearLayoutManager);
        // query posts from Parstagram
        queryComments(currentOffset);
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
        rvComments.addOnScrollListener(scrollListener);
        settingOnClickBtnSend();
    }
    private void settingOnClickBtnSend(){
        btnSend.setOnClickListener(v -> {
            String description=etComment.getText().toString();
            if(description.isEmpty()){
                Toast.makeText(PostDetailView.this,"Please, fill the comment", Toast.LENGTH_LONG)
                        .show();
            }
            else {
                savePost(description);
                etComment.setText("");
                adapter.notifyItemInserted(0);
                rvComments.scrollToPosition(0);
            }
        });
    }

    private void setImage(String url,ImageView imageView){
        int radiusIP = 100;
        Glide.with(this).load(url)
                .apply(new RequestOptions()
                        .centerCrop()
                        .transform(new RoundedCorners(radiusIP))
                )
                .into(imageView);
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
        queryComments(NUMBER_COMMENTS_REQUEST);
        currentOffset=NUMBER_COMMENTS_REQUEST;
    }

    private void queryComments(int currentLimit) {
        // specify what type of data we want to query - Post.class
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        // include data referred by user key
        query.include(Comment.KEY_USER);
        query.include(Comment.KEY_POST);
        // limit query to latest 20 items
        query.setLimit(currentLimit);
        query.setSkip(currentLimit-NUMBER_COMMENTS_REQUEST); // skip the first 10 results
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground((comments, e) -> {
            // check for errors
            if (e != null) {
                Log.e(TAG, "Issue with getting posts", e);
                return;
            }
            // save received posts to list and notify adapter of new data
            adapter.addAll(comments);
        });
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        queryComments(currentOffset+NUMBER_COMMENTS_REQUEST);
        int itemCountAdded=adapter.getItemCount()-currentOffset-1;
        adapter.notifyItemRangeInserted(currentOffset,itemCountAdded);
        currentOffset+=NUMBER_COMMENTS_REQUEST;
    }
    private void savePost(String bodyComment) {
        Comment comment = new Comment();
        comment.setComment(bodyComment);
        comment.setUser(ParseUser.getCurrentUser());
        comment.setPost(post);
        comment.setLikes(new Long(0));
        comment.saveInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "error while saving: ",e );
                Toast.makeText(PostDetailView.this,"error while saving", Toast.LENGTH_SHORT)
                        .show();
            }
            Log.i(TAG, "Post save was successful!");
        });
        adapter.addInPosition(comment,0);

    }

}