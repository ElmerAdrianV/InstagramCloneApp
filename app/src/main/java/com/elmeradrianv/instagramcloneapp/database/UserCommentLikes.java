package com.elmeradrianv.instagramcloneapp.database;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("UserCommentLikes")
public class UserCommentLikes extends ParseObject {
    public static final String KEY_USER="user";
    public static final String KEY_COMMENT="comment";

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }
    public void setUser(ParseUser parseUser){
        put(KEY_USER, parseUser);
    }

    public Comment getComment(){ return (Comment) getParseObject(KEY_COMMENT);}
    public void setComment(Comment comment){
        put(KEY_COMMENT, comment);
    }
}
