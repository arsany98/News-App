package com.example.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.content.AsyncTaskLoader;

import java.util.List;

public class NewsStoryLoader extends AsyncTaskLoader<List<NewsStory>> {

    String mUrl;
    public NewsStoryLoader(@NonNull Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<NewsStory> loadInBackground() {
        return QueryUtils.fetchNewsStoriesData(mUrl);
    }
}
