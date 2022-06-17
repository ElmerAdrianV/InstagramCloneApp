package com.elmeradrianv.instagramcloneapp.database;


import android.widget.TextView;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;



@ParseClassName("Comment")
public class Comment extends ParseObject {
    public static final String KEY_USER="user";
    public static final String KEY_POST="post";
    public static final String KEY_COMMENT="comment";
    public static final String KEY_LIKES="likes";

    public Comment(){
    }
    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }
    public void setUser(ParseUser parseUser){
        put(KEY_USER, parseUser);
    }

    public Post getPost(){ return (Post) getParseObject(KEY_POST);}
    public void setPost(Post post){
        put(KEY_POST, post);
    }

    public String getComment(){ return getString(KEY_COMMENT); }
    public void setComment(String comment){  put(KEY_COMMENT,comment); }

    public long getLikes(){return getLong(KEY_LIKES);}
    public void setLikes(Long likes){put(KEY_LIKES,likes);}

    public static void formatComment(TextView tvComment){
        //Reusing same code
        Post.formatDescription(tvComment);
    }
}
