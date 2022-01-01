package com.example.newslist.message.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ListSQLiteHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "chatList.db";

    public ListSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ChatList (id INTEGER PRIMARY KEY," +
                "friend_name varchar(64), " +
                "friend_id integer);");
        ContentValues values = new ContentValues();
        values.put("friend_name", "庞老闆");
        values.put("msg_content", 1005);
        db.insert("ChatList", null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS ChatList");
        onCreate(db);
    }
}
