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

import com.example.kant.artmevisual.ArtmeAPI.ArtmeAPI;
import com.example.kant.artmevisual.ArtmeAPI.Event;
import com.example.kant.artmevisual.ArtmeAPI.Group;
import com.example.kant.artmevisual.ArtmeAPI.User;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.iainconnor.objectcache.CacheManager;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GroupActivity extends BaseActivity {

    private Context mContext;
    private LayoutInflater mInflater;
    private CacheManager mCacheManager;

    private int group_id;

    private ArtmeAPI mApi;

    private Toolbar mToolbar;
    private FloatingActionButton mFab;

    private ImageView mGroupImg;
    private TextView mPlace;
    private TextView mDesc;

    private LinearLayout mGroupUsers;
    private LinearLayout mGroupEvents;
    private LinearLayout mGroupPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        group_id = getIntent().getIntExtra("group_id", 0);
        if (group_id == 0) {
            //TODO: Snackbar to show error.
            finish();
        }

        mContext = this;

        mInflater = LayoutInflater.from(this);

        mCacheManager = CacheManager.getInstance(((MyApplication) getApplicationContext()).getDiskCache());

        mToolbar = getActionBarToolbar();

        mFab = (FloatingActionButton) findViewById(R.id.edit_btn);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditGroupActivity.class);
                intent.putExtra("group_id", group_id);
                startActivity(intent);
            }
        });

        mGroupImg = (ImageView) findViewById(R.id.group_img);
        mPlace = (TextView) findViewById(R.id.group_address);
        mDesc = (TextView) findViewById(R.id.group_desc);

        mGroupUsers = (LinearLayout) findViewById(R.id.group_users);
        mGroupEvents = (LinearLayout) findViewById(R.id.group_events);
        mGroupPhotos = (LinearLayout) findViewById(R.id.group_photos);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getString(R.string.base_url))
                .build();
        mApi = restAdapter.create(ArtmeAPI.class);

        mApi.getGroupById(group_id,
                MySharedPreferences.readToPreferences(this, getString(R.string.token_string), ""),
                new Callback<Group>() {
                    @Override
                    public void success(Group group, Response response) {
                        if (!group.can_edit) {
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
                        insertPhotos(group.photos);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                    }
                });
    }

    private void insertPhotos(List<String> photos) {
        mGroupPhotos.removeAllViews();
        FrameLayout view;
        for (String photo : photos) {
            view = (FrameLayout) mInflater.inflate(R.layout.horizontal_item, mGroupPhotos, false);
            Picasso.with(mContext).load(getString(R.string.base_url) + "/" + photo)
                    .placeholder(R.drawable.placeholders)
                    .resize(200, 200)
                    .centerCrop()
                    .into((ImageView) view.findViewById(R.id.item_image));
            view.removeView(view.findViewById(R.id.title_text));
            mGroupPhotos.addView(view);
        }
    }

    private void insertEvents(List<Event> past_events, List<Event> next_events) {
        mGroupEvents.removeAllViews();
        View view;
        for (final Event event : past_events) {
            view = mInflater.inflate(R.layout.horizontal_item, mGroupEvents, false);
            Picasso.with(mContext).load(getString(R.string.base_url) + "/" + event.picture_url)
                    .placeholder(R.drawable.placeholders)
                    .resize(200, 200)
                    .centerCrop()
                    .into((ImageView) view.findViewById(R.id.item_image));
            ((TextView) view.findViewById(R.id.title_text)).setText(event.title);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, EventActivity.class);
                    intent.putExtra("event_id", event.id);
                    startActivity(intent);
                    finish();
                }
            });
            mGroupEvents.addView(view);
        }
        for (final Event event : next_events) {
            view = mInflater.inflate(R.layout.horizontal_item, mGroupEvents, false);
            Picasso.with(mContext).load(getString(R.string.base_url) + "/" + event.picture_url)
                    .placeholder(R.drawable.placeholders)
                    .resize(200, 200)
                    .centerCrop()
                    .into((ImageView) view.findViewById(R.id.item_image));
            ((TextView) view.findViewById(R.id.title_text)).setText(event.title);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, EventActivity.class);
                    intent.putExtra("event_id", event.id);
                    startActivity(intent);
                    finish();
                }
            });
            mGroupEvents.addView(view);
        }
    }

    private void insertUsers(List<User> users) {
        mGroupUsers.removeAllViews();
        View view;
        for (final User user : users) {
            view = mInflater.inflate(R.layout.horizontal_item, mGroupUsers, false);
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
                }
            });
            mGroupUsers.addView(view);
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
