package com.example.kant.artmevisual;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.kant.artmevisual.ArtmeAPI.ArtmeAPI;
import com.example.kant.artmevisual.ArtmeAPI.Group;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class EditGroupActivity extends ActionBarActivity {

    private int group_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        group_id = getIntent().getIntExtra("group_id", 0);
        if (group_id == 0) {
            finish();
        }

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getString(R.string.base_url))
                .build();
        ArtmeAPI mApi = restAdapter.create(ArtmeAPI.class);

        mApi.getGroupById(group_id,
                MySharedPreferences.readToPreferences(this, getString(R.string.token_string), ""),
                new Callback<Group>() {
                    @Override
                    public void success(Group group, Response response) {
                        // TODO : set info that exist
                        /*if (!group.can_edit) {
                            mFab.setVisibility(View.GONE);
                        }
                        Picasso.with(mContext)
                                .load(getString(R.string.base_url) + "/" + group.picture_url)
                                .centerCrop()
                                .fit()
                                .into(mGroupImg);
                        mToolbar.setTitle(group.title);
                        setSupportActionBar(mToolbar);
                        mPlace.setText(group.adress);
                        mDesc.setText(group.description);
                        insertUsers(group.users);
                        insertEvents(group.past_events, group.next_events);
                        insertPhotos(group.photos);*/
                    }

                    @Override
                    public void failure(RetrofitError error) {
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_group, menu);
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
