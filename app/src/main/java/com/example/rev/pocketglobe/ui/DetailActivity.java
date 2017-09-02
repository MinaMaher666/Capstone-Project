package com.example.rev.pocketglobe.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rev.pocketglobe.R;
import com.example.rev.pocketglobe.model.Article;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {
    public static final String TAG = DetailActivity.class.getSimpleName();

    @BindView(R.id.article_title) TextView articleTitleTV;
    @BindView(R.id.article_description) TextView articleDesTV;
    @BindView(R.id.article_date) TextView articleDateTV;
    @BindView(R.id.article_image) ImageView articleImageIV;
    @BindView(R.id.article_url) TextView articleUrlTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Article mainArticle = getIntent().getParcelableExtra(ArticlesFragment.EXTRA_ARTICLE);
        Log.i(TAG, "onCreate: " + mainArticle.getmDescription());

        ButterKnife.bind(this);

        setViews(mainArticle);
    }

    void setViews(Article mainArticle) {
        String articleTitle = mainArticle.getmTitle();
        String articleDate = mainArticle.getmDate();
        String imageUrl = mainArticle.getmImageUrl();
        final String articleUrl = mainArticle.getmUrl();
        String description = mainArticle.getmDescription();

        articleTitleTV.setText(articleTitle);
        articleDesTV.setText(description);
        articleDateTV.setText(articleDate);
        Picasso.with(DetailActivity.this)
                .load(imageUrl).into(articleImageIV);

        articleUrlTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewOnlineIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(articleUrl));
                if(getPackageManager().resolveActivity(viewOnlineIntent, 0) != null) {
                    startActivity(viewOnlineIntent);
                }
            }
        });
    }
}