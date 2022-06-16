package com.elmeradrianv.instagramcloneapp.database;

import android.app.Application;

import com.elmeradrianv.instagramcloneapp.BuildConfig;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

   private static final String applicationId= BuildConfig.APP_ID;
   private static final String clientKey = BuildConfig.CLIENT_KEY;
   private static final String server = "https://parseapi.back4app.com/";
    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Post.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(applicationId)
                .clientKey(clientKey)
                .server(server).build());

    }
}
