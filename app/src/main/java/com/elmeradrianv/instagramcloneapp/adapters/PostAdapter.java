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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.elmeradrianv.instagramcloneapp.Post;
import com.elmeradrianv.instagramcloneapp.PostDetailView;
import com.elmeradrianv.instagramcloneapp.R;

import org.parceler.Parcels;

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
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvDescription;
        TextView tvUsername;
        TextView tvUsernameDescription;
        TextView tvTimeAgo;
        TextView tvNumLikes;
        ImageView ivPost;
        ImageView ivProfileUser;
        ImageButton btnLike;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            tvDescription=itemView.findViewById(R.id.tvDescription);
            tvUsername=itemView.findViewById(R.id.tvUsername);
            tvUsernameDescription=itemView.findViewById(R.id.tvUsernameDescription);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
            ivPost=itemView.findViewById(R.id.ivPost);
            ivProfileUser=itemView.findViewById(R.id.ivProfileUserPost);
            tvNumLikes=itemView.findViewById(R.id.tvNumLikes);
            btnLike =itemView.findViewById(R.id.btnLike);
            itemView.setOnClickListener(this);
        }

        public void bind(Post post) {
            int radiusIP = 100; // corner radius, higher value = more rounded
            tvDescription.setText(post.getDescription());
            tvUsername.setText(post.getUser().getUsername());
            tvUsernameDescription.setText(post.getUser().getUsername());
            tvTimeAgo.setText(calculateTimeAgo(post.getCreatedAt(),context));
            tvNumLikes.setText(Long.toString(post.getLikes()));
            Glide.with(context).load(post.getImage().getUrl())
                    .apply(new RequestOptions()
                            .fitCenter() // scale image to fill the entire ImageView
                    )
                    .into(ivPost);
            Glide.with(context).load(post.getUser().getParseFile("profilePhoto").getUrl())
                    .apply(new RequestOptions()
                            .centerCrop() // scale image to fill the entire ImageView
                            .transform(new RoundedCorners(radiusIP))
                    )
                    .into(ivProfileUser);
            settingOnClickBtnLike(post);
        }
        private void settingOnClickBtnLike(Post post){
            btnLike.setOnClickListener(v -> {
                //In progress, it need connect with the DB to search if the user already like the post
                //
                long newNumLikes=post.getLikes()+1;
                post.setLikes(newNumLikes);
                tvNumLikes.setText(Long.toString(newNumLikes));
                btnLike.setImageResource(R.drawable.ic_like_active);
                post.saveInBackground();
            });
        }
        @Override
        public void onClick(View v) {
            //gets item position
            int position=getAdapterPosition();
            Toast.makeText(context,"In", Toast.LENGTH_LONG).show();
            if(position!=RecyclerView.NO_POSITION){
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
    public static String calculateTimeAgo(Date createdAt,Context context) {

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
                return diff / MINUTE_MILLIS + " "+context.getString(R.string.min);
            } else if (diff < 90 * MINUTE_MILLIS) {
                return context.getString(R.string.an_hour_ago);
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " "+context.getString(R.string.hours);
            } else if (diff < 48 * HOUR_MILLIS) {
                return context.getString(R.string.yesterday);
            } else {
                return diff / DAY_MILLIS + " "+context.getString(R.string.day);
            }
        } catch (Exception e) {
            Log.i("Error:", "getRelativeTimeAgo failed", e);
            e.printStackTrace();
        }

        return "";
    }
}
