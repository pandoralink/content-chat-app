package com.example.newslist.message;

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

    public int getHead() {
        return head;
    }

    public void setHead(int head) {
        this.head = head;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private String friendName;
    private String firstMsg;
    private int head;
    /**
     * type：1 信息
     * type：2 通知
     * 默认值为 1
     */
    private int type = 1;
}
