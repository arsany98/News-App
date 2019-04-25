package com.example.newsapp;

import java.util.List;

public class NewsStory {
    static List<String> sSections;
    private String mSectionId;
    private String mSectionName;
    private String mTitle;
    private String mUrl;
    private String mPublicationDate;
    private String mAuthorName;

    public NewsStory(String mSectionId,String mSectionName, String mTitle, String mUrl, String mPublicationDate ,String mAuthorName) {
        this.mSectionId = mSectionId;
        this.mSectionName = mSectionName;
        this.mTitle = mTitle;
        this.mUrl = mUrl;
        this.mPublicationDate = mPublicationDate;
        this.mAuthorName = mAuthorName;
    }

    public String getmSectionId() {
        return mSectionId;
    }

    public void setmSectionId(String mSectionId) {
        this.mSectionId = mSectionId;
    }

    public String getmSectionName() {
        return mSectionName;
    }

    public void setmSectionName(String mSectionName) {
        this.mSectionName = mSectionName;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getmPublicationDate() {
        return mPublicationDate;
    }

    public void setmPublicationDate(String mPublicationDate) {
        this.mPublicationDate = mPublicationDate;
    }

    public String getmAuthorName() {
        return mAuthorName;
    }

    public void setmAuthorName(String mAuthorName) {
        this.mAuthorName = mAuthorName;
    }
}
