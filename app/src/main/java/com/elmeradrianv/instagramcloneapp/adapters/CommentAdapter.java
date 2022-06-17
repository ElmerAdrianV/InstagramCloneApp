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

import com.elmeradrianv.instagramcloneapp.database.Comment;
import com.elmeradrianv.instagramcloneapp.database.Post;
import com.elmeradrianv.instagramcloneapp.PostDetailView;
import com.elmeradrianv.instagramcloneapp.R;
import com.elmeradrianv.instagramcloneapp.database.UserCommentLikes;
import com.elmeradrianv.instagramcloneapp.database.UserPostLikes;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    public static final String TAG = "CommentAdapter";

    List<Comment> comments;
    Context context;
    //ParseUser currentUser = ParseUser.getCurrentUser();

    public CommentAdapter(Context context) {
        this.context = context;
        comments = new ArrayList<>();
    }

    public void clear() {
        comments.clear();
        notifyDataSetChanged();
    }


    public void addAll(List<Comment> list) {
        comments.addAll(list);
        notifyDataSetChanged();
    }
    public void addInPosition(Comment comment, int position){
        comments.add(position,comment);
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        //Get the data at position
        Comment comment = comments.get(position);
        try {
            holder.bind(comment);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserNameComment;
        TextView tvComment;
        TextView tvTimeAgo;
//        TextView tvNumLikes;
        ImageView ivPhotoUserComment;
//        ImageButton btnLike;
//        boolean likePost;
//        List<UserCommentLikes> userCommentLikes = new ArrayList<>();
        Post post;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUserNameComment = itemView.findViewById(R.id.tvUserComment);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
//            tvNumLikes = itemView.findViewById(R.id.tvNumberLikes);
            ivPhotoUserComment = itemView.findViewById(R.id.ivProfilePhotoComment);
//            btnLike = itemView.findViewById(R.id.btnLikeComment);
        }

        public void bind(Comment comment) throws ParseException {
            post = comment.getPost();
            tvComment.setText(comment.getComment());
            Comment.formatComment(tvComment);
            tvUserNameComment.setText(comment.getUser().getUsername());
            tvTimeAgo.setText(calculateTimeAgo(comment.getCreatedAt(), context));
//            tvNumLikes.setText(Long.toString(comment.getLikes()));
            putProfilePhoto(context, comment);
//            likePost = getLikeStatus(comment);
//            setImageBtnLike();
//            settingOnClickBtnLike(comment);
        }

        private void putProfilePhoto(Context context, Comment comment) {
            int radiusIP = 100; // corner radius, higher value = more rounded
            Glide.with(context).load(comment.getUser().getParseFile("profilePhoto").getUrl())
                    .apply(new RequestOptions()
                            .centerCrop() // scale image to fill the entire ImageView
                            .transform(new RoundedCorners(radiusIP))
                    )
                    .into(ivPhotoUserComment);

        }


//        private void setImageBtnLike() {
//            if (likePost)
//                btnLike.setImageResource(R.drawable.ic_like_active);
//            else
//                btnLike.setImageResource(R.drawable.ufi_heart_icon);
//        }
//
//        private void settingOnClickBtnLike(Comment comment) {
//
//            btnLike.setOnClickListener(v -> {
//                long newNumLikes = comment.getLikes();
//                likePost = !likePost;
//                //In progress, it need connect with the DB to search if the user already like the post
//                if (likePost) {
//                    newNumLikes += 1;
//                    comment.setLikes(newNumLikes);
//                    tvNumLikes.setText(Long.toString(newNumLikes));
//                    btnLike.setImageResource(R.drawable.ic_like_active);
//                    post.saveInBackground();
//                    try {
//                        addUserCommentLikes(comment, currentUser);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    newNumLikes -= 1;
//                    comment.setLikes(newNumLikes);
//                    tvNumLikes.setText(Long.toString(newNumLikes));
//                    btnLike.setImageResource(R.drawable.ufi_heart_icon);
//                    post.saveInBackground();
//                    deleteUserCommentLike(comment);
//                }
//            });
//        }
//        private void getQuery(Comment comment) {
//
//            // specify what type of data we want to query - Post.class
//            ParseQuery<UserCommentLikes> query = ParseQuery.getQuery(UserCommentLikes.class);
//
//            query.whereEqualTo(UserCommentLikes.KEY_USER, currentUser);
//            query.whereEqualTo(UserCommentLikes.KEY_COMMENT, comment);
//            // start an asynchronous call for comments
//
//            query.findInBackground((likes, e) -> {
//                // check for errors
//                if (e != null) {
//                    onQueryError(e);
//                } else {
//                    onQuerySuccess(likes);
//                }
//            });
//        }
//
//        private void onQueryError(ParseException e) {
//            Log.e(TAG, "Issue with getting the like", e);
//        }
//
//        private void onQuerySuccess(List<UserCommentLikes> likes) {
//            Log.d(TAG, "onQuerySuccess: All well");
//            userCommentLikes.addAll(likes);
//        }
//
//        public boolean getLikeStatus(Comment comment)  {
//            getQuery(comment);
//            return !userCommentLikes.isEmpty();
//        }
//
//        private void addUserCommentLikes(Comment comment, ParseUser user) throws ParseException {
//            UserCommentLikes like = new UserCommentLikes();
//            like.setComment(comment);
//            like.setUser(user);
//            like.saveInBackground(e -> {
//                if (e != null) {
//                    Log.e(TAG, "error while saving: ", e);
//                }
//            });
//        }
//
//        private void deleteUserCommentLike(Comment comment){
//            getQuery(comment);
//            UserCommentLikes like = userCommentLikes.get(0);
//            like.deleteInBackground();
//            userCommentLikes.remove(0);
//        }
    }
    public static String calculateTimeAgo(Date createdAt, Context context) {
        return PostAdapter.calculateTimeAgo(createdAt,context);
    }

}
