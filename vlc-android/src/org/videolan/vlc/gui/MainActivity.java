/*****************************************************************************
 * MainActivity.java
 *****************************************************************************
 * Copyright © 2011-2014 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package org.videolan.vlc.gui;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FilterQueryProvider;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.percolate.caffeine.StringSplitCombine;
import com.percolate.service.ServiceManager;
import com.percolate.service.SystemOverlayMenuServiceMainActivity;
import com.percolate.youtube.support.http.Search;
import com.percolate.youtube.support.player.UrlParser;
import com.percolate.youtube.ui.search.SearchResultAdapter;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.sense.duckduckgo.DDGApplication;
import org.sense.duckduckgo.events.AutoCompleteResultClickEvent;
import org.sense.duckduckgo.events.ConfirmDialogOkEvent;
import org.sense.duckduckgo.events.DisplayHomeScreenEvent;
import org.sense.duckduckgo.events.DisplayScreenEvent;
import org.sense.duckduckgo.events.HistoryItemLongClickEvent;
import org.sense.duckduckgo.events.HistoryItemSelectedEvent;
import org.sense.duckduckgo.events.ReloadEvent;
import org.sense.duckduckgo.events.RemoveWebFragmentEvent;
import org.sense.duckduckgo.events.RequestOpenWebPageEvent;
import org.sense.duckduckgo.events.RequestSyncAdaptersEvent;
import org.sense.duckduckgo.events.StopActionEvent;
import org.sense.duckduckgo.events.WebViewEvents.WebViewBackPressActionEvent;
import org.sense.duckduckgo.events.WebViewEvents.WebViewItemMenuClickEvent;
import org.sense.duckduckgo.events.deleteEvents.DeleteStoryInHistoryEvent;
import org.sense.duckduckgo.events.deleteEvents.DeleteUrlInHistoryEvent;
import org.sense.duckduckgo.events.externalEvents.SearchExternalEvent;
import org.sense.duckduckgo.events.externalEvents.SendToExternalBrowserEvent;
import org.sense.duckduckgo.events.feedEvents.MainFeedItemLongClickEvent;
import org.sense.duckduckgo.events.feedEvents.SavedFeedItemLongClickEvent;
import org.sense.duckduckgo.events.pasteEvents.RecentSearchPasteEvent;
import org.sense.duckduckgo.events.pasteEvents.SuggestionPasteEvent;
import org.sense.duckduckgo.events.saveEvents.SaveSearchEvent;
import org.sense.duckduckgo.events.saveEvents.SaveStoryEvent;
import org.sense.duckduckgo.events.saveEvents.UnSaveSearchEvent;
import org.sense.duckduckgo.events.saveEvents.UnSaveStoryEvent;
import org.sense.duckduckgo.events.savedSearchEvents.SavedSearchItemLongClickEvent;
import org.sense.duckduckgo.events.savedSearchEvents.SavedSearchItemSelectedEvent;
import org.sense.duckduckgo.events.searchBarEvents.SearchBarChangeEvent;
import org.sense.duckduckgo.events.searchBarEvents.SearchBarClearEvent;
import org.sense.duckduckgo.events.searchBarEvents.SearchBarSetProgressEvent;
import org.sense.duckduckgo.events.searchBarEvents.SearchBarSetTextEvent;
import org.sense.duckduckgo.events.shareEvents.ShareFeedEvent;
import org.sense.duckduckgo.events.shareEvents.ShareSearchEvent;
import org.sense.duckduckgo.events.shareEvents.ShareWebPageEvent;
import org.sense.duckduckgo.views.SlidingTabLayout;
import org.sense.duckduckgo.views.webview.DDGWebView;
import org.videolan.libvlc.util.AndroidUtil;
import org.videolan.vlc.BuildConfig;
import org.videolan.vlc.MediaDatabase;
import org.videolan.vlc.MediaLibrary;
import org.videolan.vlc.PlaybackService;
import org.videolan.vlc.R;
import org.videolan.vlc.VLCApplication;
import org.videolan.vlc.dagger.bus.BusProvider;
import org.videolan.vlc.gui.SidebarAdapter.SidebarEntry;
import org.videolan.vlc.gui.audio.AudioBrowserFragment;
import org.videolan.vlc.gui.browser.BaseBrowserFragment;
import org.videolan.vlc.gui.browser.MediaBrowserFragment;
import org.videolan.vlc.gui.browser.NetworkBrowserFragment;
import org.videolan.vlc.gui.network.MRLPanelFragment;
import org.videolan.vlc.gui.video.VideoGridFragment;
import org.videolan.vlc.gui.video.VideoListAdapter;
import org.videolan.vlc.gui.video.VideoPlayerActivity;
import org.videolan.vlc.interfaces.IRefreshable;
import org.videolan.vlc.interfaces.ISortable;
import org.videolan.vlc.util.Util;
import org.videolan.vlc.util.VLCInstance;
import org.videolan.vlc.util.WeakHandler;
import org.videolan.vlc.widget.HackyDrawerLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AudioPlayerContainerActivity implements OnItemClickListener, SearchSuggestionsAdapter.SuggestionDisplay, FilterQueryProvider {
    public final static String TAG = "VLC/MainActivity";

    private static final String PREF_FIRST_RUN = "first_run";

    private static final int ACTIVITY_RESULT_PREFERENCES = 1;
    private static final int ACTIVITY_SHOW_INFOLAYOUT = 2;
    private static final int ACTIVITY_SHOW_PROGRESSBAR = 3;
    private static final int ACTIVITY_HIDE_PROGRESSBAR = 4;
    private static final int ACTIVITY_SHOW_TEXTINFO = 5;

    MediaLibrary mMediaLibrary;

    private SidebarAdapter mSidebarAdapter;
    private HackyDrawerLayout mDrawerLayout;
    private ListView mListView;
    private ActionBarDrawerToggle mDrawerToggle;

    private View mInfoLayout;
    private ProgressBar mInfoProgress;
    private TextView mInfoText;
    private String mCurrentFragment;


    private int mVersionNumber = -1;
    private boolean mFirstRun = false;
    private boolean mScanNeeded = false;

    private Handler mHandler = new MainActivityHandler(this);
    private int mFocusedPrior = 0;
    private int mActionBarIconId = -1;
    Menu mMenu;
    private SearchView mSearchView;
    private SearchView searchView;
    private List<SearchResult> searchResultList = new ArrayList<>();
    private SearchResultAdapter adapter;
    String parsedurl;
    View duckduckgo;
    private ServiceManager serviceMain;
    public String videoId;
    private boolean keyboardHidden = false;
    private RelativeLayout mMainFrame;
    private SlidingTabLayout slidingTabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        VLCApplication.setVlcMainActivity(this);
        if (!VLCInstance.testCompatibleCPU(this)) {
            finish();
            return;
        }
        /* Enable the indeterminate progress feature */
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        /* Get the current version from package */
        mVersionNumber = BuildConfig.VERSION_CODE;

        /* Check if it's the first run */
        mFirstRun = mSettings.getInt(PREF_FIRST_RUN, -1) != mVersionNumber;
        if (mFirstRun) {
            Editor editor = mSettings.edit();
            editor.putInt(PREF_FIRST_RUN, mVersionNumber);
            Util.commitPreferences(editor);
        }

        /* Load media items from database and storage */
        mMediaLibrary = MediaLibrary.getInstance();
        mMediaLibrary.loadMediaItems();

        /*** Start initializing the UI ***/

        setContentView(R.layout.main);

        mMainFrame = (RelativeLayout) findViewById(R.id.main_frame);

        mDrawerLayout = (HackyDrawerLayout) findViewById(R.id.root_container);
        DDGApplication.setVLCMainFrame(mDrawerLayout);
        mListView = (ListView) findViewById(R.id.sidelist);
        mListView.setFooterDividersEnabled(true);
        mSidebarAdapter = new SidebarAdapter(this);
        mListView.setAdapter(mSidebarAdapter);

        initAudioPlayerContainerActivity();

        if (savedInstanceState != null) {
            mCurrentFragment = savedInstanceState.getString("current");
            if (mCurrentFragment != null)
                mSidebarAdapter.setCurrentFragment(mCurrentFragment);
        }


        /* Initialize UI variables */
        mInfoLayout = findViewById(R.id.info_layout);
        mInfoProgress = (ProgressBar) findViewById(R.id.info_progress);
        mInfoText = (TextView) findViewById(R.id.info_text);

        /* Set up the action bar */
        prepareActionBar();

        slidingTabLayout = (SlidingTabLayout) mToolbar.findViewById(R.id.sliding_tabs);
        DDGApplication.setDDGslidingTabLayout(slidingTabLayout);
        /* Set up the sidebar click listener
         * no need to invalidate menu for now */
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (getSupportFragmentManager().findFragmentById(R.id.fragment_placeholder) instanceof MediaBrowserFragment)
                    ((MediaBrowserFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_placeholder)).setReadyToDisplay(true);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        // set a custom shadowresmenu that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mListView.setOnItemClickListener(this);

        if (mFirstRun) {
            /*
             * The sliding menu is automatically opened when the user closes
             * the info dialog. If (for any reason) the dialog is not shown,
             * open the menu after a short delay.
             */
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDrawerLayout.openDrawer(mListView);
                }
            }, 500);
        }

        /* Reload the latest preferences */
        reloadPreferences();

        setupMenuServices();

        LayoutInflater inflater = getLayoutInflater();
        View residemenu = inflater.inflate(R.layout.bottommenubar, null);
        residemenu.setBottom(Gravity.BOTTOM);
        View residefragment = inflater.inflate(R.layout.residemenuframe, null);
        //      ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(200,200);
