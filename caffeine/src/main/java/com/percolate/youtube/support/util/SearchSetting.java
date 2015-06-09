package com.percolate.youtube.support.util;

import android.preference.PreferenceManager;

import com.percolate.youtube.support.GlobalApplication;
import com.percolate.youtube.ui.search.SearchSetingFragment;

/**
 * Created by LQG on 2014/12/10.
 */
public class SearchSetting {
    private String maxResults, order, safeSearch, videoDefinition, videoDuration, videoType;
    private static SearchSetting ourInstance = new SearchSetting();

    private SearchSetting() {
    }

    public static SearchSetting getInstance() {
        return ourInstance;
    }

    public String getMaxResults() {
        if (maxResults == null)
            maxResults = getMaxResultsFromPre();
        return maxResults;
    }

    public static String getMaxResultsFromPre() {
        return PreferenceManager.getDefaultSharedPreferences(GlobalApplication.getInstance()).getString(SearchSetingFragment.MAXRESULTS, "20");
    }

    public String getOrder() {
        if (order == null)
            order = PreferenceManager.getDefaultSharedPreferences(GlobalApplication.getInstance()).getString(SearchSetingFragment.ORDER, "relevance");
        return order;
    }

    public static String getOrderFromPre() {
        return PreferenceManager.getDefaultSharedPreferences(GlobalApplication.getInstance()).getString(SearchSetingFragment.ORDER, "relevance");
    }

    public String getSafeSearch() {
        if (safeSearch == null)
            safeSearch = PreferenceManager.getDefaultSharedPreferences(GlobalApplication.getInstance()).getString(SearchSetingFragment.SAFESEARCH, "none");
        return safeSearch;
    }

    public static String getSafeSearchFromPre() {
        return PreferenceManager.getDefaultSharedPreferences(GlobalApplication.getInstance()).getString(SearchSetingFragment.SAFESEARCH, "none");
    }

    public String getVideoDefinition() {
        if (videoDefinition == null)
            videoDefinition = PreferenceManager.getDefaultSharedPreferences(GlobalApplication.getInstance()).getString(SearchSetingFragment.VIDEODEFINITION, "any");
        return videoDefinition;
    }

    public static String getVideoDefinitionFromPre() {
        return PreferenceManager.getDefaultSharedPreferences(GlobalApplication.getInstance()).getString(SearchSetingFragment.VIDEODEFINITION, "any");
    }

    public String getVideoDuration() {
        if (videoDuration == null)
            videoDuration = PreferenceManager.getDefaultSharedPreferences(GlobalApplication.getInstance()).getString(SearchSetingFragment.VIDEODURATION, "any");
        return videoDuration;
    }

    public static String getVideoDurationFromPre() {
        return PreferenceManager.getDefaultSharedPreferences(GlobalApplication.getInstance()).getString(SearchSetingFragment.VIDEODURATION, "any");
    }

    public String getVideoType() {
        if (videoType == null)
            videoType = PreferenceManager.getDefaultSharedPreferences(GlobalApplication.getInstance()).getString(SearchSetingFragment.VIDEOTYPE, "any");
        return videoType;
    }

    public static String getVideoTypeFromPre() {
        return PreferenceManager.getDefaultSharedPreferences(GlobalApplication.getInstance()).getString(SearchSetingFragment.VIDEOTYPE, "any");
    }

    public void setMaxResults(String maxResults) {
        this.maxResults = maxResults;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public void setSafeSearch(String safeSearch) {
        this.safeSearch = safeSearch;
    }

    public void setVideoDefinition(String videoDefinition) {
        this.videoDefinition = videoDefinition;
    }

    public void setVideoDuration(String videoDuration) {
        this.videoDuration = videoDuration;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }
}
