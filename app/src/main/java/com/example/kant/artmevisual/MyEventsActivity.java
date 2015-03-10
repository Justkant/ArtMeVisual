package com.example.kant.artmevisual;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;


public class MyEventsActivity extends ToolbarControlBaseActivity<ObservableListView> {

    private ObservableListView mCardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        Toolbar toolbar = getActionBarToolbar();
        if (toolbar != null) {
            toolbar.setTitle("Mes évènements");
            setSupportActionBar(toolbar);
        }

        overridePendingTransition(0, 0);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected View createContainer() {
        return mCardList;
    }

    @Override
    protected ObservableListView createScrollable() {
        mCardList = (ObservableListView) findViewById(R.id.card_list);
        //mSearchList.setAdapter(new ListViewAdapter(this, getData()));
        return mCardList;
    }

    @Override
    public void onRefresh() {

    }
}
