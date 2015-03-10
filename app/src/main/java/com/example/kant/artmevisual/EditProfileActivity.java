package com.example.kant.artmevisual;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.kant.artmevisual.ArtmeAPI.User;
import com.google.gson.reflect.TypeToken;
import com.iainconnor.objectcache.CacheManager;
import com.iainconnor.objectcache.GetCallback;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;


public class EditProfileActivity extends ActionBarActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mContext = this;

        CacheManager mCacheManager = CacheManager.getInstance(((MyApplication) getApplicationContext()).getDiskCache());

        Type userType = new TypeToken<User>() {
        }.getType();
        mCacheManager.getAsync("me", User.class, userType, new GetCallback() {
            @Override
            public void onSuccess(Object o) {
                if (o != null) {
                    User user = (User) o;
                    // TODO : set info that exist
                    /*Picasso.with(mContext)
                            .load(getString(R.string.base_url) + "/" + user.picture_url)
                            .centerCrop()
                            .fit()
                            .into(mProfileImg);
                    mToolbar.setTitle(user.username);
                    setSupportActionBar(mToolbar);
                    mName.setText(user.first_name + " " + user.last_name);
                    mEmail.setText(user.email);
                    mDesc.setText(user.description);
                    insertGroups(user.groups);
                    insertEvents(user.past_events, user.next_events);
                    insertSubEvents(user.sub_events);
                    insertPhotos(user.photos);*/
                }
            }

            @Override
            public void onFailure(Exception e) {
                //TODO: implement error handler
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
