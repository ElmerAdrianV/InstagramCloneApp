package com.elmeradrianv.instagramcloneapp;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.elmeradrianv.instagramcloneapp.adapters.PostAdapter;
import com.parse.ParseException;
import com.parse.ParseUser;



public class PostDetailView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail_view);

        TextView tvDescription = findViewById(R.id.tvDescription);
        TextView tvUsernameDescription = findViewById(R.id.tvUsernameDescription);
        TextView tvTimeAgo=findViewById(R.id.tvTimeAgo);
        ImageView ivProfileUserPost = findViewById(R.id.ivProfileUserPost);

        ParseUser currentUser=ParseUser.getCurrentUser();
        ImageView ivPhotoCurrentUser=findViewById(R.id.ivProfilePhotoCurrentUser);



        String postId =  getIntent().getStringExtra(Post.class.getSimpleName());

        Post post= null;
        try {
            post = Post.getPost(postId);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        tvDescription.setText(post.getDescription());
        Post.formatDescription(tvDescription);
        tvUsernameDescription.setText(post.getUser().getUsername());
        tvTimeAgo.setText(PostAdapter.calculateTimeAgo(post.getCreateAt(),this));
        setImage(post.getUser().getParseFile("profilePhoto").getUrl(),ivProfileUserPost);

        setImage(currentUser.getParseFile("profilePhoto").getUrl(),ivPhotoCurrentUser);
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
}