package com.example.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class SectionsLoader extends AsyncTaskLoader<List<String>> {

    String mUrl;
    public SectionsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<String> loadInBackground() {
        return QueryUtils.fetchSections(mUrl);
    }
}
