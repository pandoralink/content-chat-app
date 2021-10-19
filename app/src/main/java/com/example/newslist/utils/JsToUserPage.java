package com.example.newslist.utils;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.example.newslist.user.FriendActivity;
import com.google.gson.Gson;

public class JsToUserPage {
    private Context context;

    public JsToUserPage(Context context) {
        this.context = context;
    }

    public JsToUserPage() {

    }

    @JavascriptInterface
    public void toPage(String data) {
        Gson gson = new Gson();
        Author author = gson.fromJson(data, Author.class);
        Intent intent = new Intent(context, FriendActivity.class);
        intent.putExtra("authorJSON", gson.toJson(author));
        context.startActivity(intent);
    }
}
