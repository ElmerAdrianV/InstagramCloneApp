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

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }
    public Post getPost(){ return (Post) getParseObject(KEY_POST);}
    public String getComment(){ return getString(KEY_COMMENT); }

    public static void formatComment(TextView tvComment){
        //Reusing same code
        Post.formatDescription(tvComment);
    }
}
