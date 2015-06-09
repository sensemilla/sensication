package com.percolate.youtube.ui.search;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.percolate.R;
import com.percolate.dagger.activity.CaffeineBaseActivity;
import com.percolate.dagger.bus.BusProvider;
import com.percolate.dagger.component.CaffeineAppComponent;
import com.percolate.youtube.support.util.LogUtil;

import javax.inject.Inject;

// todo @EActivity(R.layout.activity_main)
//@EActivity
public class YouTubeActivity extends CaffeineBaseActivity implements SearchView.OnQueryTextListener {
    // todo @ViewById
    DrawerLayout drawerLayout;

    @Inject
    BusProvider busProvider;

    // todo @ViewById
    Toolbar toolbar;

    SearchSetingFragment searchSetingFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     //   setContentView(R.layout.activity_main);

      // todo drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

      //todo  toolbar = (Toolbar) findViewById(R.id.tl_custom);
    }

    @Override
    protected void setupComponent(CaffeineAppComponent caffeineAppComponent) {

    }

  //  @AfterViews
    void afterViews() {
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);
        searchSetingFragment = findSearchSetingFragment();
        searchSetingFragment.setup(R.id.drawcontainer, drawerLayout, toolbar);
    }

    SearchView searchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        if (!searchSetingFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.menu_search, menu);
            searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
            searchView.setOnQueryTextListener(this);
    //        return true;
  //      }
        return super.onCreateOptionsMenu(menu);
    }

    public SearchFragment findSearchFragment() {
        return (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.maincontainer);
    }

    private SearchSetingFragment findSearchSetingFragment() {
        return (SearchSetingFragment) getFragmentManager().findFragmentById(R.id.drawcontainer);
    }

    boolean duplicate;
    String query;

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!duplicate) {
            this.query = query;
            LogUtil.d("query===" + query);
            duplicate = true;
            setTitle(query);
            SearchFragment searchFragment = new SearchFragment();
            hideSoftKeyboard(searchView);
            searchView.clearFocus();
            return searchFragment.onQueryTextSubmit(query);
        } else
            duplicate = false;

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        SearchFragment searchFragment = new SearchFragment();
        return searchFragment.onQueryTextChange(newText);
    }

    public String getQuery() {
        return query == null ? "Search" : query;
    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
