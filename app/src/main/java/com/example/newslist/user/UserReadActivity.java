package com.example.newslist.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.newslist.Articles;
import com.example.newslist.NewsAdapter;
import com.example.newslist.R;
import com.example.newslist.data.ArticleLocalDataSource;
import com.example.newslist.data.Constants;
import com.example.newslist.news.ArticleContentActivity;
import com.example.newslist.popup.DeleteMsgDialogFragment;
import com.example.newslist.popup.MyDialogFragment;
import com.example.newslist.popup.OperationDialogFragment;
import com.example.newslist.utils.UserInfoManager;

import java.util.ArrayList;
import java.util.List;

public class UserReadActivity extends AppCompatActivity {
    private RecyclerView rvNewsList;
    private SwipeRefreshLayout swipe;
    private NewsAdapter newsAdapter;
    private List<Articles> articlesData;
    int currentUserId;
    ArticleLocalDataSource mLocalDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_read);

        rvNewsList = findViewById(R.id.lv_article_list);
        ImageView ivUserReadOut = findViewById(R.id.iv_user_read_out);
        UserInfoManager userInfoManager = new UserInfoManager(this);
        currentUserId = userInfoManager.getUserId();
        mLocalDataSource = new ArticleLocalDataSource(this);

        ivUserReadOut.setOnClickListener(view -> {
            finish();
        });

        initData();
    }

    private void initData() {
        articlesData = new ArrayList<>();
        newsAdapter = new NewsAdapter(this, R.layout.list_item, articlesData);
        getData();

        newsAdapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                confirmDeleteMsg(position);
            }

            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(UserReadActivity.this, ArticleContentActivity.class);
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
    }

    private void getData() {
        List<Articles> articles = mLocalDataSource.getArticles();
        for (Articles article : articles) {
            newsAdapter.add(article);
        }
        newsAdapter.notifyDataSetChanged();
    }

    public void confirmDeleteMsg(int index) {
        MyDialogFragment myDialogFragment = new MyDialogFragment(index, "是否删除该浏览记录");
        myDialogFragment.setOnNoticeDialogListener(new MyDialogFragment.NoticeDialogListener() {
            @Override
            public void onDialogPositiveClick(DialogFragment dialog, int index) {
                mLocalDataSource.deleteArticle(articlesData.get(index).getaId());
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