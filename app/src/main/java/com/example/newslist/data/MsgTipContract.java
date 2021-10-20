package com.example.newslist.data;

import android.provider.BaseColumns;

/**
 * 通知/提醒合约类
 */
public class MsgTipContract {
    private MsgTipContract() {
    }

    public static class MsgTipEntry implements BaseColumns {
        /**
         * tbl_article_cache：用户浏览记录
         */
        public static final String TABLE_NAME = "tbl_msg_tip_cache";
        public static final String COLUMN_NAME_FRIEND_NAME = "friendName";
        public static final String COLUMN_NAME_FIRST_MSG = "firstMsg";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_HEAD_URL = "headUrl";
        public static final String COLUMN_NAME_CONTENT_URL = "contentUrl";
        public static final String COLUMN_NAME_AID = "aid";
    }
}
