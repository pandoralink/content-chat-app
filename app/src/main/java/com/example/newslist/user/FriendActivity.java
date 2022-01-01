package com.example.newslist.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.newslist.Articles;
import com.example.newslist.MainActivity;
import com.example.newslist.NewsAdapter;
import com.example.newslist.R;
import com.example.newslist.data.Article;
import com.example.newslist.data.BaseResponse;
import com.example.newslist.data.Constants;
import com.example.newslist.message.MsgContentActivity;
import com.example.newslist.message.core.ListSQLiteHelper;
import com.example.newslist.message.core.MySQLiteHelper;
import com.example.newslist.news.ArticleContentActivity;
import com.example.newslist.news.AuthorInfoRequest;
import com.example.newslist.popup.OperationDialogFragment;
import com.example.newslist.utils.Author;
import com.example.newslist.utils.UserInfoManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FriendActivity extends AppCompatActivity {
    private static final String TAG = "PW";
    Boolean userRelate;
    int fanTotal;
    String userAccount;
    int authorId;
    /**
     * currentUserId 是 APP
     * 当前用户 id
     */
    int currentUserId;
    TextView tvAccount;
    Button btnFollow;
    TextView tvFanNum;
    TextView tvAuthorName;
    ImageView iv_author_head;
    private RecyclerView rvNewsList;
    private NewsAdapter newsAdapter;
    private List<Articles> articlesData;
    private SwipeRefreshLayout swipe;
    OkHttpClient okHttpClient;
    private Integer userId;
    MySQLiteHelper openHelper;
    ListSQLiteHelper listSQLiteHelper;

    private void initDB() {
        openHelper = new MySQLiteHelper(this, "chat.db", null, 1);
        listSQLiteHelper = new ListSQLiteHelper(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        okHttpClient = new OkHttpClient();
        Intent intent = getIntent();
        btnFollow = findViewById(R.id.btn_follow_user);
        tvAccount = findViewById(R.id.tv_account);
        iv_author_head = findViewById(R.id.iv_author_head);
        tvAuthorName = findViewById(R.id.tv_user_name);
        tvFanNum = findViewById(R.id.tv_fan_num);
        String authorName = intent.getStringExtra(Constants.AUTHOR_NAME_KEY);
        String authorHeadUrl = intent.getStringExtra(Constants.AUTHOR_HEAD_URL_KEY);
        UserInfoManager userInfoManager = new UserInfoManager(this);
        userId = userInfoManager.getUserId();

        userRelate = intent.getBooleanExtra(Constants.USER_RELATE_KEY, false);
        fanTotal = intent.getIntExtra(Constants.AUTHOR_FAN_TOTAL_KEY, 0);
        userAccount = intent.getStringExtra(Constants.AUTHOR_ACCOUNT_KEY);
        authorId = intent.getIntExtra("authorId", 0);
        /**
         * 若没有初始化当前 APP 用户信息
         * UserInfoManager.getUserId()
         * 将不起作用
         */
        currentUserId = userInfoManager.getUserId();

        if (intent.getStringExtra("authorJSON") == null) {
            tvAccount.setText(userAccount);
            if (userRelate) {
                btnFollow.setText("√已关注");
            } else {
                btnFollow.setText("+关注");
            }
            tvAuthorName.setText(authorName);
            Glide.with(FriendActivity.this).load(authorHeadUrl).into(iv_author_head);
            tvFanNum.setText(Integer.toString(fanTotal) + " 粉丝");
        } else {
            Gson gson = new Gson();
            // 用到 author 的地方其实是普通用户，后期可能改名
            Author author = gson.fromJson(intent.getStringExtra("authorJSON"), Author.class);
            authorId = author.getUid();
            tvAuthorName.setText(author.getUname());
            Glide.with(FriendActivity.this).load(author.getUheadUrl()).into(iv_author_head);
            getUserAccountByUID(author.getUid());
            getRelate(author.getUid(), currentUserId);
        }

        btnFollow.setOnClickListener(view -> {
            userRelate = !userRelate;
            if (userRelate) {
                cancelFollow();
            } else {
                follow();
            }
        });
        // 私信按钮
        Button btnDm = findViewById(R.id.btn_dm);
        btnDm.setOnClickListener(view -> {
            Intent intentToMsgContent = new Intent(getApplicationContext(), MsgContentActivity.class);
            intentToMsgContent.putExtra("friendName", authorName);
            intentToMsgContent.putExtra("authorId", authorId);
            intentToMsgContent.putExtra("authorHeadUrl", authorHeadUrl);
            startActivity(intentToMsgContent);

            MainActivity.setPosition(1);
        });
        ImageView ivFriendOut = findViewById(R.id.iv_friend_out);
        ivFriendOut.setOnClickListener(view -> finish());

        // 与 NewsFragment 同逻辑部分
        rvNewsList = findViewById(R.id.lv_article_list);

        initData();
        swipe = findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        initDB();
    }

    private okhttp3.Callback callback = new okhttp3.Callback() {
        @Override
        public void onResponse(Call call, Response response)
                throws IOException {
            if (response.isSuccessful()) {
                final String body = response.body().string();
                runOnUiThread(() -> {
                    Gson gson = new Gson();
                    Type jsonType = new TypeToken<BaseResponse<List<Articles>>>() {
                    }.getType();
                    BaseResponse<List<Articles>> newsListResponse = gson.fromJson(body, jsonType);
                    for (Articles articles : newsListResponse.getData()) {
                        newsAdapter.add(articles);
                    }
                    newsAdapter.notifyDataSetChanged();
                    swipe.setRefreshing(false);
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
        articlesData = new ArrayList<>();
        newsAdapter = new NewsAdapter(this, R.layout.list_item, articlesData);
        newsAdapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
            }

            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(FriendActivity.this, ArticleContentActivity.class);
                intent.putExtra(Constants.ARTICLE_URL_KEY, articlesData.get(position).getArticle());
                intent.putExtra(Constants.ARTICLE_AUTHOR_INFO_KEY, articlesData.get(position).getAuthorId());
                intent.putExtra(Constants.ARTICLE_NAME_KEY, articlesData.get(position).getTitle());
                intent.putExtra(Constants.AUTHOR_NAME_KEY, articlesData.get(position).getUser_name());
                intent.putExtra(Constants.AUTHOR_ACCOUNT_KEY, articlesData.get(position).getUser_account());
                intent.putExtra(Constants.AUTHOR_HEAD_URL_KEY, articlesData.get(position).getAuthorHeadUrl());
                intent.putExtra("testUserKey", currentUserId);
                startActivity(intent);
            }
        });
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvNewsList.setLayoutManager(llm);
        rvNewsList.setAdapter(newsAdapter);
        refreshData();
    }

    private void refreshData() {
        Request request = new Request.Builder()
                .url(Constants.USER_ARTICLE_URL + "?userAccount=" + userAccount)
                .get().build();
        try {
            runOnUiThread(() -> {
                articlesData.clear();
                newsAdapter.notifyDataSetChanged();
            });
            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(callback);
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }
    }

    private void getUserAccountByUID(int uid) {
        Request request = new Request.Builder()
                .url(Constants.BACK_BASE_URL + "getUserAccount?uid=" + uid)
                .get().build();
        try {
            OkHttpClient client = okHttpClient;
            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    final String body = response.body().string();
                    if (response.isSuccessful()) {
                        Gson gson = new Gson();
                        userAccount = body;
                        refreshData();
                        runOnUiThread(() -> {
                            tvAccount.setText(body);
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
                client.newCall(request).enqueue(new okhttp3.Callback() {
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            final String body = response.body().string();
                            runOnUiThread(() -> {
                                Gson gson = new Gson();
                                Type jsonType = new TypeToken<BaseResponse<AuthorInfoRequest>>() {
                                }.getType();
                                BaseResponse<AuthorInfoRequest> authorInfoResponse = gson.fromJson(body, jsonType);
                                AuthorInfoRequest authorInfoRequest = (AuthorInfoRequest) authorInfoResponse.getData();
                                userRelate = authorInfoRequest.getAuthorRelate();
                                int total = authorInfoRequest.getFanTotal();
                                if (userRelate) {
                                    btnFollow.setText("√已关注");
                                } else {
                                    btnFollow.setText("+关注");
                                }
                                tvFanNum.setText(Integer.toString(fanTotal) + " 粉丝");
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }
                });
            } catch (NetworkOnMainThreadException ex) {
                Log.e(TAG, "Failed to connect server!");
                ex.printStackTrace();
            }
        }
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
}