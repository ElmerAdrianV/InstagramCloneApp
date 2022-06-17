package com.elmeradrianv.instagramcloneapp.database;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("UserPostLikes")
public class UserPostLikes extends ParseObject {
    public static final String KEY_USER="user";
    public static final String KEY_POST="post";

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
}
