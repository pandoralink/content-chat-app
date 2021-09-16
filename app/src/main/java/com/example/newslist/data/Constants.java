package com.example.newslist.data;

/**
 * @author 庞旺
 */
public final class Constants {
    private Constants() {
    }

    public static String SERVER_URL = "http://116.63.152.202:5002/";
    public static String TEST_NEW_URL = "http://116.63.152.202:5002/News/demo.html";
    public static final String BACK_BASE_URL = "http://116.63.152.202:3002/";
    public static final String ARTICLE_URL = BACK_BASE_URL + "new";
    public static final String FOLLOW_AUTHOR_ARTICLE_URL = BACK_BASE_URL + "fan";
    public static final String ARTICLE_AUTHOR_INFO_BASE_URL = BACK_BASE_URL + "authorInfo";
    public static final String USER_ARTICLE_URL = BACK_BASE_URL + "userArticle";
    public static String ARTICLE_URL_KEY = "article_url_key";
    public static String ARTICLE_AUTHOR_INFO_KEY = "article_author_info_key";
    public static String ARTICLE_NAME_KEY = "article_name_key";
    public static String AUTHOR_NAME_KEY = "author_name_key";
    public static String AUTHOR_HEAD_URL_KEY = "author_head_url_key";
    public static String AUTHOR_ACCOUNT_KEY = "author_account_key";
    public static String USER_RELATE_KEY = "user_relate_key";
    public static String AUTHOR_FAN_TOTAL_KEY = "user_relate_key";
//    public static String LOCAL_WEBSOCKET_URL = "ws://10.0.2.2:8181";
    public static String LOCAL_WEBSOCKET_URL = "ws://10.0.2.2:8181";
    public static String REMOTE_WEBSOCKET_URL = "ws://116.63.152.202:8181";
}
