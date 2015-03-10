package com.example.kant.artmevisual;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;


public class SearchActivity extends ToolbarControlBaseActivity<ObservableListView> {

    private ObservableListView mSearchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = getActionBarToolbar();
        if (toolbar != null) {
            toolbar.setTitle("Chercher");
            setSupportActionBar(toolbar);
        }

        overridePendingTransition(0, 0);

    }

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_SEARCH;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
    }

    @Override
    protected View createContainer() {
        return mSearchList;
    }

    @Override
    protected ObservableListView createScrollable() {
        mSearchList = (ObservableListView) findViewById(R.id.search_list);
        //mSearchList.setAdapter(new ListViewAdapter(this, getData()));
        return mSearchList;
    }
}
