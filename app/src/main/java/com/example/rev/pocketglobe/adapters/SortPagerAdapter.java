package com.example.rev.pocketglobe.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.rev.pocketglobe.R;
import com.example.rev.pocketglobe.data.Source;
import com.example.rev.pocketglobe.ui.ArticlesFragment;


public class SortPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private Source mSource;
    private String[] mTabs;

    public static final int ARTICLES_LOADER_BASE_ID = 777;
    public SortPagerAdapter(FragmentManager fm, Context context, Source source) {
        super(fm);

        mContext = context;
        mSource = source;

        mTabs = new String[]{mContext.getString(R.string.url_sortedby_top),
                mContext.getString(R.string.url_sortedby_latest),
                mContext.getString(R.string.url_sortedby_popular)};
    }

    @Override
    public Fragment getItem(int position) {
        return new ArticlesFragment(mSource, mTabs[position], ARTICLES_LOADER_BASE_ID+position);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs[position];
    }


}
