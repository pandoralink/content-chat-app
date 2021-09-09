package com.example.newslist.news;

import com.google.gson.annotations.SerializedName;

public class AuthorInfo {
    @SerializedName("new_id")
    private Integer aId;
    @SerializedName("new_name")
    private String title;
    @SerializedName("new_url")
    private String article;
    @SerializedName("new_owner_id")
    private Integer authorId;
    @SerializedName("new_owner_head_url")
    private String authorHeadUrl;
}
