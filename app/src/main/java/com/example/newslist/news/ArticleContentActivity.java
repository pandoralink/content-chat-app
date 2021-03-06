package com.example.newslist.news;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.newslist.data.Article;
import com.example.newslist.user.FriendActivity;
import com.example.newslist.R;
import com.example.newslist.data.BaseResponse;
import com.example.newslist.data.Constants;
import com.example.newslist.user.User;
import com.example.newslist.utils.JsToUserPage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ArticleContentActivity extends AppCompatActivity {

    private final String TAG = "PW";
    private String articleUrl;
    private String authorName;
    private String authorAccount;
    private String authorHeadUrl;
    private Boolean userRelate = false;
    private int authorId;
    private int userId;
    private String userName;
    private String userHeadUrl;
    private int articleID;
    ImageView ivAuthorHead;
    Button btnFollow;
    OkHttpClient okHttpClient = new OkHttpClient();
    private Integer type = 1;
    TextView tvNewName;

    /**
     * type: 1 || 2
     * 1: 默认值，自带数据
     * 2: 需要通过 id 请求作者信息
     *
     * @param type
     */
    public ArticleContentActivity(Integer type) {
        this.type = type;
    }

    public ArticleContentActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_content);

        ImageView ivNewContentBack = findViewById(R.id.iv_new_content_back);
        btnFollow = findViewById(R.id.btn_follow);
        tvNewName = findViewById(R.id.tv_new_name);
        String spFileName = getResources().getString(R.string.shared_preferences_file_name);
        String userIdKey = getResources().getString(R.string.userId);
        String userNameKey = getResources().getString(R.string.userName);
        String userHeadKey = getResources().getString(R.string.userHead);

        userId = getIntegerValue(ArticleContentActivity.this, spFileName, userIdKey);
        userName = getStringValue(ArticleContentActivity.this, spFileName, userNameKey);
        userHeadUrl = getStringValue(ArticleContentActivity.this, spFileName, userHeadKey);

        ivNewContentBack.setOnClickListener(view -> finish());
        btnFollow.setOnClickListener(view -> {
            if (userRelate) {
                cancelFollow();
            } else {
                follow();
            }
        });

        initView();
        initDataJudge();
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
        webView.addJavascriptInterface(new JsToUserPage(this), "JsToUserPage");
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        Log.d(TAG, "文章加载链接" + articleUrl);
        webView.loadUrl(articleUrl);
    }

    private void follow() {
        String url = Constants.BACK_BASE_URL + "addFollow" + "?fan_id=" + userId + "&blogger_id=" + authorId;
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
    }

    private void cancelFollow() {
        String url = Constants.BACK_BASE_URL + "cancelFollow" + "?fan_id=" + userId + "&blogger_id=" + authorId;
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
    }

    private void initDataJudge() {
        Intent intent = getIntent();
        // 文章Url
        articleUrl = intent.getStringExtra(Constants.ARTICLE_URL_KEY);
        Integer type = intent.getIntExtra("type", 1);
        if (type == 1) {
            initData(intent);
        } else {
            getAuthorData(intent);
        }
    }

    private void initData(Intent intent) {
        articleID = intent.getIntExtra("NewIDKey", 0);
        authorId = intent.getIntExtra(Constants.ARTICLE_AUTHOR_INFO_KEY, 0);
        TextView tvNewName = findViewById(R.id.tv_new_name);
        TextView tvAuthorName = findViewById(R.id.tv_author_name);
        ivAuthorHead = findViewById(R.id.iv_author_head);
        initArticleUrl();

        initView();
        // 在等待请求完成的过程中用户点击作者头像无法进入用户主页，可以优化的一个点
        getRelate(authorId, userId);

        authorAccount = intent.getStringExtra(Constants.AUTHOR_ACCOUNT_KEY);
        authorName = intent.getStringExtra(Constants.AUTHOR_NAME_KEY);
        authorHeadUrl = intent.getStringExtra(Constants.AUTHOR_HEAD_URL_KEY);
        authorAccount = intent.getStringExtra(Constants.AUTHOR_ACCOUNT_KEY);
        tvNewName.setText(intent.getStringExtra(Constants.ARTICLE_NAME_KEY));
        tvAuthorName.setText(authorName);
        Glide.with(ArticleContentActivity.this).load(authorHeadUrl).into(ivAuthorHead);
    }

    private void getAuthorData(Intent intent) {
        articleID = intent.getIntExtra("articleId", 0);
        initArticleUrl();
        initView();
        getAuthorIdByNewId(articleID);
    }

    private void getAuthorIdByNewId(Integer newId) {
        Request request = new Request.Builder()
                .url(Constants.BACK_BASE_URL + "getAuthorId?newId=" + newId)
                .get().build();
        try {
            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    final String body = response.body().string();
                    if (response.isSuccessful()) {
                        Log.d(TAG, "onResponse: " + body);
                        Gson gson = new Gson();
                        Article article = gson.fromJson(body, Article.class);
                        authorId = article.getNewOwnerId();
                        runOnUiThread(() -> {
                            tvNewName.setText(article.getNewName());
                        });
                        getRelate(authorId, userId);
                        getAuthorDataByAuthorId(authorId);
                    }
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }
            });
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }
    }

    private void getAuthorDataByAuthorId(Integer authorId) {
        TextView tvAuthorName = findViewById(R.id.tv_author_name);
        ivAuthorHead = findViewById(R.id.iv_author_head);
        Request request = new Request.Builder()
                .url(Constants.BACK_BASE_URL + "getAuthorInfo?authorId=" + authorId)
                .get().build();
        try {
            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String body = response.body().string();
                        runOnUiThread(() -> {
                            Gson gson = new Gson();
                            User author = gson.fromJson(body, User.class);
                            authorAccount = author.getUser_account();
                            authorName = author.getUser_name();
                            authorHeadUrl = author.getUser_head();
                            tvAuthorName.setText(authorName);
                            Glide.with(ArticleContentActivity.this).load(authorHeadUrl).into(ivAuthorHead);
                        });
                    }
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }
            });
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 填充 article 所需要的参数
     */
    private void initArticleUrl() {
        articleUrl = articleUrl + "?userId=" + userId +
                "&userName=" + userName + "&userHeadUrl=" + userHeadUrl + "&newId=" + articleID;
    }

    /**
     * 获取作者和当前用户的关系
     * 关注 || 不关注
     *
     * @param authorId
     * @param userId
     */
    private void getRelate(Integer authorId, Integer userId) {
        if (authorId == 0 || userId == 0) {
        } else {
            Request request = new Request.Builder()
                    .url(Constants.ARTICLE_AUTHOR_INFO_BASE_URL + "?blogger_id=" + authorId + "&fan_id=" + userId)
                    .get().build();
            try {
                OkHttpClient client = okHttpClient;
                client.newCall(request).enqueue(callback);
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }
    }

    private okhttp3.Callback callback = new okhttp3.Callback() {
        @Override
        public void onResponse(Call call, Response response)
                throws IOException {
            if (response.isSuccessful()) {
                final String body = response.body().string();
                runOnUiThread(() -> {
                    Gson gson = new Gson();
                    Type jsonType = new TypeToken<BaseResponse<AuthorInfoRequest>>() {
                    }.getType();
                    BaseResponse<AuthorInfoRequest> authorInfoResponse = gson.fromJson(body, jsonType);
                    AuthorInfoRequest authorInfoRequest = (AuthorInfoRequest) authorInfoResponse.getData();
                    userRelate = (Boolean) authorInfoRequest.getAuthorRelate();
                    int total = authorInfoRequest.getFanTotal();
                    ivAuthorHead.setOnClickListener(view -> {
                        Intent intent = new Intent(getApplicationContext(), FriendActivity.class);
                        intent.putExtra(Constants.AUTHOR_NAME_KEY, authorName);
                        intent.putExtra(Constants.AUTHOR_HEAD_URL_KEY, authorHeadUrl);
                        intent.putExtra(Constants.AUTHOR_ACCOUNT_KEY, authorAccount);
                        intent.putExtra(Constants.USER_RELATE_KEY, userRelate);
                        intent.putExtra(Constants.AUTHOR_FAN_TOTAL_KEY, total);
                        intent.putExtra("authorId", authorId);
                        startActivity(intent);
                    });
                    if (userRelate) {
                        btnFollow.setText("√已关注");
                    } else {
                        btnFollow.setText("+关注");
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

    private Integer getIntegerValue(Context context, String fileName, String key) {
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE).getInt(key, 0);
    }

    private String getStringValue(Context context, String fileName, String key) {
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE).getString(key, null);
    }
}