package com.example.rev.pocketglobe.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.example.rev.pocketglobe.data.Article;
import com.example.rev.pocketglobe.R;
import com.example.rev.pocketglobe.utils.ContentProviderUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {
    public static final String TAG = AppWidget.class.getSimpleName();
    private static Target target;
    static void updateAppWidget(Context context, final AppWidgetManager appWidgetManager,
                                final int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
//        views.setTextViewText(R.id.appwidget_text, widgetText);
        final Article article = ContentProviderUtils.getLatestArticle(context);
        if (article == null) {
            showEmptyArticlesView(views);
        } else {
            hideEmptyArticlesView(views);


//        if(target == null) {
//            Target target = new Target() {
//                @Override
//                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                    Log.i(TAG, "onBitmapLoaded: ");
//                    views.setImageViewBitmap(R.id.widget_article_image, bitmap);
//                    appWidgetManager.updateAppWidget(appWidgetId, views);
//                }
//
//                @Override
//                public void onBitmapFailed(Drawable errorDrawable) {
//                    views.setImageViewResource(R.id.widget_article_image, R.drawable.error_image);
//                    appWidgetManager.updateAppWidget(appWidgetId, views);
//                }
//
//                @Override
//                public void onPrepareLoad(Drawable placeHolderDrawable) {
//                }
//            };
//        }
//
//        Picasso.with(context).load(article.getmImageUrl()).error(R.drawable.error_image).into(target);


        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... voids) {
                try {
                    InputStream is = new java.net.URL(article.getmImageUrl()).openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    return bitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if(isCancelled()) {
                    bitmap = null;
                }
                views.setImageViewBitmap(R.id.widget_article_image, bitmap);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }.execute();

        views.setImageViewResource(R.id.widget_article_image, R.drawable.error_image);
        views.setTextViewText(R.id.widget_article_title, article.getmTitle());
        views.setTextViewText(R.id.widget_article_author, article.getmAuthor());
        views.setTextViewText(R.id.widget_article_date, article.getmDate());
        views.setTextViewText(R.id.widget_article_description, article.getmDescription());
        // Instruct the widget manager to update the widget
        }
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    static void showEmptyArticlesView(RemoteViews views) {
        views.setViewVisibility(R.id.widget_article_image, View.GONE);
        views.setViewVisibility(R.id.widget_article_title, View.GONE);
        views.setViewVisibility(R.id.widget_article_author, View.GONE);
        views.setViewVisibility(R.id.widget_article_date, View.GONE);
        views.setViewVisibility(R.id.widget_article_description, View.GONE);
        views.setViewVisibility(R.id.empty_widget_tv, View.VISIBLE);
    }

    static void hideEmptyArticlesView(RemoteViews views) {
        views.setViewVisibility(R.id.empty_widget_tv, View.GONE);
        views.setViewVisibility(R.id.widget_article_image, View.VISIBLE);
        views.setViewVisibility(R.id.widget_article_title, View.VISIBLE);
        views.setViewVisibility(R.id.widget_article_author, View.VISIBLE);
        views.setViewVisibility(R.id.widget_article_date, View.VISIBLE);
        views.setViewVisibility(R.id.widget_article_description, View.VISIBLE);
    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

