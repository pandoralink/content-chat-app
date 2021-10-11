package com.example.newslist.data;

import com.google.gson.annotations.SerializedName;

/**
 * new 表对应泛型
 */
public class Article {
    public Integer getNewId() {
        return newId;
    }

    public void setNewId(Integer newId) {
        this.newId = newId;
    }

    public String getNewUrl() {
        return newUrl;
    }

    public void setNewUrl(String newUrl) {
        this.newUrl = newUrl;
    }

    public Integer getNewOwnerId() {
        return newOwnerId;
    }

    public void setNewOwnerId(Integer newOwnerId) {
        this.newOwnerId = newOwnerId;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getArticleCoverUrl() {
        return articleCoverUrl;
    }

    public void setArticleCoverUrl(String articleCoverUrl) {
        this.articleCoverUrl = articleCoverUrl;
    }

    @SerializedName("new_id")
    private Integer newId;
    @SerializedName("new_url")
    private String newUrl;
    @SerializedName("new_owner_id")
    private Integer newOwnerId;
    @SerializedName("new_name")
    private String newName;
    @SerializedName("article_cover_url")
    private String articleCoverUrl;
}
