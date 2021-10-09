package com.example.newslist.utils;

import android.content.Context;

import com.example.newslist.R;

/**
 * 封装用户信息管理的一些方法
 * 由于 UserInfo 有些问题，
 * 于是再创建一个新的
 */
public class UserInfoManager {
    private Integer userId;
    private String userName;
    private String userAccount;
    private String userPassword;
    private String userHead = "";
    private String spFileName;
    private Context mContext;
    private String accountKey;
    private String passwordKey;
    private String userIdKey;
    private String userNameKey;
    private String userHeadKey;

    public UserInfoManager(Context context) {
        this.mContext = context;
        initKey(context);
    }

    /**
     * 初始化 SharedPreferences
     * 中用户信息的 key
     */
    private void initKey(Context context) {
        spFileName = context.getResources().getString(R.string.shared_preferences_file_name);
        accountKey = context.getResources().getString(R.string.login_account_name);
        passwordKey = context.getResources().getString(R.string.login_password);
        userIdKey = context.getResources().getString(R.string.userId);
        userNameKey = context.getResources().getString(R.string.userName);
        userHeadKey = context.getResources().getString(R.string.userHead);
    }

    /**
     * 三行变两行
     * @return
     */
    public String getAccount() {
        return mContext.getSharedPreferences(spFileName, Context.MODE_PRIVATE).getString(accountKey, null);
    }

    private String getPassword() {
        return mContext.getSharedPreferences(spFileName, Context.MODE_PRIVATE).getString(passwordKey, null);
    }

    public Integer getUserId() {
        return mContext.getSharedPreferences(spFileName, Context.MODE_PRIVATE).getInt(userIdKey, 0);
    }

    private String getUserName() {
        return mContext.getSharedPreferences(spFileName, Context.MODE_PRIVATE).getString(userNameKey, null);
    }


    public static Integer getIntegerValue(Context context, String fileName, String key) {
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE).getInt(key, 0);
    }

    private Integer getIntegerValue(String fileName, String key) {
        return mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE).getInt(key, 0);
    }

    private static String getStringValue(Context context, String fileName, String key) {
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE).getString(key, null);
    }

    private String getStringValue(String fileName, String key) {
        return mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE).getString(key, null);
    }
}
