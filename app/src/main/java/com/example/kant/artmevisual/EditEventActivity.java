package com.example.kant.artmevisual;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.kant.artmevisual.ArtmeAPI.ArtmeAPI;
import com.example.kant.artmevisual.ArtmeAPI.Event;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class EditEventActivity extends ActionBarActivity {

    private int event_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        event_id = getIntent().getIntExtra("event_id", 0);
        if (event_id == 0) {
            finish();
        }

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getString(R.string.base_url))
                .build();
        ArtmeAPI mApi = restAdapter.create(ArtmeAPI.class);

        mApi.getEventById(event_id,
                MySharedPreferences.readToPreferences(this, getString(R.string.token_string), ""),
                new Callback<Event>() {
                    @Override
                    public void success(Event event, Response response) {
                        // TODO : set info that exist
                        /*if (!event.can_edit)
                            mFab.setVisibility(View.GONE);
                        Picasso.with(mContext)
                                .load(getString(R.string.base_url) + "/" + event.picture_url)
                                .centerCrop()
                                .fit()
                                .into(mEventImg);
                        mToolbar.setTitle(event.title);
                        setSupportActionBar(mToolbar);
                        mPlace.setText(event.adress);
                        mDesc.setText(event.description);
                        insertUsers(event.users);
                        insertPhotos(event.photos);*/
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_event, menu);
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
