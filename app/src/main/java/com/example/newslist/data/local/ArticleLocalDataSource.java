package com.example.newslist.data.local;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.newslist.Articles;
import com.example.newslist.message.Messages;
import com.example.newslist.message.core.Msg;

import java.util.ArrayList;
import java.util.List;

public class ArticleLocalDataSource {
    private MyDbOpenHelper myDbOpenHelper;
    private static ArticleLocalDataSource INSTANCE;

    public ArticleLocalDataSource(@NonNull Context context) {
        myDbOpenHelper = new MyDbOpenHelper(context);
    }

    public static ArticleLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ArticleLocalDataSource(context);
        }
        return INSTANCE;
    }

    public List<Articles> getArticles() {
        List<Articles> articles = new ArrayList<Articles>();
        SQLiteDatabase db = myDbOpenHelper.getReadableDatabase();

        String[] projection = {
                ArticleContract.ArticleEntry.COLUMN_NAME_AID,
                ArticleContract.ArticleEntry.COLUMN_NAME_URL,
                ArticleContract.ArticleEntry.COLUMN_NAME_TITLE,
                ArticleContract.ArticleEntry.COLUMN_NAME_AUTHORID,
                ArticleContract.ArticleEntry.COLUMN_NAME_ARTICLECOVERURL,
                ArticleContract.ArticleEntry.COLUMN_NAME_AUTHORACCOUNT,
                ArticleContract.ArticleEntry.COLUMN_NAME_AUTHORNAME,
                ArticleContract.ArticleEntry.COLUMN_NAME_AUTHORHEADURL
        };

        Cursor c = db.query(
                ArticleContract.ArticleEntry.TABLE_NAME, projection, null, null, null, null, null);

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                int aid = c.getInt(c.getColumnIndexOrThrow(ArticleContract.ArticleEntry.COLUMN_NAME_AID));
                String aurl = c.getString(c.getColumnIndexOrThrow(ArticleContract.ArticleEntry.COLUMN_NAME_URL));
                String title = c.getString(c.getColumnIndexOrThrow(ArticleContract.ArticleEntry.COLUMN_NAME_TITLE));
                int authorId = c.getInt(c.getColumnIndexOrThrow(ArticleContract.ArticleEntry.COLUMN_NAME_AUTHORID));
                String articleCoverUrl = c.getString(c.getColumnIndexOrThrow(ArticleContract.ArticleEntry.COLUMN_NAME_ARTICLECOVERURL));
                String authorAccount = c.getString(c.getColumnIndexOrThrow(ArticleContract.ArticleEntry.COLUMN_NAME_AUTHORACCOUNT));
                String authorName = c.getString(c.getColumnIndexOrThrow(ArticleContract.ArticleEntry.COLUMN_NAME_AUTHORNAME));
                String authorHeadUrl = c.getString(c.getColumnIndexOrThrow(ArticleContract.ArticleEntry.COLUMN_NAME_AUTHORHEADURL));

                Articles article = new Articles(aid, aurl, title, authorId, articleCoverUrl, authorAccount, authorName, authorHeadUrl);
                articles.add(article);
            }
        }
        if (c != null) {
            c.close();
        }
        db.close();
        return articles;
    }

    public void deleteArticle(int aid) {
        SQLiteDatabase db = myDbOpenHelper.getWritableDatabase();

        String selection = ArticleContract.ArticleEntry.COLUMN_NAME_AID + "=?";
        String[] selectionArgs = {Integer.toString(aid)};

        db.delete(ArticleContract.ArticleEntry.TABLE_NAME, selection, selectionArgs);

        db.close();
    }

    public void insertArticle(Articles article) {

        if (!isArticlesExist(article.getaId())) {
            /**
             * 这里面有一个致命的错误，当我们调用 isArticlesExist() 时
             * 如果在里面关闭了 db，那么则有可能关闭 insertArticle
             * 想要操作的 db，这样会导致无法对 db 进行操作
             */
            SQLiteDatabase db = myDbOpenHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(ArticleContract.ArticleEntry.COLUMN_NAME_AID, article.getaId());
            values.put(ArticleContract.ArticleEntry.COLUMN_NAME_URL, article.getArticle());
            values.put(ArticleContract.ArticleEntry.COLUMN_NAME_AUTHORID, article.getAuthorId());
            values.put(ArticleContract.ArticleEntry.COLUMN_NAME_TITLE, article.getTitle());
            values.put(ArticleContract.ArticleEntry.COLUMN_NAME_ARTICLECOVERURL, article.getArticle_cover_url());
            values.put(ArticleContract.ArticleEntry.COLUMN_NAME_AUTHORACCOUNT, article.getUser_account());
            values.put(ArticleContract.ArticleEntry.COLUMN_NAME_AUTHORNAME, article.getUser_name());
            values.put(ArticleContract.ArticleEntry.COLUMN_NAME_AUTHORHEADURL, article.getAuthorHeadUrl());

            db.insert(ArticleContract.ArticleEntry.TABLE_NAME, null, values);
            db.close();
        }
    }

    private Boolean isArticlesExist(int aid) {
        SQLiteDatabase db = myDbOpenHelper.getReadableDatabase();

        String[] projection = {
                ArticleContract.ArticleEntry.COLUMN_NAME_AID,
        };
        @SuppressLint("Recycle") Cursor c = db.query(
                ArticleContract.ArticleEntry.TABLE_NAME, projection,
                "aid=?", new String[]{Integer.toString(aid)}, null, null, null);
        Boolean result = c != null && c.getCount() > 0;

        if (c != null) {
            c.close();
        }
        db.close();

        return result;
    }

    public List<Messages> getMsgTip() {
        List<Messages> messages = new ArrayList<Messages>();
        SQLiteDatabase db = myDbOpenHelper.getReadableDatabase();

        String[] projection = {
                MsgTipContract.MsgTipEntry.COLUMN_NAME_FRIEND_NAME,
                MsgTipContract.MsgTipEntry.COLUMN_NAME_FIRST_MSG,
                MsgTipContract.MsgTipEntry.COLUMN_NAME_TYPE,
                MsgTipContract.MsgTipEntry.COLUMN_NAME_HEAD_URL,
                MsgTipContract.MsgTipEntry.COLUMN_NAME_CONTENT_URL,
                MsgTipContract.MsgTipEntry.COLUMN_NAME_AID,
        };

        Cursor c = db.query(
                MsgTipContract.MsgTipEntry.TABLE_NAME, projection, null, null, null, null, null);

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {

                String friendName = c.getString(c.getColumnIndexOrThrow(MsgTipContract.MsgTipEntry.COLUMN_NAME_FRIEND_NAME));
                String firstMsg = c.getString(c.getColumnIndexOrThrow(MsgTipContract.MsgTipEntry.COLUMN_NAME_FIRST_MSG));
                int type = c.getInt(c.getColumnIndexOrThrow(MsgTipContract.MsgTipEntry.COLUMN_NAME_TYPE));
                String headUrl = c.getString(c.getColumnIndexOrThrow(MsgTipContract.MsgTipEntry.COLUMN_NAME_HEAD_URL));
                String contentUrl = c.getString(c.getColumnIndexOrThrow(MsgTipContract.MsgTipEntry.COLUMN_NAME_CONTENT_URL));
                int aid = c.getInt(c.getColumnIndexOrThrow(MsgTipContract.MsgTipEntry.COLUMN_NAME_AID));

                Messages message = new Messages(friendName, firstMsg, type, headUrl, contentUrl, aid);
                messages.add(message);
            }
        }
        if (c != null) {
            c.close();
        }
        db.close();

        return messages;
    }

    public void deleteMsgTip(String firstMsg) {
        SQLiteDatabase db = myDbOpenHelper.getWritableDatabase();

        // 可以考虑用 id 进行删除
        String selection = MsgTipContract.MsgTipEntry.COLUMN_NAME_FIRST_MSG + "=?";
        String[] selectionArgs = {firstMsg};

        db.delete(MsgTipContract.MsgTipEntry.TABLE_NAME, selection, selectionArgs);

        db.close();
    }

    public void insertMsgTip(Messages message) {
        SQLiteDatabase db = myDbOpenHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MsgTipContract.MsgTipEntry.COLUMN_NAME_FRIEND_NAME, message.getFriendName());
        values.put(MsgTipContract.MsgTipEntry.COLUMN_NAME_FIRST_MSG, message.getFirstMsg());
        values.put(MsgTipContract.MsgTipEntry.COLUMN_NAME_TYPE, message.getType());
        values.put(MsgTipContract.MsgTipEntry.COLUMN_NAME_HEAD_URL, message.getHeadUrl());
        values.put(MsgTipContract.MsgTipEntry.COLUMN_NAME_CONTENT_URL, message.getContentUrl());
        values.put(MsgTipContract.MsgTipEntry.COLUMN_NAME_AID, message.getAid());

        db.insert(MsgTipContract.MsgTipEntry.TABLE_NAME, null, values);
        db.close();
    }

    public List<Msg> getMsgList(String userName) {
        List<Msg> msgs = new ArrayList<Msg>();
        SQLiteDatabase db = myDbOpenHelper.getReadableDatabase();
        String sql = "select msg_content,msg_date,msg_type from Chat where friend_name=?";

        Cursor cursor = db.rawQuery(sql, new String[]{userName});
        while (cursor.moveToNext()) {
            String msg_content = cursor.getString(cursor.getColumnIndex("msg_content"));
            String msg_date = cursor.getString(cursor.getColumnIndex("msg_date"));
            int msg_type = cursor.getInt(cursor.getColumnIndex("msg_type"));
            StringBuilder sb = new StringBuilder();
            sb.append(msg_date).append("\n" + msg_content);
            Log.d("sql语句", sql);
            Log.d("获取本地数据库中“我”和" + userName + "的聊天记录", msg_content + " " + msg_date + " " + msg_type);
            Msg msg = new Msg(sb.toString(), msg_type);
            msgs.add(msg);
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return msgs;
    }

    /**
     * 删库跑路
     */
    public void clearAll() {
        SQLiteDatabase db = myDbOpenHelper.getWritableDatabase();

        db.execSQL("delete from " + MsgTipContract.MsgTipEntry.TABLE_NAME);
        db.execSQL("delete from " + ArticleContract.ArticleEntry.TABLE_NAME);

        db.close();
    }
}
