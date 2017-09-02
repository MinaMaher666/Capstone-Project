package com.example.rev.pocketglobe.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Article implements Parcelable{
    private String mAuther;
    private String mTitle;
    private String mDescription;
    private String mUrl;
    private String mImageUrl;
    private String mDate;

    public Article(String mAuther, String mTitle, String mDescription, String mUrl, String mImageUrl, String mDate) {
        this.mAuther = mAuther;
        this.mTitle = mTitle;
        this.mDescription = mDescription;
        this.mUrl = mUrl;
        this.mImageUrl = mImageUrl;
        this.mDate = mDate;
    }

    protected Article(Parcel in) {
        mAuther = in.readString();
        mTitle = in.readString();
        mDescription = in.readString();
        mUrl = in.readString();
        mImageUrl = in.readString();
        mDate = in.readString();
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    public String getmAuther() {
        return mAuther;
    }

    public void setmAuther(String mAuther) {
        this.mAuther = mAuther;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mAuther);
        parcel.writeString(mTitle);
        parcel.writeString(mDescription);
        parcel.writeString(mUrl);
        parcel.writeString(mImageUrl);
        parcel.writeString(mDate);
    }
}
