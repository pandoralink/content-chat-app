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
import com.example.newslist.News;
import com.example.newslist.NewsAdapter;
import com.example.newslist.R;
import com.example.newslist.data.BaseResponse;
import com.example.newslist.data.Constants;
import com.example.newslist.message.MsgContentActivity;
import com.example.newslist.news.NewsContentActivity;
import com.example.newslist.popup.OperationDialogFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FriendActivity extends AppCompatActivity {
    private static final String TAG = "PW";
    Boolean userRelate;
    int fanTotal;
    String userAccount;
    private RecyclerView rvNewsList;
    private NewsAdapter newsAdapter;
    private List<Articles> articlesData;
    private SwipeRefreshLayout swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        Intent intent = getIntent();
        ImageView iv_author_head = findViewById(R.id.iv_author_head);
        TextView tvAuthorName = findViewById(R.id.tv_title);
        Button btnFollow = findViewById(R.id.btn_follow_user);
        TextView tvAccount = findViewById(R.id.tv_account);
        TextView tvFanNum = findViewById(R.id.tv_fan_num);
        String authorName = intent.getStringExtra(Constants.AUTHOR_NAME_KEY);
        String authorHeadUrl = intent.getStringExtra(Constants.AUTHOR_HEAD_URL_KEY);

        userRelate = intent.getBooleanExtra(Constants.USER_RELATE_KEY, false);
        fanTotal = intent.getIntExtra(Constants.AUTHOR_FAN_TOTAL_KEY, 0);
        userAccount = intent.getStringExtra(Constants.AUTHOR_ACCOUNT_KEY);
        tvFanNum.setText(Integer.toString(fanTotal) + " 粉丝");
        tvAccount.setText(userAccount);
        tvAuthorName.setText(authorName);
        Glide.with(FriendActivity.this).load(authorHeadUrl).into(iv_author_head);
        if (userRelate) {
            btnFollow.setText("√已关注");
        } else {
            btnFollow.setText("+关注");
        }
        btnFollow.setOnClickListener(view -> {
            userRelate = !userRelate;
            if (userRelate) {
                btnFollow.setText("√已关注");
            } else {
                btnFollow.setText("+关注");
            }
        });
        // 私信按钮
        Button btnDm = findViewById(R.id.btn_dm);
        btnDm.setOnClickListener(view -> {
            Intent intent1 = new Intent(getApplicationContext(), MsgContentActivity.class);
            startActivity(intent1);
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
                        Type jsonType = new TypeToken<BaseResponse<List<Articles>>>() {
                        }.getType();
                        BaseResponse<List<Articles>> newsListResponse = gson.fromJson(body, jsonType);
                        for (Articles articles : newsListResponse.getData()) {
                            newsAdapter.add(articles);
                        }
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

    private void initData() {
        articlesData = new ArrayList<>();
        newsAdapter = new NewsAdapter(this, R.layout.list_item, articlesData);
        newsAdapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                OperationDialogFragment operationDialogFragment = new OperationDialogFragment();
                operationDialogFragment.articleUrl = articlesData.get(position).getArticle();
                operationDialogFragment.itemIndex = position;
                operationDialogFragment.setOnNotLikeClickListener(new OperationDialogFragment.OnNotLikeClickListener() {
                    @Override
                    public void onClick(int position) {
                        articlesData.remove(position);
                        newsAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(FriendActivity.this, NewsContentActivity.class);
                intent.putExtra(Constants.ARTICLE_URL_KEY, articlesData.get(position).getArticle());
                intent.putExtra(Constants.ARTICLE_AUTHOR_INFO_KEY, articlesData.get(position).getAuthorId());
                intent.putExtra(Constants.ARTICLE_NAME_KEY, articlesData.get(position).getTitle());
                intent.putExtra(Constants.AUTHOR_NAME_KEY, articlesData.get(position).getUser_name());
                intent.putExtra(Constants.AUTHOR_ACCOUNT_KEY, articlesData.get(position).getUser_account());
                intent.putExtra(Constants.AUTHOR_HEAD_URL_KEY, articlesData.get(position).getAuthorHeadUrl());
                intent.putExtra("testUserKey", 1005);
                startActivity(intent);
            }
        });
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvNewsList.setLayoutManager(llm);
        rvNewsList.setAdapter(newsAdapter);
        refreshData();
    }

    private void refreshData() {
        new Thread(() -> {
            Request request = new Request.Builder()
                    .url(Constants.USER_ARTICLE_URL + "?userAccount=" + userAccount)
                    .get().build();
            try {
                articlesData.clear();
                newsAdapter.notifyDataSetChanged();
                OkHttpClient client = new OkHttpClient();
                client.newCall(request).enqueue(callback);
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }
}