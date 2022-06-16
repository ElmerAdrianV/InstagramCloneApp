package com.elmeradrianv.instagramcloneapp.database;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("UserPostLikes")
public class UserPostLikes extends ParseObject {
    public static final String KEY_USER="user";
    public static final String KEY_POST="post";
    public static final String KEY_LIKE="like";

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }
    public Post getPost(){ return (Post) getParseObject(KEY_POST);}

    public boolean getLike(){
        return getBoolean(KEY_LIKE);
    }
    public void setLike(boolean like){
        put(KEY_LIKE, like);
    }
}
