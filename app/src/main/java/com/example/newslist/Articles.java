package com.example.newslist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Articles {
    public String getUser_account() {
        return user_account;
    }

    public void setUser_account(String user_account) {
        this.user_account = user_account;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getArticle_cover_url() {
        return article_cover_url;
    }

    public void setArticle_cover_url(String article_cover_url) {
        this.article_cover_url = article_cover_url;
    }

    public Integer getaId() {
        return aId;
    }

    public void setaId(Integer aId) {
        this.aId = aId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public String getAuthorHeadUrl() {
        return authorHeadUrl;
    }

    public void setAuthorHeadUrl(String authorHeadUrl) {
        this.authorHeadUrl = authorHeadUrl;
    }

    public Articles(Integer aId, String article, String title, Integer authorId, String article_cover_url, String user_account, String user_name, String authorHeadUrl) {
        this.aId = aId;
        this.article = article;
        this.title = title;
        this.authorId = authorId;
        this.article_cover_url = article_cover_url;
        this.user_account = user_account;
        this.user_name = user_name;
        this.authorHeadUrl = authorHeadUrl;
    }

    @SerializedName("new_id")
    private Integer aId;
    @SerializedName("new_url")
    private String article;
    @SerializedName("new_name")
    private String title;
    @SerializedName("new_owner_id")
    private Integer authorId;
    private String article_cover_url = "";
    private String user_account;
    /**
     * 作者名称
     */
    private String user_name;
    @SerializedName("user_head")
    private String authorHeadUrl;
}
