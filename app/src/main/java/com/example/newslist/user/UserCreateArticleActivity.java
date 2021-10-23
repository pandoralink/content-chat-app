package com.example.newslist.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.view.View;
import android.widget.ImageView;

import com.example.newslist.Articles;
import com.example.newslist.NewsAdapter;
import com.example.newslist.R;
import com.example.newslist.createNew.CreateArticleActivity;
import com.example.newslist.data.Constants;
import com.example.newslist.popup.MyDialogFragment;
import com.example.newslist.utils.UserInfoManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserCreateArticleActivity extends AppCompatActivity {
    private RecyclerView rvNewsList;
    private SwipeRefreshLayout swipe;
    private NewsAdapter newsAdapter;
    private List<Articles> articlesData;
    int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_create_article);

        rvNewsList = findViewById(R.id.lv_create_article_list);
        ImageView ivUserReadOut = findViewById(R.id.iv_user_create_out);
        swipe = findViewById(R.id.sw_user_create_article);
        UserInfoManager userInfoManager = new UserInfoManager(this);
        currentUserId = userInfoManager.getUserId();

        swipe.setOnRefreshListener(() -> refreshData());
        ivUserReadOut.setOnClickListener(view -> {
            finish();
        });

        initData();
    }

    private void initData() {
        articlesData = new ArrayList<>();
        newsAdapter = new NewsAdapter(this, R.layout.list_item, articlesData);

        newsAdapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                confirmDeleteMsg(position);
            }

            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(UserCreateArticleActivity.this, CreateArticleActivity.class);
                intent.putExtra("articleName", articlesData.get(position).getArticle());
                intent.putExtra("aid", articlesData.get(position).getaId());
                intent.putExtra("title", articlesData.get(position).getTitle());
                startActivity(intent);
            }
        });
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvNewsList.setLayoutManager(llm);
        rvNewsList.setAdapter(newsAdapter);

        refreshData();
    }

    private void refreshData() {
        swipe.setRefreshing(true);
        articlesData.clear();
        getData();
    }

    private void getData() {
        Request request = new Request.Builder()
                .url(Constants.BACK_BASE_URL + "getUserArticles" + "?userId=" + currentUserId)
                .get().build();
        try {
            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    runOnUiThread(() -> swipe.setRefreshing(false));
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String body = response.body().string();
                        Gson gson = new Gson();
                        runOnUiThread(() -> {
                            List<Articles> articleList = gson.fromJson(body, new TypeToken<List<Articles>>() {
                            }.getType());
                            for (Articles article : articleList) {
                                newsAdapter.add(article);
                            }
                            newsAdapter.notifyDataSetChanged();
                        });

                    }
                    runOnUiThread(() -> swipe.setRefreshing(false));
                }
            });
        } catch (NetworkOnMainThreadException ex) {
            runOnUiThread(() -> swipe.setRefreshing(false));
            ex.printStackTrace();
        }
    }

    private void deleteUserCreateArticle(int aid) {
        Request request = new Request.Builder()
                .url(Constants.BACK_BASE_URL + "deleteUserArticle" + "?userId=" + currentUserId + "&aid=" + aid)
                .get().build();
        try {
            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                }
            });
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }
    }

    public void confirmDeleteMsg(int index) {
        MyDialogFragment myDialogFragment = new MyDialogFragment(index, "是否删除该文章");
        myDialogFragment.setOnNoticeDialogListener(new MyDialogFragment.NoticeDialogListener() {
            @Override
            public void onDialogPositiveClick(DialogFragment dialog, int index) {
                deleteUserCreateArticle(articlesData.get(index).getaId());
                articlesData.remove(index);
                newsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {

            }
        });
        myDialogFragment.show(getSupportFragmentManager(), "deleteMsg");
    }

}