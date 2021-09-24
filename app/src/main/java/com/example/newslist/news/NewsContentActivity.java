package com.example.newslist.news;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
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
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsContentActivity extends AppCompatActivity {

    private final String TAG = "PW";
    private String articleUrl;
    private String authorName;
    private String authorAccount;
    private String authorHeadUrl;
    private Boolean userRelate = false;
    private int authorId;
    private int userId;
    private int articleID;
    ImageView ivAuthorHead;
    Button btnFollow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_content);

        ImageView ivNewContentBack = findViewById(R.id.iv_new_content_back);
        btnFollow = findViewById(R.id.btn_follow);
        ivNewContentBack.setOnClickListener(view -> finish());
        btnFollow.setOnClickListener(view -> {
            if (userRelate) {
                cancelFollow();
            } else {
                follow();
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
                        BaseResponse<AuthorInfoRequest> authorInfoResponse = gson.fromJson(body, jsonType);
                        AuthorInfoRequest authorInfoRequest = (AuthorInfoRequest) authorInfoResponse.getData();
                        userRelate = authorInfoRequest.getAuthorRelate();
                        int total = authorInfoRequest.getFanTotal();
                        ivAuthorHead.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(), FriendActivity.class);
                                intent.putExtra(Constants.AUTHOR_NAME_KEY, authorName);
                                intent.putExtra(Constants.AUTHOR_HEAD_URL_KEY, authorHeadUrl);
                                intent.putExtra(Constants.AUTHOR_ACCOUNT_KEY, authorAccount);
                                intent.putExtra(Constants.USER_RELATE_KEY, userRelate);
                                intent.putExtra(Constants.AUTHOR_FAN_TOTAL_KEY, total);
                                startActivity(intent);
                            }
                        });
                        if (userRelate) {
                            btnFollow.setText("√已关注");
                        } else {
                            btnFollow.setText("+关注");
                        }
                        initView();
                    }
                });
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
        articleID = intent.getIntExtra("NewIDKey", 0);
        Log.d(TAG, "文章ID：" + articleID);
        authorId = intent.getIntExtra(Constants.ARTICLE_AUTHOR_INFO_KEY, 0);
        userId = intent.getIntExtra("testUserKey", 0);
//        userName 和 userId 本来是全局，但由于登录系统还未完成暂时用此代替
        String userName = "庞老闆";
        String userHeadUrl = "http://116.63.152.202:5002/userHead/default_head.png";
        TextView tvNewName = findViewById(R.id.tv_new_name);
        TextView tvAuthorName = findViewById(R.id.tv_author_name);
        ivAuthorHead = findViewById(R.id.iv_author_head);

        articleUrl = intent.getStringExtra(Constants.ARTICLE_URL_KEY) + "?userId=" + userId +
                "&userName=" + userName + "&userHeadUrl=" + userHeadUrl + "&newId=" + articleID;
        Log.d(TAG, "initData: " + articleUrl);
        authorAccount = intent.getStringExtra(Constants.AUTHOR_ACCOUNT_KEY);
        authorName = intent.getStringExtra(Constants.AUTHOR_NAME_KEY);
        authorHeadUrl = intent.getStringExtra(Constants.AUTHOR_HEAD_URL_KEY);
        authorAccount = intent.getStringExtra(Constants.AUTHOR_ACCOUNT_KEY);
        tvNewName.setText(intent.getStringExtra(Constants.ARTICLE_NAME_KEY));
        tvAuthorName.setText(authorName);
        Glide.with(NewsContentActivity.this).load(authorHeadUrl).into(ivAuthorHead);

        if (authorId == 0 || userId == 0) {
        } else {
            new Thread(() -> {
                Request request = new Request.Builder()
                        .url(Constants.ARTICLE_AUTHOR_INFO_BASE_URL + "?blogger_id=" + authorId + "&fan_id=" + userId)
                        .get().build();
                try {
                    OkHttpClient client = new OkHttpClient();
                    client.newCall(request).enqueue(callback);
                } catch (NetworkOnMainThreadException ex) {
                    ex.printStackTrace();
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
            }
        });
        /* 设置WebView属性,运行执行js脚本 */
        webView.getSettings().setJavaScriptEnabled(true);
        String a = "3";
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.loadUrl(articleUrl);
    }

    private void follow() {
        new Thread(() -> {
            // 不想继续再 Constant 里面定义 Key
            String url = Constants.BACK_BASE_URL + "addFollow" + "?fan_id=" + userId + "&blogger_id=" + authorId;
            OkHttpClient okHttpClient = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(url)
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "onFailure: ");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        runOnUiThread(() -> {
                            userRelate = true;
                            btnFollow.setText("√已关注");
                        });
                    }
                }
            });
        }).start();
    }

    private void cancelFollow() {
        new Thread(() -> {
            String url = Constants.BACK_BASE_URL + "cancelFollow" + "?fan_id=" + userId + "&blogger_id=" + authorId;
            OkHttpClient okHttpClient = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(url)
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "onFailure: ");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        runOnUiThread(() -> {
                            userRelate = false;
                            btnFollow.setText("+关注");
                        });
                    }
                }
            });
        }).start();
    }
}