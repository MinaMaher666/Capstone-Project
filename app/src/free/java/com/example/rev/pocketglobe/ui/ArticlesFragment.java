package com.example.rev.pocketglobe.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rev.pocketglobe.R;
import com.example.rev.pocketglobe.adapters.ArticlesAdapter;
import com.example.rev.pocketglobe.adapters.SortPagerAdapter;
import com.example.rev.pocketglobe.data.Article;
import com.example.rev.pocketglobe.data.Source;
import com.example.rev.pocketglobe.utils.ContentProviderUtils;
import com.example.rev.pocketglobe.utils.JsonUtils;
import com.example.rev.pocketglobe.utils.NetworkUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ArticlesFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Article>>,
        ArticlesAdapter.ArticleOnCLickListener{

    public static final String TAG = ArticlesFragment.class.getSimpleName();
    public static final String SOURCE = "source";
    public static final String SORTED_BY = "sorted_by";
    public static final String EXTRA_ARTICLE = "article";
    public boolean isConnected = false;

    private Source mSource;
    private String mSortBy;
    private ArticlesAdapter mAdapter;
    private ArrayList<Article> mArticles;
    private int mLoaderId;

    @BindView(R.id.articles_rv) RecyclerView articlesRecyclerView;
    @BindView(R.id.empty_article_tv) TextView emptyArticleTV;
    @BindView(R.id.adView) AdView adView;

    public ArticlesFragment() {
    }

    @Override
    public void setArguments(Bundle args) {
        mSource = args.getParcelable(SortPagerAdapter.SOURCE);
        mSortBy = args.getString(SortPagerAdapter.SORT);
        mLoaderId = args.getInt(SortPagerAdapter.LOADER_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_articles, container, false);
        ButterKnife.bind(this, rootView);

        AdRequest adRequest = new AdRequest.Builder().build();

        adView.loadAd(adRequest);

        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(SOURCE)) {
                mSource = savedInstanceState.getParcelable(SOURCE);
            }
            if (savedInstanceState.containsKey(SORTED_BY)) {
                mSortBy = savedInstanceState.getString(SORTED_BY);
            }
        }

        mArticles = new ArrayList<>();
        mAdapter = new ArticlesAdapter(mArticles, this);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(numberOfColumns(),
                StaggeredGridLayoutManager.VERTICAL);

        articlesRecyclerView.setLayoutManager(layoutManager);
        articlesRecyclerView.setAdapter(mAdapter);

        if (!NetworkUtils.isConnected(getContext())) {
            Toast.makeText(getContext(), getString(R.string.no_network_message), Toast.LENGTH_SHORT).show();
        } else {
            isConnected = true;
        }
        Bundle loaderBundle = new Bundle();
        loaderBundle.putString(SOURCE, mSource.getmId());
        loaderBundle.putString(SORTED_BY, mSortBy);

        getActivity().getSupportLoaderManager().initLoader(mLoaderId, loaderBundle, this);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(SOURCE, mSource);
        outState.putString(SORTED_BY, mSortBy);
        super.onSaveInstanceState(outState);
    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int id, final Bundle args) {
        final Context context = getContext();
        return new AsyncTaskLoader<List<Article>>(context) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }

            @Override
            public List<Article> loadInBackground() {
                String jsonResponse = NetworkUtils.getArticlesJsonResponse(context,
                        args.getString(SOURCE),
                        args.getString(SORTED_BY));
                if(isConnected) {
                    return JsonUtils.extractArticlesFromJson(jsonResponse, context);
                } else {
                    return ContentProviderUtils.getArticlesFromProvider(getContext(), mSource.getmId(), mSortBy);
                }
            }
        };
    }

    void showEmptyListView() {
        articlesRecyclerView.setVisibility(View.GONE);
        emptyArticleTV.setVisibility(View.VISIBLE);
    }

    void hideEmptyListView() {
        emptyArticleTV.setVisibility(View.GONE);
        articlesRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, final List<Article> data) {
        if(data ==null || data.size() == 0) {
            showEmptyListView();
        } else {
            if (isConnected) {
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        ContentProviderUtils.insertArticlesIntoContentProvider(getContext(), data, mSource.getmId(), mSortBy);
                        return null;
                    }
                }.execute();
            }
            mArticles.clear();
            mArticles.addAll(data);
            mAdapter.notifyDataSetChanged();
            hideEmptyListView();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
    }

    @Override
    public void onCLick(int position) {
        Article currentArticle = mArticles.get(position);
        Intent detailIntent = new Intent(getContext(), DetailActivity.class);
        detailIntent.putExtra(EXTRA_ARTICLE, currentArticle);

        startActivity(detailIntent);
    }
}
