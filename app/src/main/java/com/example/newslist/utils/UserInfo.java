package com.example.newslist.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.newslist.R;
import com.example.newslist.user.User;

import java.net.URI;
import java.net.URISyntaxException;

public class UserInfo {
    public static String account;
    public static String password;
    public static Integer userId;
    public static String userName;
    public static String userAccount;
    public static String userPassword;
    public static String userHead = "";
    private SharedPreferences spFile;
    private Context mContext;

    public UserInfo(Context context, User user) {
        mContext = context;
        String spFileName = context.getResources().getString(R.string.shared_preferences_file_name);
        String accountKey = context.getResources().getString(R.string.login_account_name);
        String passwordKey = context.getResources().getString(R.string.login_password);
        String userIdKey = context.getResources().getString(R.string.userId);
        String userNameKey = context.getResources().getString(R.string.userName);
        String userHeadKey = context.getResources().getString(R.string.userHead);
        spFile = context.getSharedPreferences(
                spFileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spFile.edit();

        account = spFile.getString(accountKey, null);
        password = spFile.getString(passwordKey, null);
        userId = user.getUser_id();
        userName = user.getUser_name();
        userAccount = user.getUser_account();
        userPassword = user.getUser_password();
        userHead = user.getUser_head();
        editor.putInt(userIdKey, userId);
        editor.putString(userHeadKey, userHead);
        editor.putString(userNameKey, userName);
        editor.apply();
    }
    public UserInfo(Context context) {
        this.mContext = context;
    }

    /**
     * 初始化 SharedPreferences
     * 中用户信息的 key
     */
    void initKey(Context context) {
        String spFileName = context.getResources().getString(R.string.shared_preferences_file_name);
        String accountKey = context.getResources().getString(R.string.login_account_name);
        String passwordKey = context.getResources().getString(R.string.login_password);
        String userIdKey = context.getResources().getString(R.string.userId);
        String userNameKey = context.getResources().getString(R.string.userName);
        String userHeadKey = context.getResources().getString(R.string.userHead);
    }

    public static void setAccount() {
        account = "123";
    }

    public int getUserId() {
        if (spFile != null && userId != null) {
            return userId;
        } else {
            String spFileName = mContext.getResources().getString(R.string.shared_preferences_file_name);
            spFile = mContext.getSharedPreferences(
                    spFileName,
                    Context.MODE_PRIVATE);
        }
        return 1;
    }


    public static URI appendUri(String uri, String appendQuery) throws URISyntaxException {
        URI oldUri = new URI(uri);
        return new URI(oldUri.getScheme(), oldUri.getAuthority(), oldUri.getPath(),
                oldUri.getQuery() == null ? appendQuery : oldUri.getQuery() + "&" + appendQuery, oldUri.getFragment());
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
