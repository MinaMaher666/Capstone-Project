package com.example.rev.pocketglobe.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rev.pocketglobe.R;
import com.example.rev.pocketglobe.adapters.ArticlesAdapter;
import com.example.rev.pocketglobe.model.Article;
import com.example.rev.pocketglobe.model.Source;
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

    private Source mSource;
    private String mSortBy;
    private ArticlesAdapter mAdapter;
    private ArrayList<Article> mArticles;
    private int mLoaderId;

    @BindView(R.id.articles_rv) RecyclerView articlesRecyclerView;
    @BindView(R.id.empty_article_tv) TextView emptyArticleTV;
    @BindView(R.id.adView) AdView adView;


    public ArticlesFragment(Source source, String sortby, int loaderId) {
        mSource = source;
        mSortBy = sortby;
        mLoaderId = loaderId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_articles, container, false);
        ButterKnife.bind(this, rootView);

        AdRequest adRequest = new AdRequest.Builder().build();

        adView.loadAd(adRequest);

        mArticles = new ArrayList<>();
        mAdapter = new ArticlesAdapter(mArticles, this);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);

        articlesRecyclerView.setLayoutManager(layoutManager);
        articlesRecyclerView.setAdapter(mAdapter);

        if (!NetworkUtils.isConnected(getContext())) {

        } else {
            Bundle loaderBundle = new Bundle();
            loaderBundle.putString(SOURCE, mSource.getmId());
            loaderBundle.putString(SORTED_BY, mSortBy);

            getActivity().getSupportLoaderManager().restartLoader(mLoaderId, loaderBundle, this);
        }

        return rootView;
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int id, final Bundle args) {
        Log.i(TAG, "onCreateLoader: ");
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
                Log.i(TAG, "loadInBackground: " + NetworkUtils.buildArticlesUrl(context, args.getString(SOURCE),
                        args.getString(SORTED_BY)));
                return JsonUtils.extractArticlesFromJson(jsonResponse, context);
            }
        };
    }

    void showEmptyListView() {
        articlesRecyclerView.setVisibility(View.GONE);
        emptyArticleTV.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> data) {
        mArticles.clear();
        mArticles.addAll(data);
        if(data.size() == 0) {
            showEmptyListView();
        }
        Log.i(TAG, "onLoadFinished: " + mArticles.size());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        mArticles.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCLick(int position) {
        Article currentArticle = mArticles.get(position);
        Intent detailIntent = new Intent(getContext(), DetailActivity.class);
        detailIntent.putExtra(EXTRA_ARTICLE, currentArticle);

        startActivity(detailIntent);
    }
}
