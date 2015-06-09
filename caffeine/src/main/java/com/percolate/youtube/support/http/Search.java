package com.percolate.youtube.support.http;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.percolate.youtube.support.util.LogUtil;
import com.percolate.youtube.support.util.SearchSetting;

import java.io.IOException;

public class Search {

    public static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();

    public static final JsonFactory JSON_FACTORY = new AndroidJsonFactory();

    public static String apiKey = "AIzaSyANvuaYUUPEE-yElePL92QrhBiZ6btN9V0";

    private static YouTube youtube;

    static {
        youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName("Sensication").build();
    }

    public static SearchListResponse search(String queryTerm) {

        try {
            YouTube.Search.List search = youtube.search().list("id,snippet")
                    .setKey(apiKey)
                    .setQ(queryTerm)
                    .setType("video")
                    .setFields("items(id(videoId),snippet(title,description,thumbnails/default/url))")
                    .setMaxResults(Long.valueOf(SearchSetting.getInstance().getMaxResults()))
                    .setOrder(SearchSetting.getInstance().getOrder())
                    .setSafeSearch(SearchSetting.getInstance().getSafeSearch())
                    .setVideoDefinition(SearchSetting.getInstance().getVideoDefinition())
                    .setVideoDuration(SearchSetting.getInstance().getVideoDuration())
                    .setVideoType(SearchSetting.getInstance().getVideoType());
            return search.execute();
        } catch (GoogleJsonResponseException e) {
            LogUtil.d("There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
        } catch (IOException e) {
            LogUtil.d("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            LogUtil.d(t);
        }

        return null;
    }

}
