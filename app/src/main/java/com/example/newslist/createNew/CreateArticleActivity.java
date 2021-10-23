package com.example.newslist.createNew;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.example.newslist.R;
import com.example.newslist.data.Constants;

/**
 * 更新用户文章
 * 缺点是无法在更新并返回上一级‘我的文章’页
 * 后更新‘我的文章’页中相应的文章项内容
 */
public class CreateArticleActivity extends AppCompatActivity {
    private int aid;
    private String articleName = "";
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_article);

        ImageView ivArticleCreateBack = findViewById(R.id.iv_article_create_back);
        articleName = getIntent().getStringExtra("articleName");
        title = getIntent().getStringExtra("title");
        aid = getIntent().getIntExtra("aid", 0);
        String url = Constants.SERVER_URL + "?articleName=" + articleName + "&aid=" + aid + "&title=" + title;

        ivArticleCreateBack.setOnClickListener(view -> finish());

        WebView webView = findViewById(R.id.wv_create_edit);
        webView.setWebViewClient(new WebViewClient() {
            //设置在webView点击打开的新网页在当前界面显示,而不跳转到新的浏览器中
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        /* 设置WebView属性,运行执行js脚本 */
        webView.getSettings().setJavaScriptEnabled(true);
        /* //调用loadUrl方法为WebView加入链接 */
        webView.loadUrl(url);
    }
}