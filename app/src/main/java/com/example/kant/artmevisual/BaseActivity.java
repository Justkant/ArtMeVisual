package com.example.kant.artmevisual;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kant.artmevisual.ArtmeAPI.ArtmeAPI;
import com.example.kant.artmevisual.ArtmeAPI.Group;
import com.example.kant.artmevisual.ArtmeAPI.User;
import com.google.gson.reflect.TypeToken;
import com.iainconnor.objectcache.CacheManager;
import com.iainconnor.objectcache.GetCallback;
import com.iainconnor.objectcache.PutCallback;
import com.makeramen.RoundedTransformationBuilder;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Quentin on 27/02/2015.
 * ArtMe Project.
 */
public abstract class BaseActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final int REQUEST_CODE = 100;
    private static final int UPDATE_INFOS_USER_CODE = 200;

    private CacheManager mCacheManager;

    // Navigation Drawer
    private DrawerLayout mDrawerLayout;

    // ActionBar Toolbar
    private Toolbar mActionBarToolbar;

    // ActionBar Drawer Toggle
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    // SwipeRefresh Layout
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    private ArrayList<Integer> mNavDrawerItems = new ArrayList<>();
    private ViewGroup mDrawerItemsListContainer;
    private View[] mNavDrawerItemViews = null;
    private Handler mHandler;

    protected static final int NAVDRAWER_ITEM_EXPLORE = 0;
    protected static final int NAVDRAWER_ITEM_MY_EVENTS = 1;
    protected static final int NAVDRAWER_ITEM_SEARCH = 2;
    protected static final int NAVDRAWER_ITEM_CREATE_GROUP = 3;
    protected static final int NAVDRAWER_ITEM_SETTINGS = 4;
    protected static final int NAVDRAWER_ITEM_INVALID = -1;
    protected static final int NAVDRAWER_ITEM_SEPARATOR = -2;

    // titles for navdrawer items (indices must correspond to the above)
    private static final int[] NAVDRAWER_TITLE_RES_ID = new int[]{
            R.string.navdrawer_item_explore,
            R.string.navdrawer_item_my_events,
            R.string.navdrawer_item_search,
            R.string.navdrawer_item_create_group,
            R.string.navdrawer_item_settings
    };

    // icons for navdrawer items (indices must correspond to above array)
    private static final int[] NAVDRAWER_ICON_RES_ID = new int[]{
            R.drawable.ic_drawer_explore,  // Explore
            R.drawable.ic_event_grey600_24dp,
            R.drawable.ic_drawer_search,
            R.drawable.ic_drawer_create_group,
            R.drawable.ic_drawer_settings
    };

    // delay to launch nav drawer item, to allow close animation to play
    private static final int NAVDRAWER_LAUNCH_DELAY = 250;

    // fade in and fade out durations for the main content when switching between
    // different Activities of the app through the Nav Drawer
    private static final int MAIN_CONTENT_FADEOUT_DURATION = 150;
    private static final int MAIN_CONTENT_FADEIN_DURATION = 250;

    private LinearLayout mGroupItemsListContainer;

    private ArtmeAPI mApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCacheManager = CacheManager.getInstance(((MyApplication) getApplicationContext()).getDiskCache());

        mHandler = new Handler();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getString(R.string.base_url))
                .build();
        mApi = restAdapter.create(ArtmeAPI.class);

        if (savedInstanceState == null)
            checkUserToken();
    }

    /**
     * Auto setup of the Nav Drawer & the Swipe Refresh
     *
     * @param savedInstanceState saveInstanceState not used
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        setupNavDrawer();
        trySetupSwipeRefresh();

        View mainContent = findViewById(R.id.main_content);
        if (mainContent != null) {
            mainContent.setAlpha(0);
            mainContent.animate().alpha(1).setDuration(MAIN_CONTENT_FADEIN_DURATION);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserToken();
    }

    protected void getUserInfo() {
        String token = MySharedPreferences.readToPreferences(this, getString(R.string.token_string), "");
        final Context context = this;
        if (token.length() != 0)
            mApi.userMe(token, new Callback<User>() {
                @Override
                public void success(final User user, Response response) {
                    mCacheManager.putAsync("me", user, new PutCallback() {
                        @Override
                        public void onSuccess() {
                            setupNavDrawerInfo();
                        }

                        @Override
                        public void onFailure(Exception e) {
                        }
                    });
                }

                @Override
                public void failure(RetrofitError error) {
                    SnackbarManager.show(
                            Snackbar.with(context)
                                    .type(SnackbarType.MULTI_LINE)
                                    .text("Impossible de récupérer les infos")
                                    .actionLabel("Se reconnecter")
                                    .actionListener(new ActionClickListener() {
                                        @Override
                                        public void onActionClicked(Snackbar snackbar) {
                                            startActivity(new Intent(context, LoginActivity.class));
                                            finish();
                                        }
                                    })
                    );
                }
            });
    }

    protected void checkUserToken() {
        if (MySharedPreferences.readToPreferences(this, getString(R.string.token_string), "").length() == 0) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    /**
     * Sets up the navigation drawer as appropriate.
     */
    private void setupNavDrawer() {
        int selfItem = getSelfNavDrawerItem();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout == null)
            return;

        if (selfItem == NAVDRAWER_ITEM_INVALID) {
            View navDrawer = mDrawerLayout.findViewById(R.id.navdrawer);
            if (navDrawer != null)
                ((ViewGroup) navDrawer.getParent()).removeView(navDrawer);
            mDrawerLayout = null;
            return;
        }

        if (mActionBarToolbar != null) {
            mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                    mActionBarToolbar, 0, 0);
            mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
            mDrawerLayout.post(new Runnable() {
                @Override
                public void run() {
                    mActionBarDrawerToggle.syncState();
                }
            });
        }

        setupNavDrawerInfo();
    }

    protected void setupNavDrawerInfo() {
        if (mDrawerLayout == null)
            return;
        Type userType = new TypeToken<User>() {
        }.getType();
        mCacheManager.getAsync("me", User.class, userType, new GetCallback() {
            @Override
            public void onSuccess(Object o) {
                if (o != null) {
                    User user = (User) o;
                    if (user.picture_url != null)
                        setNavDrawerImage(getString(R.string.base_url) + "/" + user.picture_url);
                    String complete_name = "";
                    if (user.first_name != null)
                        complete_name += user.first_name + " ";
                    if (user.last_name != null)
                        complete_name += user.last_name;
                    setNavDrawerUsername(complete_name, user.username);
                    findViewById(R.id.navdrawer_profile_info).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startProfileActivity();
                        }
                    });
                    populateNavDrawer(user.groups);
                }
            }

            @Override
            public void onFailure(Exception e) {
                //TODO: implement error handler
            }
        });
    }

    private void startProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivityForResult(intent, UPDATE_INFOS_USER_CODE);
        mDrawerLayout.closeDrawer(Gravity.START);
    }

    private void setNavDrawerUsername(String fullname, String username) {
        ((TextView) findViewById(R.id.navdrawer_fullname)).setText(fullname);
        ((TextView) findViewById(R.id.navdrawer_username)).setText(username);
    }

    private void setNavDrawerImage(String url) {
        Transformation transformation = new RoundedTransformationBuilder()
                .oval(true)
                .build();
        Picasso.with(this)
                .load(url)
                .fit()
                .transform(transformation)
                .into((ImageView) findViewById(R.id.navdrawer_profile_image));
    }

    private void populateNavDrawer(List<Group> groups) {
        mNavDrawerItems.clear();

        // Explore is always shown
        mNavDrawerItems.add(NAVDRAWER_ITEM_EXPLORE);
        mNavDrawerItems.add(NAVDRAWER_ITEM_MY_EVENTS);
        mNavDrawerItems.add(NAVDRAWER_ITEM_SEARCH);
        mNavDrawerItems.add(NAVDRAWER_ITEM_SEPARATOR);
        mNavDrawerItems.add(NAVDRAWER_ITEM_CREATE_GROUP);
        mNavDrawerItems.add(NAVDRAWER_ITEM_SEPARATOR);
        mNavDrawerItems.add(NAVDRAWER_ITEM_SETTINGS);

        createNavDrawerItems(groups);
    }

    private void createNavDrawerItems(List<Group> groups) {
        mDrawerItemsListContainer = (ViewGroup) findViewById(R.id.navdrawer_items_list);
        if (mDrawerItemsListContainer == null) {
            return;
        }

        mNavDrawerItemViews = new View[mNavDrawerItems.size()];
        mDrawerItemsListContainer.removeAllViews();
        int i = 0;
        for (int itemId : mNavDrawerItems) {
            if (itemId == NAVDRAWER_ITEM_CREATE_GROUP) {
                mGroupItemsListContainer = new LinearLayout(this);
                mGroupItemsListContainer.setOrientation(LinearLayout.VERTICAL);
                for (final Group group : groups) {
                    Log.d("BaseActivity", group.title);
                    View view = getLayoutInflater().inflate(R.layout.navdrawer_item, mGroupItemsListContainer, false);
                    ImageView iconView = (ImageView) view.findViewById(R.id.icon);
                    TextView titleView = (TextView) view.findViewById(R.id.title);

                    iconView.setVisibility(group.picture_url != null ? View.VISIBLE : View.INVISIBLE);
                    if (group.picture_url != null) {
                        Transformation transformation = new RoundedTransformationBuilder()
                                .oval(true)
                                .build();
                        Picasso.with(this)
                                .load(getString(R.string.base_url) + "/" + group.picture_url)
                                .fit()
                                .transform(transformation)
                                .into(iconView);
                    }
                    titleView.setText(group.title);
                    titleView.setTextColor(getResources().getColor(R.color.navdrawer_text_color));
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startGroup(group.id);
                        }
                    });
                    mGroupItemsListContainer.addView(view);
                }
                mDrawerItemsListContainer.addView(mGroupItemsListContainer);
            }
            mNavDrawerItemViews[i] = makeNavDrawerItem(itemId, mDrawerItemsListContainer);
            mDrawerItemsListContainer.addView(mNavDrawerItemViews[i]);
            ++i;
        }
    }

    private void startGroup(int id) {
        Intent intent = new Intent(this, GroupActivity.class);
        intent.putExtra("group_id", id);
        startActivity(intent);
        mDrawerLayout.closeDrawer(Gravity.START);
    }

    private View makeNavDrawerItem(final int itemId, ViewGroup container) {
        boolean selected = getSelfNavDrawerItem() == itemId;

        int layoutToInflate;
        if (itemId == NAVDRAWER_ITEM_SEPARATOR) {
            layoutToInflate = R.layout.navdrawer_separator;
        } else {
            layoutToInflate = R.layout.navdrawer_item;
        }
        View view = getLayoutInflater().inflate(layoutToInflate, container, false);

        if (itemId == NAVDRAWER_ITEM_SEPARATOR) {
            // we are done
            UIUtils.setAccessibilityIgnore(view);
            return view;
        }

        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        TextView titleView = (TextView) view.findViewById(R.id.title);
        int iconId = itemId >= 0 && itemId < NAVDRAWER_ICON_RES_ID.length ?
                NAVDRAWER_ICON_RES_ID[itemId] : 0;
        int titleId = itemId >= 0 && itemId < NAVDRAWER_TITLE_RES_ID.length ?
                NAVDRAWER_TITLE_RES_ID[itemId] : 0;

        // set icon and text
        iconView.setVisibility(iconId > 0 ? View.VISIBLE : View.GONE);
        if (iconId > 0) {
            iconView.setImageResource(iconId);
        }
        titleView.setText(getString(titleId));

        formatNavDrawerItem(view, itemId, selected);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavDrawerItemClicked(itemId);
            }
        });

        return view;
    }

    private void onNavDrawerItemClicked(final int itemId) {
        if (itemId == getSelfNavDrawerItem()) {
            mDrawerLayout.closeDrawer(Gravity.START);
            return;
        }

        if (itemId == NAVDRAWER_ITEM_SETTINGS || itemId == NAVDRAWER_ITEM_CREATE_GROUP) {
            goToNavDrawerItem(itemId);
        } else {
            // launch the target Activity after a short delay, to allow the close animation to play
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToNavDrawerItem(itemId);
                }
            }, NAVDRAWER_LAUNCH_DELAY);

            // change the active item on the list so the user can see the item changed
            setSelectedNavDrawerItem(itemId);
            // fade out the main content
            View mainContent = findViewById(R.id.main_content);
            if (mainContent != null) {
                mainContent.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
            }
        }

        mDrawerLayout.closeDrawer(Gravity.START);
    }

    private void goToNavDrawerItem(int itemId) {
        Intent intent;
        switch (itemId) {
            case NAVDRAWER_ITEM_EXPLORE:
                intent = new Intent(this, BrowseEventsActivity.class);
                startActivity(intent);
                finish();
                break;
            case NAVDRAWER_ITEM_MY_EVENTS:
                intent = new Intent(this, MyEventsActivity.class);
                startActivity(intent);
                finish();
                break;
            case NAVDRAWER_ITEM_SEARCH:
                intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                finish();
                break;
            case NAVDRAWER_ITEM_CREATE_GROUP:
                intent = new Intent(this, CreateGroupActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case NAVDRAWER_ITEM_SETTINGS:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            getUserInfo();
        }
        if (requestCode == UPDATE_INFOS_USER_CODE && resultCode == RESULT_OK) {
            setupNavDrawerInfo();
        }
    }

    /**
     * Sets up the given navdrawer item's appearance to the selected state. Note: this could
     * also be accomplished (perhaps more cleanly) with state-based layouts.
     */
    private void setSelectedNavDrawerItem(int itemId) {
        if (mNavDrawerItemViews != null) {
            for (int i = 0; i < mNavDrawerItemViews.length; i++) {
                if (i < mNavDrawerItems.size()) {
                    int thisItemId = mNavDrawerItems.get(i);
                    formatNavDrawerItem(mNavDrawerItemViews[i], thisItemId, itemId == thisItemId);
                }
            }
        }
    }

    private void formatNavDrawerItem(View view, int itemId, boolean selected) {
        if (itemId == NAVDRAWER_ITEM_SEPARATOR) {
            // not applicable
            return;
        }

        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        TextView titleView = (TextView) view.findViewById(R.id.title);

        if (selected) {
            view.setBackgroundResource(R.drawable.selected_navdrawer_item_background);
        }

        // configure its appearance according to whether or not it's selected
        titleView.setTextColor(selected ?
                getResources().getColor(R.color.navdrawer_text_color_selected) :
                getResources().getColor(R.color.navdrawer_text_color));
        iconView.setColorFilter(selected ?
                getResources().getColor(R.color.navdrawer_icon_tint_selected) :
                getResources().getColor(R.color.navdrawer_icon_tint));
    }

    /**
     * Try sets up the Swipe Refresh as appropriate.
     */
    private void trySetupSwipeRefresh() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        if (mSwipeRefreshLayout == null)
            return;
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.primary,
                R.color.accent,
                R.color.primary_dark);
    }

    /**
     * Returns the navigation drawer item that corresponds to this Activity. Subclasses
     * of BaseActivity override this to indicate what nav drawer item corresponds to them
     * Return NAVDRAWER_ITEM_INVALID to mean that this Activity should not have a Nav Drawer.
     */
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_INVALID;
    }

    /**
     * Getter for the ActionBar Toolbar
     *
     * @return Toolbar
     */
    protected Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
            if (mActionBarToolbar != null) {
                setSupportActionBar(mActionBarToolbar);
            }
        }
        return mActionBarToolbar;
    }

    /**
     * Override to auto set the ActionBar Toolbar if exist in the view
     *
     * @param layoutResID
     */
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getActionBarToolbar();
    }

    /**
     * Tell if the Nav Drawer is open or not
     *
     * @return Boolean
     */
    protected boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(Gravity.START);
    }

    /**
     * Close the Nav Drawer
     */
    protected void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(Gravity.START);
        }
    }

    /**
     * Override to be able to close the Nav Drawer on back pressed by user
     */
    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        } else {
            super.onBackPressed();
        }
    }

    protected int getScreenHeight() {
        return findViewById(R.id.main_content).getHeight();
    }

    protected int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }
}
