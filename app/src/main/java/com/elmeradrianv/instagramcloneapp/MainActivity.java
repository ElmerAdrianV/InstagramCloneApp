package com.elmeradrianv.instagramcloneapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    ParseUser currentUser;
    EditText etDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Button btnCaptureImage;
        ImageView ivPostImage;
        Button btnSubmit;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getMenuInflater();
        etDescription=findViewById(R.id.etDescription);
        btnCaptureImage=findViewById(R.id.btnCaptureImage);
        ivPostImage=findViewById(R.id.ivPostImage);
        btnSubmit=findViewById(R.id.btnSubmit);
        currentUser = ParseUser.getCurrentUser();
        queryPost();
        btnSubmit.setOnClickListener(v -> {
            String description=etDescription.getText().toString();
            if(description.isEmpty()){
                Toast.makeText(MainActivity.this,"Please, fill the description", Toast.LENGTH_LONG)
                        .show();
            }
            else{
                ParseUser user = ParseUser.getCurrentUser();
                savePost(description,currentUser);
            }
        });

    }

    private void queryPost() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.findInBackground((posts, e) -> {
            if(e!=null){
                Log.e(TAG, "done: something wrong with the query",e );
                return;
            }
            for (Post post:posts){
                Log.d(TAG, "post: "+post.getDescription()+" username:"+post.getUser().getUsername());
            }
        });
    }
    private void savePost(String description, ParseUser currentUser) {
        Post post = new Post();
        post.setDescription(description);
        //post.setImage();
        post.setUser(currentUser);
        post.saveInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "error while saving: ",e );
                Toast.makeText(MainActivity.this,"error while saving", Toast.LENGTH_SHORT)
                        .show();
            }
            Log.i(TAG, "Post save successful!");
            etDescription.setText("");
        });
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