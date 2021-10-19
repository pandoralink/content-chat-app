package com.example.newslist.utils;

public class Author {
    int uid = 0;
    String uname = "";
    String uheadUrl = "";

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUheadUrl() {
        return uheadUrl;
    }

    public void setUheadUrl(String uheadUrl) {
        this.uheadUrl = uheadUrl;
    }

    @Override
    public String toString() {
        return "Author{" +
                "uid=" + uid +
                ", uname='" + uname + '\'' +
                ", uheadUrl='" + uheadUrl + '\'' +
                '}';
    }
}
