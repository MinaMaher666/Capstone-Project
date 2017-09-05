package com.example.rev.pocketglobe.ui;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.rev.pocketglobe.R;
import com.example.rev.pocketglobe.adapters.SortPagerAdapter;
import com.example.rev.pocketglobe.data.Source;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticlesActivity extends AppCompatActivity {
    public static final String TAG = ArticlesActivity.class.getSimpleName();
    private SortPagerAdapter mPagerAdapter;

    @BindView(R.id.sort_vp) ViewPager sortViewPager;
    @BindView(R.id.sliding_tabs) TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles);

        ButterKnife.bind(this);

        Source extraSource =  getIntent().getParcelableExtra(MainActivity.EXTRA_SOURCE);
        String sourceName = extraSource.getmName();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(sourceName);
        mPagerAdapter = new SortPagerAdapter(getSupportFragmentManager(), this, extraSource);

        sortViewPager.setAdapter(mPagerAdapter);
        tabLayout.setupWithViewPager(sortViewPager);
    }
}
