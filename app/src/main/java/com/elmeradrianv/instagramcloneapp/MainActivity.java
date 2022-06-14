package com.elmeradrianv.instagramcloneapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseUser;


public class   MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    ParseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getMenuInflater();
        currentUser=ParseUser.getCurrentUser();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.btnLogout:
                ParseUser.logOut();
                currentUser = ParseUser.getCurrentUser(); // this will now be null
                //Log.d(TAG, "onOptionsItemSelected: "+currentUser);
                Intent i = new Intent(this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // this makes sure the Back button won't work
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // same as above
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}