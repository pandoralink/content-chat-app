package com.example.newslist.news;

import com.google.gson.annotations.SerializedName;

public class AuthorInfoRequest {
    @SerializedName("authorName")
    private String authorName;
    @SerializedName("authorHeadUrl")
    private String authorHeadUrl;
    @SerializedName("relate")
    private Boolean authorRelate;

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorHeadUrl() {
        return authorHeadUrl;
    }

    public void setAuthorHeadUrl(String authorHeadUrl) {
        this.authorHeadUrl = authorHeadUrl;
    }

    public Boolean getAuthorRelate() {
        return authorRelate;
    }

    public void setAuthorRelate(Boolean authorRelate) {
        this.authorRelate = authorRelate;
    }
}
