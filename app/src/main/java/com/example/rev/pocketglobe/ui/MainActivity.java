package com.example.rev.pocketglobe.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rev.pocketglobe.R;
import com.example.rev.pocketglobe.adapters.SourcesAdapter;
import com.example.rev.pocketglobe.model.Source;
import com.example.rev.pocketglobe.utils.JsonUtils;
import com.example.rev.pocketglobe.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Source>>, SourcesAdapter.SourceOnClickListener{
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int SOURCES_MANAGER_ID = 666;
    public static final String EXTRA_SOURCE = "source";
    private Toast mToast;
    private ArrayList<Source> mSources;
    private SourcesAdapter mAdapter;

    @BindView(R.id.sources_rv) RecyclerView sourcesRecyclerView;
    @BindView(R.id.loading_indicator) ProgressBar loadingIndicator;
    @BindView(R.id.empty_sources_list) TextView emptyListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context context = MainActivity.this;
        ButterKnife.bind(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        mSources = new ArrayList<>();
        mAdapter = new SourcesAdapter(mSources, this);
        sourcesRecyclerView.setLayoutManager(layoutManager);
        sourcesRecyclerView.setAdapter(mAdapter);

        if(!NetworkUtils.isConnected(context)) {
            showNoNetworkToast();
            showEmptyListView();
        } else {
            getSupportLoaderManager().initLoader(SOURCES_MANAGER_ID, null, this);
        }
    }

    void showNoNetworkToast() {
        if(mToast != null)
            mToast.cancel();

        mToast = Toast.makeText(MainActivity.this, getString(R.string.no_network_message), Toast.LENGTH_SHORT);
        mToast.show();
    }

    void showLoadingIndicator() {
        sourcesRecyclerView.setVisibility(View.GONE);
        emptyListView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    void hideLoadingIndicator() {
        loadingIndicator.setVisibility(View.GONE);
        emptyListView.setVisibility(View.GONE);
        sourcesRecyclerView.setVisibility(View.VISIBLE);

    }

    void showEmptyListView() {
        sourcesRecyclerView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.GONE);
        emptyListView.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<List<Source>> onCreateLoader(int id, Bundle args) {
        final Context context = MainActivity.this;
        return new AsyncTaskLoader<List<Source>>(context) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
                showLoadingIndicator();
            }

            @Override
            public List<Source> loadInBackground() {
                String jsonResponse = NetworkUtils.getSourcesJsonResponse(context);
                return JsonUtils.extractSourcesFromJson(jsonResponse, context);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Source>> loader, List<Source> data) {
        if(data == null || data.size() ==0) {
            showEmptyListView();
        } else {
            mSources.clear();
            mSources.addAll(data);
            mAdapter.notifyDataSetChanged();
            hideLoadingIndicator();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Source>> loader) {
    }

    @Override
    public void onClick(int position) {
        Intent articleIntent = new Intent(MainActivity.this, ArticlesActivity.class);
        articleIntent.putExtra(EXTRA_SOURCE, mSources.get(position));
        startActivity(articleIntent);
    }
}
