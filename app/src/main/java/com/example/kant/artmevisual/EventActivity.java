package com.example.kant.artmevisual;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kant.artmevisual.ArtmeAPI.ArtmeAPI;
import com.example.kant.artmevisual.ArtmeAPI.Event;
import com.example.kant.artmevisual.ArtmeAPI.User;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.iainconnor.objectcache.CacheManager;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class EventActivity extends BaseActivity {

    private Context mContext;
    private LayoutInflater mInflater;
    private CacheManager mCacheManager;

    private int event_id;

    private ArtmeAPI mApi;

    private Toolbar mToolbar;
    private FloatingActionButton mFab;

    private ImageView mEventImg;
    private TextView mPlace;
    private TextView mDesc;

    private LinearLayout mEventUsers;
    private LinearLayout mEventPhotos;
    private int EVENT_EDIT_CODE = 10;

    private FloatingActionButton mSubFab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        event_id = getIntent().getIntExtra("event_id", 0);
        if (event_id == 0) {
            //TODO: Snackbar to show error.
            finish();
        }

        mContext = this;

        mInflater = LayoutInflater.from(this);

        mCacheManager = CacheManager.getInstance(((MyApplication) getApplicationContext()).getDiskCache());

        mToolbar = getActionBarToolbar();

        mFab = (FloatingActionButton) findViewById(R.id.edit_btn);
        mFab.setVisibility(View.GONE);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditEventActivity.class);
                intent.putExtra("event_id", event_id);
                startActivityForResult(intent, EVENT_EDIT_CODE);
            }
        });

        mEventImg = (ImageView) findViewById(R.id.event_img);
        mPlace = (TextView) findViewById(R.id.event_address);
        mDesc = (TextView) findViewById(R.id.event_desc);

        mEventUsers = (LinearLayout) findViewById(R.id.event_users);
        mEventPhotos = (LinearLayout) findViewById(R.id.event_photos);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getString(R.string.base_url))
                .build();
        mApi = restAdapter.create(ArtmeAPI.class);
        updateInfosEvent();

        mSubFab = (FloatingActionButton) findViewById(R.id.sub_btn);
        mSubFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApi.subEvent(event_id,
                        MySharedPreferences.readToPreferences(mContext, getString(R.string.token_string), ""),
                        new Callback<Event>() {
                            @Override
                            public void success(Event event, Response response) {
                                updateInfosEvent();
                            }

                            @Override
                            public void failure(RetrofitError error) {

                            }
                        });
            }
        });
    }

    public void updateInfosEvent() {
        mApi.getEventById(event_id,
                MySharedPreferences.readToPreferences(this, getString(R.string.token_string), ""),
                new Callback<Event>() {
                    @Override
                    public void success(Event event, Response response) {
                        if (event.can_edit)
                            mFab.setVisibility(View.VISIBLE);
                        if (event.is_sub)
                            mSubFab.setVisibility(View.GONE);
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
                        insertPhotos(event.photos);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        finish();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EVENT_EDIT_CODE) {
            if (resultCode == RESULT_OK) {

                boolean deleted = false;
                if (data != null) {
                    deleted = data.getBooleanExtra("delete", false);
                }
                if (deleted == true)
                    finish();
                updateInfosEvent();
            }
        }

    }

    private void insertPhotos(List<String> photos) {
        mEventPhotos.removeAllViews();
        FrameLayout view;
        for (String photo : photos) {
            view = (FrameLayout) mInflater.inflate(R.layout.horizontal_item, mEventPhotos, false);
            Picasso.with(mContext).load(getString(R.string.base_url) + "/" + photo)
                    .placeholder(R.drawable.placeholders)
                    .resize(200, 200)
                    .centerCrop()
                    .into((ImageView) view.findViewById(R.id.item_image));
            view.removeView(view.findViewById(R.id.title_text));
            mEventPhotos.addView(view);
        }
    }

    private void insertUsers(List<User> users) {
        mEventUsers.removeAllViews();
        View view;
        for (final User user : users) {
            view = mInflater.inflate(R.layout.horizontal_item, mEventUsers, false);
            Picasso.with(mContext).load(getString(R.string.base_url) + "/" + user.picture_url)
                    .placeholder(R.drawable.placeholders)
                    .resize(200, 200)
                    .centerCrop()
                    .into((ImageView) view.findViewById(R.id.item_image));
            ((TextView) view.findViewById(R.id.title_text)).setText(user.username);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, UserActivity.class);
                    intent.putExtra("user_id", user.id);
                    startActivity(intent);
                    finish();
                    // TODO : tu pourra retirer les finish sur la nav des users/groups/events
                }
            });
            mEventUsers.addView(view);
        }
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

    @Override
    public void onRefresh() {

    }
}
