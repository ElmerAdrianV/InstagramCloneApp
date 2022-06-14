package com.elmeradrianv.instagramcloneapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.elmeradrianv.instagramcloneapp.Post;
import com.elmeradrianv.instagramcloneapp.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    List<Post> posts;
    Context context;

    public PostAdapter() {
        super();
    }

    public PostAdapter(Context context) {
        this.posts = new ArrayList<>();
        this.context=context;
    }

    public void clear(){
        posts.clear();
        notifyDataSetChanged();
    }
    public void addAll(List<Post> list){
        posts.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        //Get the data at position
        Post post = posts.get(position);
        //Bind the tweet with view holder
        holder.bind(post);
    }



    @Override
    public int getItemCount() {
        return posts.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvDescription;
        TextView tvUsername;
        TextView tvUsernameDescription;
        TextView tvTimeAgo;
        ImageView ivPost;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            tvDescription=itemView.findViewById(R.id.tvDescription);
            tvUsername=itemView.findViewById(R.id.tvUsername);
            tvUsernameDescription=itemView.findViewById(R.id.tvUsernameDescription);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
            ivPost=itemView.findViewById(R.id.ivPost);
        }

        public void bind(Post post) {
            tvDescription.setText(post.getDescription());
            tvUsername.setText(post.getUser().getUsername());
            tvUsernameDescription.setText(post.getUser().getUsername());
            tvTimeAgo.setText(calculateTimeAgo(post.getCreatedAt()));
            Glide.with(context).load(post.getImage().getUrl())
                    .apply(new RequestOptions()
                            .centerCrop() // scale image to fill the entire ImageView

                    )
                    .into(ivPost);
        }
    }
    public static String calculateTimeAgo(Date createdAt) {

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
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (Exception e) {
            Log.i("Error:", "getRelativeTimeAgo failed", e);
            e.printStackTrace();
        }

        return "";
    }
}
