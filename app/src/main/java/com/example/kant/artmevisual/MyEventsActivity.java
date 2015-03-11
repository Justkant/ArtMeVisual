package com.example.kant.artmevisual;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.example.kant.artmevisual.ArtmeAPI.ArtmeAPI;
import com.example.kant.artmevisual.ArtmeAPI.Event;
import com.example.kant.artmevisual.ArtmeAPI.User;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
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


public class MyEventsActivity extends ToolbarControlBaseActivity<ObservableListView> implements AdapterView.OnItemClickListener {

    private ObservableListView mCardList;
    private CardListViewAdapter mEventsCardAdapter;
    private List<Event> events = new ArrayList<>();
    private ArtmeAPI mApi;
    private Context mContext;
    private User user;
    private int eventToShow = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        mContext = this;

        Toolbar toolbar = getActionBarToolbar();
        if (toolbar != null) {
            toolbar.setTitle("Mes évènements");
            setSupportActionBar(toolbar);
        }

        overridePendingTransition(0, 0);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getString(R.string.base_url))
                .build();
        mApi = restAdapter.create(ArtmeAPI.class);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getEvents();
    }

    private void getEvents() {
        mApi.userMe(MySharedPreferences.readToPreferences(this, getString(R.string.token_string), ""), new Callback<User>() {

            @Override
            public void success(User me, Response response) {
                user = me;
                events.clear();
                if (eventToShow == 0)
                    events.addAll(user.next_events);
                else if (eventToShow == 1)
                    events.addAll(user.past_events);
                else if (eventToShow == 2)
                    events.addAll(user.sub_events);
                mEventsCardAdapter.setEventList(events);
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
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_MY_EVENTS;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_events, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        item.setChecked(true);
        if (id == R.id.next_events) {
            eventToShow = 0;
            getEvents();
        }
        else if (id == R.id.past_events) {
            eventToShow = 1;
            getEvents();
        }
        else if (id == R.id.sub_events) {
            eventToShow = 2;
            getEvents();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected View createContainer() {
        return mSwipeRefreshLayout;
    }

    @Override
    protected ObservableListView createScrollable() {
        mCardList = (ObservableListView) findViewById(R.id.card_list);
        mEventsCardAdapter = new CardListViewAdapter(this, events);
        mCardList.setAdapter(mEventsCardAdapter);
        mCardList.setOnItemClickListener(this);
        return mCardList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (events.isEmpty() || events.get(position) == null)
            return;
        Intent intent = new Intent(this, EventActivity.class);
        intent.putExtra("event_id", events.get(position).id);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        getEvents();
    }

    @Override
    public void onScrollChanged(int i, boolean b, boolean b2) {
        boolean enable = false;
        if (mCardList != null && mCardList.getChildCount() > 0) {
            // check if the first item of the list is visible
            boolean firstItemVisible = mCardList.getFirstVisiblePosition() == 0;
            // check if the top of the first item is visible
            boolean topOfFirstItemVisible = mCardList.getChildAt(0).getTop() == 0;
            // enabling or disabling the refresh layout
            enable = firstItemVisible && topOfFirstItemVisible;
        }
        mSwipeRefreshLayout.setEnabled(enable);
    }

}
