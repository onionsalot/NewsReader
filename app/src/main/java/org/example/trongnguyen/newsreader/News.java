package org.example.trongnguyen.newsreader;

import java.io.Serializable;

public class News implements Serializable{
    // Class implements Serializable so that it may be passed in as an object for Intent
    String mTitle;
    String mAuthor;
    String mDate;
    String mSource;
    String mDescription;
    String mPicture;
    String mUrl;
    String mTags;

    public News(String title, String author, String date, String source, String description, String picture, String url, String tags) {
        mTitle = title;
        mAuthor = author;
        mDate = date;
        mSource = source;
        mDescription = description;
        mPicture = picture;
        mUrl = url;
        mTags = tags;
    }


    public String getTitle() {
        return mTitle;
    }
    public String getAuthor() {
        return mAuthor;
    }
    public String getDate() {
        return mDate;
    }
    public String getSource() {
        return mSource;
    }
    public String getDescription() {
        return mDescription;
    }
    public String getPicture() {
        return mPicture;
    }
    public String getUrl() {
        return mUrl;
    }
    public String getTags() {
        return mTags;
    }
}
