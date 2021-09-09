package com.example.newslist.news;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;

import com.example.newslist.News;
import com.example.newslist.R;
import com.example.newslist.data.BaseResponse;
import com.example.newslist.data.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class NewsContentActivity extends AppCompatActivity {

    private String TAG = "PW";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_content);

        ImageView ivNewContentBack = findViewById(R.id.iv_new_content_back);
        ivNewContentBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Intent intent = getIntent();
        String url = intent.getStringExtra(Constants.ARTICLE_URL_KEY);
        WebView webView = findViewById(R.id.wv_new);
        webView.setWebViewClient(new WebViewClient() {
            //设置在webView点击打开的新网页在当前界面显示,而不跳转到新的浏览器中
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String userName = "豪哥";
                String js = "window.localStorage.setItem('userName','" + userName + "');";
                String jsUrl = "javascript:(function({ " +
                        "var localStorage = window.localStorage; " +
                        "localStorage.setItem('userName', '" + userName + "') " +
                        "})()";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    view.evaluateJavascript(js, null);
                } else {
                    view.loadUrl(jsUrl);
                    view.reload();
                }
            }
        });
        webView.getSettings().

                setJavaScriptEnabled(true);
        webView.getSettings().

                setDomStorageEnabled(true);// 打开本地缓存提供JS调用,至关重要
        webView.getSettings().

                setAppCacheMaxSize(1024 * 1024 * 8);// 实现8倍缓存
        webView.getSettings().

                setAllowFileAccess(true);
        webView.getSettings().

                setAppCacheEnabled(true);

        String appCachePath = getApplication().getCacheDir().getAbsolutePath();
        webView.getSettings().

                setAppCachePath(appCachePath);
        webView.getSettings().

                setDatabaseEnabled(true);
        /* 设置WebView属性,运行执行js脚本 */
        webView.getSettings().

                setJavaScriptEnabled(true);
        /* //调用loadUrl方法为WebView加入链接 */
        webView.loadUrl(url);
    }

    private okhttp3.Callback callback = new okhttp3.Callback() {
        @Override
        public void onResponse(Call call, Response response)
                throws IOException {
            if (response.isSuccessful()) {
                final String body = response.body().string();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        Type jsonType = new TypeToken<BaseResponse<List<News>>>() {
                        }.getType();
                        BaseResponse<List<News>> newsListResponse = gson.fromJson(body, jsonType);
                        newsAdapter.notifyDataSetChanged();
                        swipe.setRefreshing(false);
                    }
                });
            } else {
            }
        }

        @Override
        public void onFailure(Call call, IOException e) {
            Log.e(TAG, "Failed to connect server!");
            e.printStackTrace();
        }
    };
}