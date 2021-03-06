package com.example.newslist.message;

import com.google.gson.annotations.SerializedName;

/**
 * @author 庞旺
 */
public class Messages {
    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getFirstMsg() {
        return firstMsg;
    }

    public void setFirstMsg(String firstMsg) {
        this.firstMsg = firstMsg;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public Integer getAid() {
        return aid;
    }

    public void setAid(Integer aid) {
        this.aid = aid;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Messages(String friendName, String firstMsg, int type, String headUrl, String contentUrl, Integer aid) {
        this.friendName = friendName;
        this.firstMsg = firstMsg;
        this.type = type;
        this.headUrl = headUrl;
        this.contentUrl = contentUrl;
        this.aid = aid;
    }

    public Messages() {

    }

    private String friendName;
    private String firstMsg;
    private int userId;
    /**
     * type：1 信息
     * type：2 通知
     * 默认值为 1
     */
    private int type = 1;
    /**
     * 消息/通知左侧图片链接
     * 默认为 "" 此时会隐藏图片
     */
    private String headUrl = "";
    /**
     * 通知指引链接，一般为文章链接
     * 消息的 contentUrl 为空
     * 默认为 ""
     */
    private String contentUrl = "";
    private Integer aid = 0;
}
