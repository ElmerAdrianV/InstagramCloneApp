package com.elmeradrianv.instagramcloneapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import com.elmeradrianv.instagramcloneapp.database.Post;
import com.elmeradrianv.instagramcloneapp.PostDetailView;
import com.elmeradrianv.instagramcloneapp.R;
import com.elmeradrianv.instagramcloneapp.database.UserPostLikes;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    public static final String TAG = "PostAdapter";
    List<Post> posts;
    Context context;
    ParseUser currentUser = ParseUser.getCurrentUser();

    public PostAdapter(Context context) {
        this.posts = new ArrayList<>();
        this.context = context;
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public void clearWithoutNotify() {
        posts.clear();
    }

    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        //Get the data at position
        Post post = posts.get(position);
        //Bind the tweet with view holder
        try {
            holder.bind(post);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvDescription;
        TextView tvUsername;
        TextView tvUsernameDescription;
        TextView tvTimeAgo;
        TextView tvNumLikes;
        ImageView ivPost;
        ImageView ivProfileUser;
        ImageButton btnLike;
        ImageButton btnComment;
        boolean likePost;
        List<UserPostLikes> userPostLikes = new ArrayList<>();

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvUsernameDescription = itemView.findViewById(R.id.tvUsernameDescription);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
            ivPost = itemView.findViewById(R.id.ivPost);
            ivProfileUser = itemView.findViewById(R.id.ivProfileUserPost);
            tvNumLikes = itemView.findViewById(R.id.tvNumLikes);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnComment = itemView.findViewById(R.id.btnComment);
            itemView.setOnClickListener(this);
        }

        public void bind(Post post) throws ParseException {

            tvDescription.setText(post.getDescription());
            Post.formatDescription(tvDescription);
            tvUsername.setText(post.getUser().getUsername());
            tvUsernameDescription.setText(post.getUser().getUsername());
            tvTimeAgo.setText(calculateTimeAgo(post.getCreatedAt(), context));
            tvNumLikes.setText(Long.toString(post.getLikes()));
            putPostImage(context, post);
            putProfilePhoto(context, post);
            likePost = getLikeStatus(post);
            setImageBtnLike();
        }

        private void putPostImage(Context context, Post post) {
            Glide.with(context).load(post.getImage().getUrl())
                    .apply(new RequestOptions()
                            .fitCenter() // scale image to fill the entire ImageView
                    )
                    .into(ivPost);
        }

        private void putProfilePhoto(Context context, Post post) {
            int radiusIP = 100; // corner radius, higher value = more rounded
            Glide.with(context).load(post.getUser().getParseFile("profilePhoto").getUrl())
                    .apply(new RequestOptions()
                            .centerCrop() // scale image to fill the entire ImageView
                            .transform(new RoundedCorners(radiusIP))
                    )
                    .into(ivProfileUser);
            settingOnClickBtnLike(post);
            settingOnClickBtnComment(post);
        }

        private void setImageBtnLike() {
            if (likePost)
                btnLike.setImageResource(R.drawable.ic_like_active);
            else
                btnLike.setImageResource(R.drawable.ufi_heart_icon);
        }

        private void settingOnClickBtnLike(Post post) {

            btnLike.setOnClickListener(v -> {
                long newNumLikes = post.getLikes();
                likePost = !likePost;
                //In progress, it need connect with the DB to search if the user already like the post
                if (likePost) {
                    newNumLikes += 1;
                    post.setLikes(newNumLikes);
                    tvNumLikes.setText(Long.toString(newNumLikes));
                    btnLike.setImageResource(R.drawable.ic_like_active);
                    post.saveInBackground();
                    try {
                        addUserPostLikes(post, currentUser);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    newNumLikes -= 1;
                    post.setLikes(newNumLikes);
                    tvNumLikes.setText(Long.toString(newNumLikes));
                    btnLike.setImageResource(R.drawable.ufi_heart_icon);
                    post.saveInBackground();
                    deleteUserPostLikes(post);
                }
            });
        }
        private void getQuery(Post post) {

            // specify what type of data we want to query - Post.class
            ParseQuery<UserPostLikes> query = ParseQuery.getQuery(UserPostLikes.class);

            query.whereEqualTo(UserPostLikes.KEY_USER, currentUser);
            query.whereEqualTo(UserPostLikes.KEY_POST, post);
            // start an asynchronous call for posts

            query.findInBackground((likes, e) -> {
                // check for errors
                if (e != null) {
                    onQueryError(e);
                } else {
                    onQuerySuccess(likes);
                }
            });
        }

        private void onQueryError(ParseException e) {
            Log.e(TAG, "Issue with getting the like", e);
        }

        private void onQuerySuccess(List<UserPostLikes> likes) {
            Log.d(TAG, "onQuerySuccess: All well");
            userPostLikes.addAll(likes);
        }

        public boolean getLikeStatus(Post post)  {
            getQuery(post);
            return !userPostLikes.isEmpty();
        }

        private void addUserPostLikes(Post post, ParseUser user) throws ParseException {
            UserPostLikes like = new UserPostLikes();
            like.setPost(post);
            like.setUser(user);
            like.saveInBackground(e -> {
                if (e != null) {
                    Log.e(TAG, "error while saving: ", e);
                }
            });
        }

        private void deleteUserPostLikes(Post post){
            getQuery(post);
            UserPostLikes like = userPostLikes.get(0);
            like.deleteInBackground();
            userPostLikes.remove(0);
        }


        private void settingOnClickBtnComment(Post post) {

            Log.d("Onclick///", "onClick:Go to commments ");
            btnComment.setOnClickListener(v -> {
                //create intent for the new activity
                Intent intent = new Intent(context, PostDetailView.class);
                intent.putExtra(Post.class.getSimpleName(), post.getObjectId());
                //show the activity
                context.startActivity(intent);
            });
        }

        @Override
        public void onClick(View v) {
            //gets item position
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Post post = posts.get(position);
                //create intent for the new activity
                Intent intent = new Intent(context, PostDetailView.class);
                intent.putExtra(Post.class.getSimpleName(), post.getObjectId());
                //show the activity
                context.startActivity(intent);
                Log.d("Onclick///", "onClick:Full view of post ");
            }
        }
    }

    public static String calculateTimeAgo(Date createdAt, Context context) {

        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try {
            createdAt.getTime();
            long time = createdAt.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return context.getString(R.string.just_now);
            } else if (diff < 2 * MINUTE_MILLIS) {
                return context.getString(R.string.a_minute_ago);
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " " + context.getString(R.string.min);
            } else if (diff < 90 * MINUTE_MILLIS) {
                return context.getString(R.string.an_hour_ago);
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " " + context.getString(R.string.hours);
            } else if (diff < 48 * HOUR_MILLIS) {
                return context.getString(R.string.yesterday);
            } else {
                return diff / DAY_MILLIS + " " + context.getString(R.string.day);
            }
        } catch (Exception e) {
            Log.i("Error:", "getRelativeTimeAgo failed", e);
            e.printStackTrace();
        }

        return "";
    }
}
