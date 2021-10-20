package com.example.newslist.data;

import android.provider.BaseColumns;

public final class ArticleContract {
    private ArticleContract() {
    }

    public static class ArticleEntry implements BaseColumns {
        /**
         * tbl_article_cache：用户浏览记录
         */
        public static final String TABLE_NAME = "tbl_article_cache";
        public static final String COLUMN_NAME_AID = "aid";
        public static final String COLUMN_NAME_URL = "aurl";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_AUTHORID = "authorId";
        public static final String COLUMN_NAME_ARTICLECOVERURL = "articleCoverUrl";
        public static final String COLUMN_NAME_AUTHORACCOUNT = "userAccount";
        public static final String COLUMN_NAME_AUTHORNAME = "userName";
        public static final String COLUMN_NAME_AUTHORHEADURL = "authorHeadUrl";
    }
}
