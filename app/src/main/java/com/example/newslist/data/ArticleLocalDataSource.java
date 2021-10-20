package com.example.newslist.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.newslist.Articles;

import java.util.ArrayList;
import java.util.List;

public class ArticleLocalDataSource {
    private MyDbOpenHelper myDbOpenHelper;

    public ArticleLocalDataSource(@NonNull Context context) {
        myDbOpenHelper = new MyDbOpenHelper(context);
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
        SQLiteDatabase db = myDbOpenHelper.getWritableDatabase();

        if (!isArticlesExist(article.getaId())) {
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
}
