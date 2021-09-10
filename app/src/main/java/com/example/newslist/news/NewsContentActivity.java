package com.example.newslist.news;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.newslist.user.FriendActivity;
import com.example.newslist.R;
import com.example.newslist.data.BaseResponse;
import com.example.newslist.data.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsContentActivity extends AppCompatActivity {

    private String TAG = "PW";
    private String articleUrl;
    private String authorName;
    private String authorHeadUrl;
    private Boolean userRelate;
    ImageView iv_author_head;

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
        initData();


    }

    private okhttp3.Callback callback = new okhttp3.Callback() {
        @Override
        public void onResponse(Call call, Response response)
                throws IOException {
            if (response.isSuccessful()) {
                final String body = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        Type jsonType = new TypeToken<BaseResponse<AuthorInfoRequest>>() {
                        }.getType();
                        BaseResponse<AuthorInfoRequest> AuthorInfoResponse = gson.fromJson(body, jsonType);
                        AuthorInfoRequest authorInfoRequest = (AuthorInfoRequest) AuthorInfoResponse.getData();
                        ImageView iv_author_head = findViewById(R.id.iv_author_head);
                        TextView tvAuthorName = findViewById(R.id.tv_author_name);
                        Button btnFollow = findViewById(R.id.btn_follow);
                        authorName = authorInfoRequest.getAuthorName();
                        authorHeadUrl = authorInfoRequest.getAuthorHeadUrl();
                        userRelate = authorInfoRequest.getAuthorRelate();
                        iv_author_head.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(), FriendActivity.class);
                                intent.putExtra(Constants.AUTHOR_NAME_KEY, authorName);
                                intent.putExtra(Constants.AUTHOR_HEAD_URL_KEY, authorHeadUrl);
                                intent.putExtra(Constants.USER_RELATE_KEY, userRelate);
                                startActivity(intent);
                            }
                        });
                        tvAuthorName.setText(authorName);
                        Glide.with(NewsContentActivity.this).load(authorHeadUrl).into(iv_author_head);
                        if (userRelate) {
                            btnFollow.setText("√已关注");
                        } else {
                            btnFollow.setText("+关注");
                        }
                        btnFollow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                userRelate = !userRelate;
                                if (userRelate) {
                                    btnFollow.setText("√已关注");
                                } else {
                                    btnFollow.setText("+关注");
                                }
                            }
                        });
                        initView();
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

    private void initData() {
        Intent intent = getIntent();
        int authorId = intent.getIntExtra(Constants.ARTICLE_AUTHOR_INFO_KEY, 0);
        articleUrl = intent.getStringExtra(Constants.ARTICLE_URL_KEY);
        int userId = intent.getIntExtra("testUserKey", 0);
        TextView tv_new_name = findViewById(R.id.tv_new_name);
        tv_new_name.setText(intent.getStringExtra(Constants.ARTICLE_NAME_KEY));
        if (authorId == 0 || userId == 0) {
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Request request = new Request.Builder()
                            .url(Constants.ARTICLE_AUTHOR_INFO_BASE_URL + "?blogger_id=" + authorId + "&fan_id=" + userId)
                            .get().build();
                    try {
                        OkHttpClient client = new OkHttpClient();
                        client.newCall(request).enqueue(callback);
                    } catch (NetworkOnMainThreadException ex) {
                        ex.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void initView() {
        WebView webView = findViewById(R.id.wv_new);
        webView.setWebViewClient(new WebViewClient() {
            //设置在webView点击打开的新网页在当前界面显示,而不跳转到新的浏览器中
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(articleUrl);
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
        webView.loadUrl(articleUrl);
    }
}