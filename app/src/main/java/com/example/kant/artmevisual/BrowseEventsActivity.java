package com.example.kant.artmevisual;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.kant.artmevisual.ArtmeAPI.ArtmeAPI;
import com.example.kant.artmevisual.ArtmeAPI.Event;
import com.github.ksoichiro.android.observablescrollview.ObservableGridView;
import com.iainconnor.objectcache.CacheManager;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;

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
public class BrowseEventsActivity extends ToolbarControlBaseActivity<ObservableGridView> implements AdapterView.OnItemClickListener {

    private ObservableGridView mEventsGrid;
    private List<Event> events = new ArrayList<>();
    private MaterialDialog.Builder mDialog;
    private CacheManager mCacheManager;
    private Context mContext;
    private ArtmeAPI mApi;
    private GridViewAdapter mEventsGridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_events);
        mContext = this;

        mCacheManager = CacheManager.getInstance(((MyApplication) getApplicationContext()).getDiskCache());

        Toolbar toolbar = getActionBarToolbar();
        if (toolbar != null) {
            toolbar.setTitle("Explorer");
            setSupportActionBar(toolbar);
        }

        overridePendingTransition(0, 0);

        findViewById(R.id.normal_plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, CreateEventActivity.class));
            }
        });

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getString(R.string.base_url))
                .build();
        mApi = restAdapter.create(ArtmeAPI.class);
    }

    //TODO : improve to make less work and get better ui experience
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getUserInfo();
        getEvents();
    }

    private void getEvents() {
        mApi.getEvents(new Callback<List<Event>>() {

            @Override
            public void success(List<Event> allEvents, Response response) {
                events.clear();
                events.addAll(allEvents);
                mEventsGridAdapter.setEventList(events);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void failure(RetrofitError error) {
                mSwipeRefreshLayout.setRefreshing(false);
                SnackbarManager.show(
                        Snackbar.with(mContext)
                                .type(SnackbarType.MULTI_LINE)
                                .text("Impossible de récupérer les évènements")
                                .actionLabel("Réessayer")
                                .actionListener(new ActionClickListener() {
                                    @Override
                                    public void onActionClicked(Snackbar snackbar) {
                                        getEvents();
                                    }
                                })
                );
            }
        });
    }

    @Override
    protected ObservableGridView createScrollable() {
        mEventsGrid = (ObservableGridView) findViewById(R.id.events_grid);
        mEventsGridAdapter = new GridViewAdapter(this, events);
        mEventsGrid.setAdapter(mEventsGridAdapter);
        mEventsGrid.setOnItemClickListener(this);
        return mEventsGrid;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (events.isEmpty() ||events.get(position) == null)
            return;
        Intent intent = new Intent(this, EventActivity.class);
        intent.putExtra("event_id", events.get(position).id);
        startActivity(intent);
    }

    @Override
    protected View createContainer() {
        return mSwipeRefreshLayout;
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_EXPLORE;
    }

    @Override
    public void onRefresh() {
        getEvents();
    }

    @Override
    public void onScrollChanged(int i, boolean b, boolean b2) {
        boolean enable = false;
        if (mEventsGrid != null && mEventsGrid.getChildCount() > 0) {
            // check if the first item of the list is visible
            boolean firstItemVisible = mEventsGrid.getFirstVisiblePosition() == 0;
            // check if the top of the first item is visible
            boolean topOfFirstItemVisible = mEventsGrid.getChildAt(0).getTop() == 0;
            // enabling or disabling the refresh layout
            enable = firstItemVisible && topOfFirstItemVisible;
        }
        mSwipeRefreshLayout.setEnabled(enable);
    }

    @Override
    public void onResume() {
        super.onResume();
        getEvents();
    }
}
