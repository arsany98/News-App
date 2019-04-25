package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String API_URL = "https://content.guardianapis.com/search?api-key=cba33846-de4b-43bf-89e2-325701869801&show-tags=contributor";
    private static final String SECTIONS = "https://content.guardianapis.com/sections?api-key=cba33846-de4b-43bf-89e2-325701869801";
    private static final String LOG_TAG = MainActivity.class.getName();

    SwipeRefreshLayout mRefreshLayout;
    EditText mQueryEditText;
    Spinner mQuerySpinner;
    List<String> mSectionNames;
    Button mSearchButton;
    List<NewsStory> mNewsStories;
    NewsStoryAdapter mAdapter;
    ListView mNewsListView;
    TextView mEmptyStateTextView;
    ArrayAdapter<String> adapter;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNewsStories = new ArrayList<>();
        mNewsListView = findViewById(R.id.news_list_view);
        mAdapter = new NewsStoryAdapter(this,R.layout.list_item,mNewsStories);
        mEmptyStateTextView = findViewById(R.id.empty_view);
        mQueryEditText = findViewById(R.id.query_edit_text);
        mSearchButton = findViewById(R.id.search_btn);
        mQuerySpinner = findViewById(R.id.query_spinner);
        mRefreshLayout = findViewById(R.id.refresh_layout);

        mNewsListView.setAdapter(mAdapter);
        mNewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String url = mNewsStories.get(i).getmUrl();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        mNewsListView.setEmptyView(mEmptyStateTextView);

        mSectionNames = new ArrayList<>();
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,mSectionNames);
        mQuerySpinner.setAdapter(adapter);

        mRefreshLayout.setRefreshing(true);
        bundle = new Bundle();
        load(bundle);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String q = mQueryEditText.getText().toString();
                String sectionId = mQuerySpinner.getSelectedItem().toString();
                bundle.putString("q",q);
                bundle.putString("section_id",sectionId);
                getLoaderManager().restartLoader(0,bundle,newsStoriesLoader);
            }
        });

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLoaderManager().restartLoader(0,bundle,newsStoriesLoader);
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if(s.equals(getString(R.string.order_by_key))||s.equals(getString(R.string.page_size_key)))
        {
            mAdapter.clear();
            mEmptyStateTextView.setVisibility(View.GONE);
            mRefreshLayout.setRefreshing(true);
            getLoaderManager().restartLoader(0,null,newsStoriesLoader);
        }
    }

    public void load(Bundle bundle)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected())
        {
            getLoaderManager().initLoader(0,bundle,newsStoriesLoader);
            getLoaderManager().initLoader(1,null,sectionsLoader);
        }
        else
        {
            mRefreshLayout.setRefreshing(false);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    private LoaderManager.LoaderCallbacks<List<NewsStory>> newsStoriesLoader = new LoaderManager.LoaderCallbacks<List<NewsStory>>() {
        @NonNull
        @Override
        public Loader<List<NewsStory>> onCreateLoader(int i, @Nullable Bundle bundle) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            String orderBy = prefs.getString(getString(R.string.order_by_key),"newest");
            String pageSize = prefs.getString(getString(R.string.page_size_key),"10");

            Uri baseUri = Uri.parse(API_URL);
            Uri.Builder builder = baseUri.buildUpon();

            if(bundle!=null)
            {
                String q = bundle.getString("q");
                if(q!=null && !q.isEmpty())
                    builder.appendQueryParameter("q",q);
                String sectionId = bundle.getString("section_id");
                if(sectionId!=null && !sectionId.isEmpty() && !sectionId.equals("All"))
                    builder.appendQueryParameter("section",sectionId);
            }

            builder.appendQueryParameter("order-by",orderBy);
            builder.appendQueryParameter("page-size",pageSize);

            return new NewsStoryLoader(MainActivity.this,builder.toString());
        }

        @Override
        public void onLoadFinished(@NonNull Loader<List<NewsStory>> loader, List<NewsStory> newsStories) {

            mRefreshLayout.setRefreshing(false);
            mEmptyStateTextView.setText(R.string.no_news);
            mAdapter.clear();
            mAdapter.addAll(newsStories);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<List<NewsStory>> loader) {
            mAdapter.clear();
        }

    };

    private LoaderManager.LoaderCallbacks<List<String>> sectionsLoader = new LoaderManager.LoaderCallbacks<List<String>>() {
        @NonNull
        @Override
        public Loader<List<String>> onCreateLoader(int i, @Nullable Bundle bundle) {

            return new SectionsLoader(MainActivity.this,SECTIONS);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<List<String>> loader, List<String> sections) {

            mRefreshLayout.setRefreshing(false);
            mEmptyStateTextView.setText(R.string.no_news);
            adapter.clear();
            adapter.add("All");
            adapter.addAll(sections);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<List<String>> loader) {
            adapter.clear();
        }

    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== R.id.settings_item)
        {
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
