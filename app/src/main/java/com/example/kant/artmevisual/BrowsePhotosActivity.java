package com.example.kant.artmevisual;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;

import com.example.kant.artmevisual.ArtmeAPI.ArtmeAPI;
import com.example.kant.artmevisual.ArtmeAPI.Event;
import com.example.kant.artmevisual.ArtmeAPI.Group;
import com.example.kant.artmevisual.ArtmeAPI.User;
import com.github.ksoichiro.android.observablescrollview.CacheFragmentStatePagerAdapter;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.github.ksoichiro.android.observablescrollview.TouchInterceptionFrameLayout;
import com.google.gson.reflect.TypeToken;
import com.iainconnor.objectcache.CacheManager;
import com.iainconnor.objectcache.GetCallback;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;
import retrofit.mime.TypedFile;


public class BrowsePhotosActivity extends BaseActivity implements ObservableScrollViewCallbacks {

    private static final String USER = "users";
    private static final String EVENT = "events";
    private static final String GROUP = "groups";

    private static final int SELECT_IMAGE = 100;

    private Context mContext;

    private View mToolbarView;
    private TouchInterceptionFrameLayout mInterceptionLayout;
    private ViewPager mPager;
    private NavigationAdapter mPagerAdapter;
    private int mSlop;
    private boolean mScrolled;
    private ScrollState mLastScrollState;

