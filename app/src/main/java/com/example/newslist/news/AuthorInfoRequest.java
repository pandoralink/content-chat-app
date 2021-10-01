package com.example.newslist.news;

import com.google.gson.annotations.SerializedName;

public class AuthorInfoRequest {
    private int fanTotal;
    @SerializedName("relate")
    private Boolean authorRelate;

    public int getFanTotal() {
        return fanTotal;
    }

    public void setFanTotal(int fanTotal) {
        this.fanTotal = fanTotal;
    }

    public Boolean getAuthorRelate() {
        return authorRelate;
    }

    public void setAuthorRelate(Boolean authorRelate) {
        this.authorRelate = authorRelate;
    }
}
