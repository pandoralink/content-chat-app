package com.example.newslist.data;

import com.google.gson.annotations.SerializedName;

/**
 * @author 庞旺
 */
public class BaseResponse<T> {
    private int code;
    private String msg;

    public final static int RESPONSE_SUCCESS = 0;

    @SerializedName("data")
    private T data;

    public BaseResponse() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