    private final List<Page> pages = new ArrayList<>();
    private ArtmeAPI mApi;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_photos);

        mContext = this;

        ViewCompat.setElevation(findViewById(R.id.header), getResources().getDimension(R.dimen.toolbar_elevation));
        mToolbarView = findViewById(R.id.toolbar_actionbar);
        mPagerAdapter = new NavigationAdapter(getSupportFragmentManager(), pages);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        // Padding for ViewPager must be set outside the ViewPager itself
        // because with padding, EdgeEffect of ViewPager become strange.
        final int tabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
        findViewById(R.id.pager_wrapper).setPadding(0, getActionBarSize() + tabHeight, 0, 0);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(mPager);

        ViewConfiguration vc = ViewConfiguration.get(this);
        mSlop = vc.getScaledTouchSlop();
        mInterceptionLayout = (TouchInterceptionFrameLayout) findViewById(R.id.main_content);
        mInterceptionLayout.setScrollInterceptionListener(mInterceptionListener);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getString(R.string.base_url))
                .build();
        mApi = restAdapter.create(ArtmeAPI.class);

        if (getIntent().getBooleanExtra("me", false)) {
            CacheManager mCacheManager = CacheManager.getInstance(((MyApplication) getApplicationContext()).getDiskCache());
            Type userType = new TypeToken<User>() {
            }.getType();
            mCacheManager.getAsync("me", User.class, userType, new GetCallback() {

                @Override
                public void onSuccess(Object o) {
                    if (o != null) {
                        user = (User) o;
                        pages.add(new Page(user.id, "Vos photos", USER, user.photos));
                        mPagerAdapter.setPages(pages);
                        ((SlidingTabLayout) findViewById(R.id.sliding_tabs)).setViewPager(mPager);
                        for (Group group : user.groups) {
                            mApi.getGroupById(group.id,
                                    MySharedPreferences.readToPreferences(mContext, getString(R.string.token_string), ""),
                                    new Callback<Group>() {
                                        @Override
                                        public void success(Group group, Response response) {
                                            pages.add(new Page(group.id, group.title, GROUP, group.photos));
                                            mPagerAdapter.setPages(pages);
                                            ((SlidingTabLayout) findViewById(R.id.sliding_tabs)).setViewPager(mPager);
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {

                                        }
                                    });
                        }
                    }
                }

                @Override
                public void onFailure(Exception e) {

                }
            });
        }

        int user_id = getIntent().getIntExtra("user", 0);
        if (user_id != 0) {
            mApi.getUserById(user_id, new Callback<User>() {
                @Override
                public void success(User user, Response response) {
                    pages.add(new Page(user.id, user.username, USER, user.photos));
                    mPagerAdapter.setPages(pages);
                    ((SlidingTabLayout) findViewById(R.id.sliding_tabs)).setViewPager(mPager);
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }
        int event_id = getIntent().getIntExtra("event", 0);
        if (event_id != 0) {
            mApi.getEventById(event_id,
                    MySharedPreferences.readToPreferences(this, getString(R.string.token_string), ""),
                    new Callback<Event>() {

                        @Override
                        public void success(Event event, Response response) {
                            pages.add(new Page(event.id, event.title, EVENT, event.photos));
                            mPagerAdapter.setPages(pages);
                            ((SlidingTabLayout) findViewById(R.id.sliding_tabs)).setViewPager(mPager);
                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
        }
        int group_id = getIntent().getIntExtra("group", 0);
        if (group_id != 0) {
            mApi.getGroupById(group_id,
                    MySharedPreferences.readToPreferences(this, getString(R.string.token_string), ""),
                    new Callback<Group>() {
                        @Override
                        public void success(Group group, Response response) {
                            pages.add(new Page(group.id, group.title, GROUP, group.photos));
                            mPagerAdapter.setPages(pages);
                            ((SlidingTabLayout) findViewById(R.id.sliding_tabs)).setViewPager(mPager);
                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_browse_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (id == R.id.add_photo) {
            Intent i = new Intent(
                    Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, SELECT_IMAGE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            File file = new File(picturePath);
            Page page = pages.get(mPager.getCurrentItem());
            postPhoto(page, new TypedFile(getMimeType(picturePath), file));
        }
    }

    private String getMimeType(String url) {
        String type = "";
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    private void postPhoto(final Page page, TypedFile file) {
        mApi.postPhoto(page.type, page.id, MySharedPreferences.readToPreferences(this, getString(R.string.token_string), ""), file, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                page.photos.add(s);
                mPagerAdapter.setPages(pages);
            }

            @Override
            public void failure(RetrofitError error) {
                SnackbarManager.show(
                        Snackbar.with(mContext)
                                .type(SnackbarType.MULTI_LINE)
                                .text("Impossible d'envoyer la photo")
                );
            }
        });

    }

    @Override
    public void onScrollChanged(int i, boolean b, boolean b2) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        if (!mScrolled) {
            // This event can be used only when TouchInterceptionFrameLayout
            // doesn't handle the consecutive events.
            adjustToolbar(scrollState);
        }
    }

    private TouchInterceptionFrameLayout.TouchInterceptionListener mInterceptionListener = new TouchInterceptionFrameLayout.TouchInterceptionListener() {
        @Override
        public boolean shouldInterceptTouchEvent(MotionEvent ev, boolean moving, float diffX, float diffY) {
            if (!mScrolled && mSlop < Math.abs(diffX) && Math.abs(diffY) < Math.abs(diffX)) {
                // Horizontal scroll is maybe handled by ViewPager
                return false;
            }

            Scrollable scrollable = getCurrentScrollable();
            if (scrollable == null) {
                mScrolled = false;
                return false;
            }

            // If interceptionLayout can move, it should intercept.
            // And once it begins to move, horizontal scroll shouldn't work any longer.
            int toolbarHeight = mToolbarView.getHeight();
            int translationY = (int) ViewHelper.getTranslationY(mInterceptionLayout);
            boolean scrollingUp = 0 < diffY;
            boolean scrollingDown = diffY < 0;
            if (scrollingUp) {
                if (translationY < 0) {
                    mScrolled = true;
                    mLastScrollState = ScrollState.UP;
                    return true;
                }
            } else if (scrollingDown) {
                if (-toolbarHeight < translationY) {
                    mScrolled = true;
                    mLastScrollState = ScrollState.DOWN;
                    return true;
                }
            }
            mScrolled = false;
            return false;
        }

        @Override
        public void onDownMotionEvent(MotionEvent ev) {
        }

        @Override
        public void onMoveMotionEvent(MotionEvent ev, float diffX, float diffY) {
            float translationY = ScrollUtils.getFloat(ViewHelper.getTranslationY(mInterceptionLayout) + diffY, -mToolbarView.getHeight(), 0);
            ViewHelper.setTranslationY(mInterceptionLayout, translationY);
            if (translationY < 0) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mInterceptionLayout.getLayoutParams();
                lp.height = (int) (-translationY + getScreenHeight());
                mInterceptionLayout.requestLayout();
            }
        }

        @Override
        public void onUpOrCancelMotionEvent(MotionEvent ev) {
            mScrolled = false;
            adjustToolbar(mLastScrollState);
        }
    };

    private void adjustToolbar(ScrollState scrollState) {
        int toolbarHeight = mToolbarView.getHeight();
        final Scrollable scrollable = getCurrentScrollable();
        if (scrollable == null) {
            return;
        }
        int scrollY = scrollable.getCurrentScrollY();
        if (scrollState == ScrollState.DOWN) {
            showToolbar();
        } else if (scrollState == ScrollState.UP) {
            if (toolbarHeight <= scrollY) {
                hideToolbar();
            } else {
                showToolbar();
            }
        } else if (!toolbarIsShown() && !toolbarIsHidden()) {
            // Toolbar is moving but doesn't know which to move:
            // you can change this to hideToolbar()
            showToolbar();
        }
    }

    private Scrollable getCurrentScrollable() {
        Fragment fragment = getCurrentFragment();
        if (fragment == null) {
            return null;
        }
        View view = fragment.getView();
        if (view == null) {
            return null;
        }
        return (Scrollable) view.findViewById(R.id.scroll);
    }

    private Fragment getCurrentFragment() {
        return mPagerAdapter.getItemAt(mPager.getCurrentItem());
    }

    private boolean toolbarIsShown() {
        return ViewHelper.getTranslationY(mInterceptionLayout) == 0;
    }

    private boolean toolbarIsHidden() {
        return ViewHelper.getTranslationY(mInterceptionLayout) == -mToolbarView.getHeight();
    }

    private void showToolbar() {
        animateToolbar(0);
    }

    private void hideToolbar() {
        animateToolbar(-mToolbarView.getHeight());
    }

    private void animateToolbar(final float toY) {
        float layoutTranslationY = ViewHelper.getTranslationY(mInterceptionLayout);
        if (layoutTranslationY != toY) {
            ValueAnimator animator = ValueAnimator.ofFloat(ViewHelper.getTranslationY(mInterceptionLayout), toY).setDuration(200);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float translationY = (float) animation.getAnimatedValue();
                    ViewHelper.setTranslationY(mInterceptionLayout, translationY);
                    if (translationY < 0) {
                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mInterceptionLayout.getLayoutParams();
                        lp.height = (int) (-translationY + getScreenHeight());
                        mInterceptionLayout.requestLayout();
                    }
                }
            });
            animator.start();
        }
    }

    @Override
    public void onRefresh() {

    }

    private static class NavigationAdapter extends CacheFragmentStatePagerAdapter {

        private List<Page> pages;

        public NavigationAdapter(FragmentManager fm, List<Page> pages) {
            super(fm);
            this.pages = pages;
        }

        public void setPages(List<Page> pages) {
            this.pages = pages;
            notifyDataSetChanged();
        }

        @Override
        protected Fragment createItem(int i) {
            return PhotosGridFragment.newInstance(pages.get(i).photos);
        }

        @Override
        public int getCount() {
            return pages.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pages.get(position).name;
        }
    }

    private static class Page {

        public int id;
        public String name;
        public String type;
        public List<String> photos;

        public Page(int id, String name, String type, List<String> photos) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.photos = photos;
        }

    }
}
