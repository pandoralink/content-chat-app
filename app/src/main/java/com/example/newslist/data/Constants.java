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
    public static String ARTICLE_URL_KEY = "article_url_key";
    public static String ARTICLE_AUTHOR_INFO_KEY = "article_author_info_key";
}
