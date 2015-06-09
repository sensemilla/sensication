package com.percolate.youtube.ui.search;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.percolate.R;
import com.percolate.youtube.support.http.Search;
import com.percolate.youtube.ui.play.PlayVideoUsingVideoViewActivity;
import com.percolate.youtube.ui.play.PlayVideoUsingYouTuBeActivity;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;


/**
 * Created by LQG on 2014/12/4.
 */
// todo @EFragment(R.layout.fragment_main)
   // @EFragment
public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener {


    private LinearLayout llLayout;

    // todo @ViewById
    ProgressBar pb;

    //todo @ViewById
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentActivity faActivity = (FragmentActivity) super.getActivity();
        // Replace LinearLayout by the type of the root element of the layout you're trying to load
        llLayout = (LinearLayout) inflater.inflate(R.layout.fragment_main, container, false);

        pb = (SmoothProgressBar) llLayout.findViewById(R.id.search_pb);
        listView = (ListView) llLayout.findViewById(R.id.listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemClicked(adapter.getItem(position));
            }
        });

        init();
        return llLayout;
    }



    private List<SearchResult> searchResultList = new ArrayList<>();
    private SearchResultAdapter adapter;
    public QueryTask task;


  //  @AfterViews
    void init() {
        adapter = new SearchResultAdapter(this, searchResultList);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onQueryTextSubmit(final String query) {
        if (task != null)
            task.cancel(true);

       // todo pb.setVisibility(View.VISIBLE);
        task = new QueryTask();
        task.execute(query);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    // todo @ItemClick(R.id.listview)
    void onItemClicked(SearchResult item) {
        Intent lVideoIntent;
        YouTubeInitializationResult result = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(getActivity());
        if (result == YouTubeInitializationResult.SUCCESS) {
            lVideoIntent = new Intent(getActivity(), PlayVideoUsingYouTuBeActivity.class);
        } else {
            lVideoIntent = new Intent(getActivity(), PlayVideoUsingVideoViewActivity.class);
        }
        lVideoIntent.putExtra("videoId", item.getId().getVideoId());
        startActivity(lVideoIntent);
    }

    private class QueryTask extends AsyncTask<String, Void, SearchListResponse> {

        @Override
        protected SearchListResponse doInBackground(String... params) {
            return Search.search(params[0]);
        }

        @Override
        protected void onPostExecute(SearchListResponse searchListResponse) {
       // todo     pb.setVisibility(View.INVISIBLE);
            if (searchListResponse == null)
                return;

            onQuery(searchListResponse);
        }
    }

    private boolean onQuery(SearchListResponse result) {
        if (result == null)
            return false;

        searchResultList.clear();
        searchResultList.addAll(result.getItems());
        Log.d(String.valueOf(result.getItems()), "Search-Results");

       // todo adapter.notifyDataSetChanged();
        return true;
    }

}
