package com.example.kant.artmevisual;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kant.artmevisual.ArtmeAPI.Event;
import com.example.kant.artmevisual.ArtmeAPI.Group;
import com.example.kant.artmevisual.ArtmeAPI.User;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.gson.reflect.TypeToken;
import com.iainconnor.objectcache.CacheManager;
import com.iainconnor.objectcache.GetCallback;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.List;


public class ProfileActivity extends BaseActivity {

    private Context mContext;
    private LayoutInflater mInflater;
    private CacheManager mCacheManager;

    private Toolbar mToolbar;
    private FloatingActionButton mFab;

    private ImageView mProfileImg;
    private TextView mName;
    private TextView mEmail;
    private TextView mDesc;

    private LinearLayout mUserGroups;
    private LinearLayout mUserEvents;
    private LinearLayout mUserSubEvents;
    private LinearLayout mUserPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mContext = this;

        mInflater = LayoutInflater.from(this);

        mCacheManager = CacheManager.getInstance(((MyApplication) getApplicationContext()).getDiskCache());

        mToolbar = getActionBarToolbar();

        mFab = (FloatingActionButton) findViewById(R.id.edit_btn);

        mProfileImg = (ImageView) findViewById(R.id.profile_img);
        mName = (TextView) findViewById(R.id.user_completename);
        mEmail = (TextView) findViewById(R.id.user_email);
        mDesc = (TextView) findViewById(R.id.user_desc);

        mUserGroups = (LinearLayout) findViewById(R.id.user_groups);
        mUserEvents = (LinearLayout) findViewById(R.id.user_events);
        mUserSubEvents = (LinearLayout) findViewById(R.id.user_sub_events);
        mUserPhotos = (LinearLayout) findViewById(R.id.user_photos);

        Type userType = new TypeToken<User>() {
        }.getType();
        mCacheManager.getAsync("me", User.class, userType, new GetCallback() {
            @Override
            public void onSuccess(Object o) {
                if (o != null) {
                    User user = (User) o;
                    Picasso.with(mContext)
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
                    insertPhotos(user.photos);
                }
            }

            @Override
            public void onFailure(Exception e) {
                //TODO: implement error handler
            }
        });

    }

    private void insertPhotos(List<String> photos) {
        FrameLayout view;
        for (String photo : photos) {
            view = (FrameLayout) mInflater.inflate(R.layout.horizontal_item, mUserPhotos, false);
            Picasso.with(mContext).load(getString(R.string.base_url) + "/" + photo)
                    .placeholder(R.drawable.placeholders)
                    .resize(200, 200)
                    .centerCrop()
                    .into((ImageView) view.findViewById(R.id.item_image));
            view.removeView(view.findViewById(R.id.title_text));
            mUserPhotos.addView(view);
        }
    }

    private void insertSubEvents(List<Event> sub_events) {
        View view;
        for (final Event event : sub_events) {
            view = mInflater.inflate(R.layout.horizontal_item, mUserSubEvents, false);
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
            mUserSubEvents.addView(view);
        }
    }

    private void insertEvents(List<Event> past_events, List<Event> next_events) {
        View view;
        for (final Event event : past_events) {
            view = mInflater.inflate(R.layout.horizontal_item, mUserEvents, false);
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
            mUserEvents.addView(view);
        }
        for (final Event event : next_events) {
            view = mInflater.inflate(R.layout.horizontal_item, mUserEvents, false);
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
            mUserEvents.addView(view);
        }
    }

    private void insertGroups(List<Group> groups) {
        View view;
        for (final Group group : groups) {
            view = mInflater.inflate(R.layout.horizontal_item, mUserGroups, false);
            Picasso.with(mContext).load(getString(R.string.base_url) + "/" + group.picture_url)
                    .placeholder(R.drawable.placeholders)
                    .resize(200, 200)
                    .centerCrop()
                    .into((ImageView) view.findViewById(R.id.item_image));
            ((TextView) view.findViewById(R.id.title_text)).setText(group.title);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, GroupActivity.class);
                    intent.putExtra("group_id", group.id);
                    startActivity(intent);
                    finish();
                }
            });
            mUserGroups.addView(view);
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
