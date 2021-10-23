 package com.example.newslist.message.core;

public class Msg {
    public static final int TYPE_RECEIVED=0;
    public static final int TYPE_SENT =1;
    private String content;
    private int type;

    public String getContent(){
        return content;
    }

    public int getType(){
        return type;
    }

    public Msg(String content, int type){
        this.content=content;
        this.type=type;
    }
}
