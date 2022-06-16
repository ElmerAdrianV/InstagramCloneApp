package com.elmeradrianv.instagramcloneapp;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.io.File;
import java.util.Date;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String TAG="Post";
    public static final String KEY_OBJECTID="objectId";
    public static final String KEY_DESCRIPTION="description";
    public static final String KEY_IMAGE="image";
    public static final String KEY_USER="user";
    public static final String KEY_CREATED_AT="createdAt";
    public static final String KEY_LIKES="likes";


    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }
    public void setDescription(String description){
         put(KEY_DESCRIPTION,description);
    }

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }
    public void setImage(ParseFile parseFile){
        put(KEY_IMAGE,parseFile);
    }


    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }
    public void setUser(ParseUser parseUser){
        put(KEY_USER, parseUser);
    }

    public Date getCreateAt(){
        return getDate(KEY_CREATED_AT);
    }

    public long getLikes(){return getLong(KEY_LIKES);}
    public void setLikes(Long likes){put(KEY_LIKES,likes);}
    public static Post getPost(String objectId) throws ParseException {
            ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
            // include data referred by user key
            query.include(Post.KEY_USER);
            query.whereEqualTo(Post.KEY_OBJECTID,objectId);
            // start an asynchronous call for posts
            query.findInBackground((posts, e) -> {
                if (e != null) {Log.e(TAG, "Issue with getting posts", e);}
            });
            return query.get(objectId);
    }

}