//        residefragment.setLayoutParams(layoutParams);
        FrameLayout placeHolderFrame = (FrameLayout) findViewById(R.id.fragment_placeholder);
        residemenu.setVisibility(View.GONE);
        placeHolderFrame.addView(residefragment);
        mMainFrame.addView(residemenu);
        // todo
        setUpMenu();
        hideresidemenuframe();

        adapter = new SearchResultAdapter(this, searchResultList);
        // QueryTask queryTask = new QueryTask();
       // queryTask.execute("mellow");


        serviceMain.start();
    }

    public class QueryTask extends AsyncTask<String, Void, SearchListResponse> {

        @Override
        protected SearchListResponse doInBackground(String... params) {
            Log.d(String.valueOf(Search.search(params[0])), "SearchListResponse");

            String lYouTubeFmtQuality = "18";
            videoId = DDGApplication.getSelVideoID();

            if (videoId == null)
                videoId = "GzI3TRleUkw";
            try {
             parsedurl = new UrlParser().calculateYouTubeUrl(lYouTubeFmtQuality, true, videoId);
            Log.d("ParsedUrl", parsedurl);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return Search.search(params[0]);
        }

        @Override
        protected void onPostExecute(SearchListResponse searchListResponse) {
            // todo     pb.setVisibility(View.INVISIBLE);
            Log.d(String.valueOf(searchListResponse), "SearchListResponse PostExecute");

            if (searchListResponse == null)
                return;

            onQuery(searchListResponse);

            String encodedurl = StringSplitCombine.doreplace(parsedurl);
            Util.openStream(MainActivity.this, encodedurl);
            MediaDatabase.getInstance().addMrlhistoryItem(encodedurl);

        }
    }

    private boolean onQuery(SearchListResponse result) {
        if (result == null)
            return false;

        searchResultList.clear();
        searchResultList.addAll(result.getItems());
        Log.d(String.valueOf(searchResultList), "Search-Results");

        adapter.notifyDataSetChanged();
        return true;
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void prepareActionBar() {
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        serviceMain.start();
        DDGApplication.setServiceMain(serviceMain);
        /* FIXME: this is used to avoid having MainActivity twice in the backstack */
        if (getIntent().hasExtra(PlaybackService.START_FROM_NOTIFICATION))
            getIntent().removeExtra(PlaybackService.START_FROM_NOTIFICATION);


        /* Load media items from database and storage */
        if (mScanNeeded)
            mMediaLibrary.loadMediaItems();
        if (mSlidingPane.getState() == mSlidingPane.STATE_CLOSED)
            mActionBar.hide();
   }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        // Figure out if currently-loaded fragment is a top-level fragment.
        Fragment current = getSupportFragmentManager()
                .findFragmentById(R.id.fragment_placeholder);
        boolean found = (current == null) || SidebarAdapter.sidebarFragments.contains(current.getTag());

        /**
         * Restore the last view.
         *
         * Replace:
         * - null fragments (freshly opened Activity)
         * - Wrong fragment open AND currently displayed fragment is a top-level fragment
         *
         * Do not replace:
         * - Non-sidebar fragments.
         * It will try to remove() the currently displayed fragment
         * (i.e. tracks) and replace it with a blank screen. (stuck menu bug)
         */
        if (current == null || (!current.getTag().equals(mCurrentFragment) && found)) {
            Log.d(TAG, "Reloading displayed fragment");
            if (mCurrentFragment == null)
                mCurrentFragment = "video";
            if (!SidebarAdapter.sidebarFragments.contains(mCurrentFragment)) {
                Log.d(TAG, "Unknown fragment \"" + mCurrentFragment + "\", resetting to video");
                mCurrentFragment = "video";
            }
            Fragment ff = getFragment(mCurrentFragment);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_placeholder, ff, mCurrentFragment);
            ft.commit();
        }
    }

    /**
     * Stop audio player and save opened tab
     */
    @Override
    public void onPause() {
        super.onPause();
        serviceMain.stop();
        /* Check for an ongoing scan that needs to be resumed during onResume */
        mScanNeeded = mMediaLibrary.isWorking();
        /* Stop scanning for files */
        mMediaLibrary.stop();
        /* Save the tab status in pref */
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString("fragment", mCurrentFragment);
        Util.commitPreferences(editor);

        mFocusedPrior = 0;
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("current", mCurrentFragment);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        /* Reload the latest preferences */
        serviceMain.stop();
        reloadPreferences();
        serviceMain.start();
        DDGApplication.setServiceMain(serviceMain);
    }

    @Override
    public void onBackPressed() {
            /* Close the menu first */
        if(mDrawerLayout.isDrawerOpen(mListView)) {
            if (mFocusedPrior != 0)
                requestFocusOnSearch();
            mDrawerLayout.closeDrawer(mListView);
            return;
        }

        // Slide down the audio player if it is shown entirely.
        if (slideDownAudioPlayer())
            return;

        if (mCurrentFragment!= null) {
            // If it's the directory view, a "backpressed" action shows a parent.
            if (mCurrentFragment.equals(SidebarEntry.ID_NETWORK) || mCurrentFragment.equals(SidebarEntry.ID_DIRECTORIES)){
                BaseBrowserFragment browserFragment = (BaseBrowserFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_placeholder);
                if (browserFragment != null) {
                    browserFragment.goBack();
                    return;
                }
            }
        }
        if (duckduckgo != null) {
            if (duckduckgo.getVisibility() == View.VISIBLE) {
                DDGWebView wv = DDGApplication.getDDGWebView();
                if (wv != null) {
                    if (wv.readableBackState) {
                        wv.readableBackState = false;
                        if (wv.canGoBack()) {
                            BusProvider.getInstance().post(new WebViewBackPressActionEvent());
                            wv.loadingReadableBack = true;
                            wv.goBack();
                        }
                    } else if (!wv.canGoBack()) {
                        if (((InputMethodManager) VLCApplication.getAppContext().getSystemService(INPUT_METHOD_SERVICE)).isActive()) {
                            hideKeyboard();

                            // todo duckduckgo.setVisibility(View.GONE);
                            return;
                        }
                    }
                }
            }
        }
        Dialog dialog = new Dialog(this);
        dialog.create();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                return;
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        dialog.show();

    }

    private Fragment getFragment(String id)
    {
        Fragment frag = getSupportFragmentManager().findFragmentByTag(id);
        if (frag != null)
            return frag;
        return mSidebarAdapter.fetchFragment(id);
    }

    private static void ShowFragment(FragmentActivity activity, String tag, Fragment fragment, String previous) {
        if (fragment == null) {
            Log.e(TAG, "Cannot show a null fragment, ShowFragment(" + tag + ") aborted.");
            return;
        }

        FragmentManager fm = activity.getSupportFragmentManager();

        //abort if fragment is already the current one
        Fragment current = fm.findFragmentById(R.id.fragment_placeholder);
        if(current != null && current.getTag().equals(tag))
            return;

        //try to pop back if the fragment is already on the backstack
        if (fm.popBackStackImmediate(tag, 0))
            return;

        //fragment is not there yet, spawn a new one
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.anim_enter_right, R.anim.anim_leave_left, R.anim.anim_enter_left, R.anim.anim_leave_right);
        ft.replace(R.id.fragment_placeholder, fragment, tag);
        ft.addToBackStack(previous);
        ft.commit();
    }

    /**
     * Show a secondary fragment.
     */
    public void showSecondaryFragment(String fragmentTag) {
        showSecondaryFragment(fragmentTag, null);
    }

    public void showSecondaryFragment(String fragmentTag, String param) {
        Intent i = new Intent(this, SecondaryActivity.class);
        i.putExtra("fragment", fragmentTag);
        if (param != null)
            i.putExtra("param", param);
        startActivity(i);
        // Slide down the audio player if needed.
        slideDownAudioPlayer();
    }

    /** Create menu from XML
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        /* Note: on Android 3.0+ with an action bar this method
         * is called while the view is created. This can happen
         * any time after onCreate.
         */

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.media_library, menu);

        if (AndroidUtil.isFroyoOrLater()) {
            SearchManager searchManager =
                    (SearchManager) VLCApplication.getAppContext().getSystemService(Context.SEARCH_SERVICE);
            mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.ml_menu_search));
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            mSearchView.setQueryHint(getString(R.string.search_hint));
            SearchSuggestionsAdapter searchSuggestionsAdapter = new SearchSuggestionsAdapter(this, null);
            searchSuggestionsAdapter.setFilterQueryProvider(this);
            mSearchView.setSuggestionsAdapter(searchSuggestionsAdapter);
        } else
            menu.findItem(R.id.ml_menu_search).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menu == null)
            return false;
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_placeholder);
        MenuItem item;
        // Disable the sort option if we can't use it on the current fragment.
        if (current == null || !(current instanceof ISortable)) {
            item = menu.findItem(R.id.ml_menu_sortby);
            if (item == null)
                return false;
            item.setEnabled(false);
            item.setVisible(false);
        } else {
            ISortable sortable = (ISortable) current;
            item = menu.findItem(R.id.ml_menu_sortby);
            if (item == null)
                return false;
            item.setEnabled(true);
            item.setVisible(true);
            item = menu.findItem(R.id.ml_menu_sortby_name);
            if (sortable.sortDirection(VideoListAdapter.SORT_BY_TITLE) == 1)
                item.setTitle(R.string.sortby_name_desc);
            else
                item.setTitle(R.string.sortby_name);
            item = menu.findItem(R.id.ml_menu_sortby_length);
            if (sortable.sortDirection(VideoListAdapter.SORT_BY_LENGTH) == 1)
                item.setTitle(R.string.sortby_length_desc);
            else
                item.setTitle(R.string.sortby_length);
            item = menu.findItem(R.id.ml_menu_sortby_date);
            if (sortable.sortDirection(VideoListAdapter.SORT_BY_DATE) == 1)
                item.setTitle(R.string.sortby_date_desc);
            else
                item.setTitle(R.string.sortby_date);
        }

        boolean networkSave = current instanceof NetworkBrowserFragment && !((NetworkBrowserFragment)current).isRootDirectory();
        if (networkSave) {
            item = menu.findItem(R.id.ml_menu_save);
            item.setVisible(true);
            String mrl = ((BaseBrowserFragment)current).mMrl;
            item.setIcon(MediaDatabase.getInstance().networkFavExists(mrl) ?
                    R.drawable.ic_menu_bookmark_w :
                    R.drawable.ic_menu_bookmark_outline_w);
        } else
            menu.findItem(R.id.ml_menu_save).setVisible(false);
        if (current instanceof MRLPanelFragment)
            menu.findItem(R.id.ml_menu_clean).setVisible(!((MRLPanelFragment) current).isEmpty());
        boolean showLast = current instanceof AudioBrowserFragment || (current instanceof VideoGridFragment && mSettings.getString(PreferencesActivity.VIDEO_LAST, null) != null);
        menu.findItem(R.id.ml_menu_last_playlist).setVisible(showLast);
        return true;
    }

    /**
     * Handle onClick form menu buttons
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Current fragment loaded
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_placeholder);

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.ml_menu_sortby_name:
            case R.id.ml_menu_sortby_length:
            case R.id.ml_menu_sortby_date:
                if (current == null)
                    break;
                if (current instanceof ISortable) {
                    int sortBy = VideoListAdapter.SORT_BY_TITLE;
                    if (item.getItemId() == R.id.ml_menu_sortby_length)
                        sortBy = VideoListAdapter.SORT_BY_LENGTH;
                    else if(item.getItemId() == R.id.ml_menu_sortby_date)
                        sortBy = VideoListAdapter.SORT_BY_DATE;
                    ((ISortable) current).sortBy(sortBy);
                    supportInvalidateOptionsMenu();
                }
                break;
            case R.id.ml_menu_equalizer:
                showSecondaryFragment(SecondaryActivity.EQUALIZER);
                break;
            // Refresh
            case R.id.ml_menu_refresh:
                if (!mMediaLibrary.isWorking()) {
                    if(current != null && current instanceof IRefreshable)
                        ((IRefreshable) current).refresh();
                    else
                        mMediaLibrary.loadMediaItems(true);
                }
                break;
            // Restore last playlist
            case R.id.ml_menu_last_playlist:
                if (current instanceof AudioBrowserFragment) {
                    Intent i = new Intent(PlaybackService.ACTION_REMOTE_LAST_PLAYLIST);
                    sendBroadcast(i);
                } else if (current instanceof VideoGridFragment) {
                    String location = Uri.decode(mSettings.getString(PreferencesActivity.VIDEO_LAST, null));
                    if (location != null)
                        VideoPlayerActivity.start(this, location);
                }
                break;
            case android.R.id.home:
                // Slide down the audio player.
                if (slideDownAudioPlayer())
                    break;
                /* Toggle the sidebar */
                if (mDrawerToggle.onOptionsItemSelected(item)) {
                    if (duckduckgo.getVisibility() == View.VISIBLE) {
                        duckduckgo.setVisibility(View.GONE);
                        mDrawerLayout.setVisibility(View.VISIBLE);
                        DDGApplication.getVLCMainToolbar().bringToFront();
                        break;
                    }
                    return true;
                }
                break;
            case R.id.ml_menu_clean:
                if (getFragment(mCurrentFragment) instanceof MRLPanelFragment)
                    ((MRLPanelFragment)getFragment(mCurrentFragment)).clearHistory();
                break;
            case R.id.ml_menu_save:
                if (current == null)
                    break;
                ((NetworkBrowserFragment)current).toggleFavorite();
                item.setIcon(R.drawable.ic_menu_bookmark_w);
                break;
        }
        mDrawerLayout.closeDrawer(mListView);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_RESULT_PREFERENCES) {
            if (resultCode == PreferencesActivity.RESULT_RESCAN)
                mMediaLibrary.loadMediaItems(true);
            else if (resultCode == PreferencesActivity.RESULT_RESTART) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        }
    }

    public void setMenuFocusDown(boolean idIsEmpty, int id) {
        if (mMenu == null)
            return;
        //Save menu items ids for focus control
        final int[] menu_controls = new int[mMenu.size()+1];
        for (int i = 0 ; i < mMenu.size() ; i++){
            menu_controls[i] = mMenu.getItem(i).getItemId();
        }
        menu_controls[mMenu.size()] = mActionBarIconId;
        /*menu_controls = new int[]{R.id.ml_menu_search,
            R.id.ml_menu_open_mrl, R.id.ml_menu_sortby,
            R.id.ml_menu_last_playlist, R.id.ml_menu_refresh,
            mActionBarIconId};*/
        int pane = mSlidingPane.getState();
        for(int r : menu_controls) {
            View v = findViewById(r);
            if (v != null) {
                if (!idIsEmpty)
                    v.setNextFocusDownId(id);
                else {
                    if (pane ==  mSlidingPane.STATE_CLOSED) {
                        v.setNextFocusDownId(R.id.play_pause);
                    } else if (pane == mSlidingPane.STATE_OPENED) {
                        v.setNextFocusDownId(R.id.header_play_pause);
                    } else if (pane ==
                        mSlidingPane.STATE_OPENED_ENTIRELY) {
                        v.setNextFocusDownId(r);
                    }
                }
            }
        }
    }

    public void setSearchAsFocusDown(boolean idIsEmpty, View parentView, int id) {
        View playPause = findViewById(R.id.header_play_pause);

        if (!idIsEmpty) {
            View list;
            int pane = mSlidingPane.getState();

            if (parentView == null)
                list = findViewById(id);
            else
                list = parentView.findViewById(id);

            if (list != null) {
                if (pane == mSlidingPane.STATE_OPENED_ENTIRELY) {
                    list.setNextFocusDownId(id);
                } else if (pane == mSlidingPane.STATE_OPENED) {
                    list.setNextFocusDownId(R.id.header_play_pause);
                    playPause.setNextFocusUpId(id);
                }
            }
        } else {
           playPause.setNextFocusUpId(R.id.ml_menu_search);
        }
    }

    // Note. onKeyDown will not occur while moving within a list
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Filter for LG devices, see https://code.google.com/p/android/issues/detail?id=78154
        if ((keyCode == KeyEvent.KEYCODE_MENU) &&
                (Build.VERSION.SDK_INT <= 16) &&
                (Build.MANUFACTURER.compareTo("LGE") == 0)) {
            return true;
        }
        if (mFocusedPrior == 0)
            setMenuFocusDown(true, 0);
        if (getCurrentFocus() != null)
            mFocusedPrior = getCurrentFocus().getId();
        return super.onKeyDown(keyCode, event);
    }

    // Note. onKeyDown will not occur while moving within a list
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        //Filter for LG devices, see https://code.google.com/p/android/issues/detail?id=78154
        if ((keyCode == KeyEvent.KEYCODE_MENU) &&
                (Build.VERSION.SDK_INT <= 16) &&
                (Build.MANUFACTURER.compareTo("LGE") == 0)) {
            openOptionsMenu();
            return true;
        }
        View v = getCurrentFocus();
        if (v == null)
            return super.onKeyUp(keyCode, event);
        if ((mActionBarIconId == -1) &&
            (v.getId() == -1)  &&
            (v.getNextFocusDownId() == -1) &&
            (v.getNextFocusUpId() == -1) &&
            (v.getNextFocusLeftId() == -1) &&
            (v.getNextFocusRightId() == -1)) {
            mActionBarIconId = Util.generateViewId();
            v.setId(mActionBarIconId);
            v.setNextFocusUpId(mActionBarIconId);
            v.setNextFocusDownId(mActionBarIconId);
            v.setNextFocusLeftId(mActionBarIconId);
            v.setNextFocusRightId(R.id.ml_menu_search);
            if (AndroidUtil.isHoneycombOrLater())
                v.setNextFocusForwardId(mActionBarIconId);
            if (findViewById(R.id.ml_menu_search) != null)
                findViewById(R.id.ml_menu_search).setNextFocusLeftId(mActionBarIconId);
        }
        return super.onKeyUp(keyCode, event);
    }

    private void reloadPreferences() {
        mCurrentFragment = mSettings.getString("fragment", "video");
    }

    @Override
    public Cursor runQuery(CharSequence constraint) {
        return MediaDatabase.getInstance().queryMedia(constraint.toString());
    }

    private static class MainActivityHandler extends WeakHandler<MainActivity> {
        public MainActivityHandler(MainActivity owner) {
            super(owner);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity ma = getOwner();
            if(ma == null) return;

            switch (msg.what) {
                case ACTIVITY_SHOW_INFOLAYOUT:
                    ma.mInfoLayout.setVisibility(View.VISIBLE);
                    break;
                case ACTIVITY_SHOW_PROGRESSBAR:
                    ma.setSupportProgressBarIndeterminateVisibility(true);
                    ma.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    break;
                case ACTIVITY_HIDE_PROGRESSBAR:
                    ma.setSupportProgressBarIndeterminateVisibility(false);
                    ma.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    break;
                case ACTIVITY_SHOW_TEXTINFO:
                    String info = (String) msg.obj;
                    int max = msg.arg1;
                    int progress = msg.arg2;
                    ma.mInfoText.setText(info);
                    ma.mInfoProgress.setMax(max);
                    ma.mInfoProgress.setProgress(progress);

                    if (info == null) {
                    /* Cancel any upcoming visibility change */
                        removeMessages(ACTIVITY_SHOW_INFOLAYOUT);
                        ma.mInfoLayout.setVisibility(View.GONE);
                    }
                    else {
                    /* Slightly delay the appearance of the progress bar to avoid unnecessary flickering */
                        if (!hasMessages(ACTIVITY_SHOW_INFOLAYOUT)) {
                            Message m = new Message();
                            m.what = ACTIVITY_SHOW_INFOLAYOUT;
                            sendMessageDelayed(m, 300);
                        }
                    }
                    break;
            }
        }
    }

    public void hideKeyboard(){
        ((InputMethodManager) VLCApplication.getAppContext().getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                getWindow().getDecorView().getRootView().getWindowToken(), 0);
    }

    public void showProgressBar() {
        mHandler.obtainMessage(ACTIVITY_SHOW_PROGRESSBAR).sendToTarget();
    }

    public void hideProgressBar() {
        mHandler.obtainMessage(ACTIVITY_HIDE_PROGRESSBAR).sendToTarget();
    }

    public void sendTextInfo(String info, int progress, int max) {
        mHandler.obtainMessage(ACTIVITY_SHOW_TEXTINFO, max, progress, info).sendToTarget();
    }

    public void clearTextInfo() {
        mHandler.obtainMessage(ACTIVITY_SHOW_TEXTINFO, 0, 100, null).sendToTarget();
    }

    protected void onPanelClosedUiSet() {
        mDrawerLayout.setDrawerLockMode(HackyDrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    protected void onPanelOpenedEntirelyUiSet() {
        mDrawerLayout.setDrawerLockMode(HackyDrawerLayout.LOCK_MODE_UNLOCKED);
    }

    protected void onPanelOpenedUiSet() {
        mDrawerLayout.setDrawerLockMode(HackyDrawerLayout.LOCK_MODE_UNLOCKED);
        removeTipViewIfDisplayed();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (duckduckgo != null) {
            if (duckduckgo.getVisibility() == View.VISIBLE)
                duckduckgo.setVisibility(View.GONE);
        }
        SidebarAdapter.SidebarEntry entry = (SidebarEntry) mListView.getItemAtPosition(position);
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_placeholder);

        if(current == null || (entry != null && current.getTag().equals(entry.id))) { /* Already selected */
            if (mFocusedPrior != 0)
                requestFocusOnSearch();
            mDrawerLayout.closeDrawer(mListView);
            return;
        }

        // This should not happen
        if(entry == null || entry.id == null)
            return;

        if (entry.type == SidebarEntry.TYPE_FRAGMENT) {

                /* Slide down the audio player */
            slideDownAudioPlayer();

                /* Switch the fragment */
            Fragment fragment = getFragment(entry.id);
            if (fragment instanceof MediaBrowserFragment)
                ((MediaBrowserFragment)fragment).setReadyToDisplay(false);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_placeholder, fragment, entry.id);
            ft.addToBackStack(mCurrentFragment);
            ft.commit();
            mCurrentFragment = entry.id;
            mSidebarAdapter.setCurrentFragment(mCurrentFragment);

            if (mFocusedPrior != 0)
                requestFocusOnSearch();
        } else if (entry.type == SidebarEntry.TYPE_SECONDARY_FRAGMENT)
            showSecondaryFragment(SecondaryActivity.ABOUT);
        else if (entry.attributeID == R.attr.ic_menu_preferences)
            startActivityForResult(new Intent(this, PreferencesActivity.class), ACTIVITY_RESULT_PREFERENCES);
        mDrawerLayout.closeDrawer(mListView);


    }

    private void requestFocusOnSearch() {
        View search = findViewById(R.id.ml_menu_search);
        if (search != null)
            search.requestFocus();
    }


    ////////////////////////////////////////SerbiceMenu

    private void setupMenuServices() {
        this.serviceMain = new ServiceManager(this, SystemOverlayMenuServiceMainActivity.class, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                /*if (msg.what == SystemOverlayMenuService.MSG_COUNTER) {
                    try { service2.send(Message.obtain(null, SystemOverlayMenuServiceTasks.MSG_VALUE, msg.arg1, 0)); }
                catch (RemoteException e) { }
                }*/
                if (msg.what == SystemOverlayMenuServiceMainActivity.MSG_BtnClickMain) {
                    if (msg.arg1 == 1) {

                    } else if (msg.arg1 == 2) {
                        duckduckgo.setVisibility(View.GONE);

                    } else if (msg.arg1 == 3) {

                        loadDDGlayout();

                    } else if (msg.arg1 == 4) {
                        Log.v(TAG, String.valueOf(msg.arg1));
                        QueryTask queryTask = new QueryTask();
                        queryTask.execute("mellow");

                    } else if (msg.arg1 == 5) {
                        WebView.HitTestResult hitTestResult = ((DDGWebView)DDGApplication.getDDGWebView()).getHitTestResult();
                        String test = String.valueOf(hitTestResult.getExtra());
                        Log.v(TAG, test);
                    } else if (msg.arg1 == 6) {

                    } else if (msg.arg1 == 7) {
                    }
                    Log.v("ClickBtn @ Service1: ", String.valueOf(msg.arg1));

                } else {
                    super.handleMessage(msg);
                }
            }
        });
        DDGApplication.setServiceMain(serviceMain);

    }

    private void loadDDGlayout() {
        LayoutInflater inflater = getLayoutInflater();
        // todo
        if (duckduckgo == null) {
            duckduckgo = inflater.inflate(R.layout.temp_main, mMainFrame, false);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            duckduckgo.setLayoutParams(layoutParams);
            duckduckgo.setPadding(0, 60, 0, 0);
            mMainFrame.addView(duckduckgo);
            inflateLayout();
            DDGApplication.setDDGMainFrame(duckduckgo);
            mDrawerLayout.setVisibility(View.GONE);
            DDGApplication.getVLCMainToolbar().bringToFront();
        } else {
            duckduckgo.setVisibility(View.VISIBLE);
            mDrawerLayout.setVisibility(View.GONE);
            DDGApplication.getVLCMainToolbar().bringToFront();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        //TorIntegrationProvider.getInstance(this).prepareTorSettings();
        // todo DDGControlVar.mDuckDuckGoContainer.torIntegration.prepareTorSettings();
        BusProvider.getInstance().register(this);
        Bus busProvider = BusProvider.getInstance();
        DDGApplication.setBusProvider(busProvider);

        loadDDGlayout();
        duckduckgo.setVisibility(View.GONE);
        mDrawerLayout.bringToFront();
    }


    @Override
    protected void onStop() {
        super.onStop();
        //BusProvider.getInstance().unregister(this);
        Log.d(TAG, "on stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    ////////////////////////////////////////////////////////////////SubscribersForDDGevents

    @Subscribe
    public void onDeleteStoryInHistoryEvent(DeleteStoryInHistoryEvent event) {onisDeleteStoryInHistoryEvent(event);//left menu
    }
    @Subscribe
    public void onDeleteUrlInHistoryEvent(DeleteUrlInHistoryEvent event) {onisDeleteUrlInHistoryEvent(event);//left menu
    }
    @Subscribe
    public void onReloadEvent(ReloadEvent event) {
        onisReloadEvent(event);
    }
    @Subscribe
    public void onSaveSearchEvent(SaveSearchEvent event) {onisSaveSearchEvent(event);
    }
    @Subscribe
    public void onSaveStoryEvent(SaveStoryEvent event) {onisSaveStoryEvent(event);
    }
    @Subscribe
    public void onSendToExternalBrowserEvent(SendToExternalBrowserEvent event) {onisSendToExternalBrowserEvent(event);
    }
    @Subscribe
    public void onSearchExternalEvent(SearchExternalEvent event) {onisSearchExternalEvent(event);
    }
    @Subscribe
    public void onShareFeedEvent(ShareFeedEvent event) {onisShareFeedEvent(event);
    }
    @Subscribe
    public void onShareSearchEvent(ShareSearchEvent event) {onisShareSearchEvent(event);
    }
    @Subscribe
    public void onShareWebPageEvent(ShareWebPageEvent event) {onisShareWebPageEvent(event);//web fragment
    }
    @Subscribe
    public void onUnSaveSearchEvent(UnSaveSearchEvent event) {onisUnSaveSearchEvent(event);
    }
    @Subscribe
    public void onUnSaveStoryEvent(UnSaveStoryEvent event) {onisUnSaveStoryEvent(event);
    }
    @Subscribe
    public void onMainFeedItemLongClick(MainFeedItemLongClickEvent event) {onisMainFeedItemLongClick(event);
    }
    @Subscribe
    public void onSavedFeedItemLongClick(SavedFeedItemLongClickEvent event) {onisSavedFeedItemLongClick(event);    }
    @Subscribe
    public void onHistoryItemSelected(HistoryItemSelectedEvent event) {onisHistoryItemSelected(event);
    }
    @Subscribe
    public void onHistoryItemLongClick(HistoryItemLongClickEvent event) {onisHistoryItemLongClick(event);
    }
    @Subscribe
    public void onSavedSearchItemSelected(SavedSearchItemSelectedEvent event) {onisSavedSearchItemSelected(event);
    }
    @Subscribe
    public void onSavedSearchItemLongClick(SavedSearchItemLongClickEvent event) {onisSavedSearchItemLongClick(event);
    }
    @Subscribe
    public void onRecentSearchPaste(RecentSearchPasteEvent event) {onisRecentSearchPaste(event);
    }
    @Subscribe
    public void onSuggestionPaste(SuggestionPasteEvent event) {onisSuggestionPaste(event);
    }
    @Subscribe
    public void onDisplayScreenEvent(DisplayScreenEvent event) {onisDisplayScreenEvent(event);
    }
    @Subscribe
    public void onSearchBarClearEvent(SearchBarClearEvent event) {onisSearchBarClearEvent(event);
    }
    @Subscribe
    public void onSearchBarSetTextEvent(SearchBarSetTextEvent event) {onisSearchBarSetTextEvent(event);
    }
    @Subscribe
    public void onSearchBarSetProgressEvent(SearchBarSetProgressEvent event) {onisSearchBarSetProgressEvent(event);
    }
    @Subscribe
    public void onRequestOpenWebPageEvent(RequestOpenWebPageEvent event) {onisRequestOpenWebPageEvent(event);
    }
    @Subscribe
    public void onStopActionEvent(StopActionEvent event) {onisStopActionEvent(event);
    }
    @Subscribe
    public void onRequestSyncAdaptersEvent(RequestSyncAdaptersEvent event) {onisRequestSyncAdaptersEvent(event);
    }
    @Subscribe
    public void onConfirmDialogOkEvent(ConfirmDialogOkEvent event) {onisConfirmDialogOkEvent(event);
    }
    @Subscribe
    public void onRemoveWebFragmentEvent(RemoveWebFragmentEvent event) {onisRemoveWebFragmentEvent(event);//Log.e("aaa", "remove web fragment");
    }
    @Subscribe
    public void onSearchBarChangeEvent(SearchBarChangeEvent event) {
        //changeSearchBar(event.screen);
    }
    @Subscribe
    public void onDisplayHomeScreenEvent(DisplayHomeScreenEvent event) {onisDisplayHomeScreenEvent(event);
    }
    @Subscribe
    public void onAutoCompleteResultClickEvent(AutoCompleteResultClickEvent event) {onisAutoCompleteResultClickEvent(event);
    }
    @Subscribe
    public void onWebViewItemMenuClickEvent(WebViewItemMenuClickEvent event) {onisWebViewItemMenuClickEvent(event);
    }



}
