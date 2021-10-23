package com.example.newslist.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDbOpenHelper extends SQLiteOpenHelper {
    private static final String SQL_CREATE_ENTRIES_1 =
            "CREATE TABLE " + ArticleContract.ArticleEntry.TABLE_NAME + " (" +
                    ArticleContract.ArticleEntry._ID + " INTEGER PRIMARY KEY, " +
                    ArticleContract.ArticleEntry.COLUMN_NAME_AID + " INT, " +
                    ArticleContract.ArticleEntry.COLUMN_NAME_URL + " VARCHAR(100), " +
                    ArticleContract.ArticleEntry.COLUMN_NAME_TITLE + " VARCHAR(100), " +
                    ArticleContract.ArticleEntry.COLUMN_NAME_AUTHORID + " INT, " +
                    ArticleContract.ArticleEntry.COLUMN_NAME_ARTICLECOVERURL + " VARCHAR(100), " +
                    ArticleContract.ArticleEntry.COLUMN_NAME_AUTHORACCOUNT + " VARCHAR(100), " +
                    ArticleContract.ArticleEntry.COLUMN_NAME_AUTHORNAME + " VARCHAR(100), " +
                    ArticleContract.ArticleEntry.COLUMN_NAME_AUTHORHEADURL + " VARCHAR(100) " +
                    ")";
    private static final String SQL_CREATE_ENTRIES_2 =
            "CREATE TABLE " + MsgTipContract.MsgTipEntry.TABLE_NAME + " (" +
                    MsgTipContract.MsgTipEntry._ID + " INTEGER PRIMARY KEY, " +
                    MsgTipContract.MsgTipEntry.COLUMN_NAME_FRIEND_NAME + " VARCHAR(100), " +
                    MsgTipContract.MsgTipEntry.COLUMN_NAME_FIRST_MSG + " VARCHAR(100), " +
                    MsgTipContract.MsgTipEntry.COLUMN_NAME_TYPE + " INT, " +
                    MsgTipContract.MsgTipEntry.COLUMN_NAME_HEAD_URL + " VARCHAR(100), " +
                    MsgTipContract.MsgTipEntry.COLUMN_NAME_CONTENT_URL + " VARCHAR(100), " +
                    MsgTipContract.MsgTipEntry.COLUMN_NAME_AID + " INT " +
                    ")";
//    private static final String SQL_CREATE_ENTRIES_3 =
//            "CREATE TABLE Chat (id INTEGER PRIMARY KEY," +
//                    "user_name varchar(64), " +
//                    "friend_name varchar(64), " +
//                    "msg_content varchar(256), " +
//                    "msg_date varchar(64), " +
//                    "msg_type integer);";

    private static final String SQL_DELETE_ENTRIES_1 =
            "DROP TABLE IF EXISTS " + ArticleContract.ArticleEntry.TABLE_NAME;
    private static final String SQL_DELETE_ENTRIES_2 =
            "DROP TABLE IF EXISTS " + MsgTipContract.MsgTipEntry.TABLE_NAME;
//    private static final String SQL_DELETE_ENTRIES_3 =
//            "DROP TABLE IF EXISTS " + "Chat";

    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "article.db";

    private Context mContext;

    public MyDbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES_1);
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES_2);
//        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES_3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES_1);
        db.execSQL(SQL_DELETE_ENTRIES_2);
//        db.execSQL(SQL_CREATE_ENTRIES_3);
        onCreate(db);
    }
}
