package com.example.newslist.data.local;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.newslist.message.Messages;
import com.example.newslist.message.core.Msg;
import com.example.newslist.message.core.MySQLiteHelper;

import java.util.ArrayList;
import java.util.List;

public class MsgLocalDataSource {
    private MySQLiteHelper mySQLiteHelper;

    private static MsgLocalDataSource INSTANCE;

    public MsgLocalDataSource(@NonNull Context context) {
        mySQLiteHelper = new MySQLiteHelper(context, "chat.db", null, 1);
    }

    public static MsgLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new MsgLocalDataSource(context);
        }
        return INSTANCE;
    }

    public List<Msg> getMsgList(String friendName) {
        List<Msg> messages = new ArrayList<Msg>();
        String msg_content;
        String msg_date;
        int msg_type;
        SQLiteDatabase db = mySQLiteHelper.getReadableDatabase();
        String sql = "select msg_content,msg_date,msg_type from Chat where friend_name=?";
        Cursor cursor = db.rawQuery(sql, new String[]{friendName});
        while (cursor.moveToNext()) {
            msg_content = cursor.getString(cursor.getColumnIndex("msg_content"));
            msg_date = cursor.getString(cursor.getColumnIndex("msg_date"));
            msg_type = cursor.getInt(cursor.getColumnIndex("msg_type"));
            StringBuilder sb = new StringBuilder();
            sb.append(msg_date).append("\n" + msg_content);
            Log.d("sql语句", sql);
            Log.d("获取本地数据库中“我”和" + friendName + "的聊天记录", msg_content + " " + msg_date + " " + msg_type);
            Msg msg = new Msg(sb.toString(), msg_type);
            messages.add(msg);
        }
        if(cursor != null) {
            cursor.close();
        }
//        db.close();
        return messages;
    }
}
