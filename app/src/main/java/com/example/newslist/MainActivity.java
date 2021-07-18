package com.example.newslist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.res.TypedArray;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    //    titles、authors 是 2.4.3.4 模板
    private String[] titles = null;
    private String[] authors = null;
    private static final String NEWS_TITLE = "news_title";
    private static final String NEWS_AUTHOR = "news_author";
    private List<Map<String, String>> dataList = new ArrayList<>();

    // 最终版静态变量如下
    public static final String NEWS_ID = "news_id";
    private List<News> newsList = new ArrayList<>();

    private NewsAdapter newsAdapter = null;
    private RecyclerView recyclerView;

    private SwipeRefreshLayout swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.lv_news_list);
        initData();

        newsAdapter = new NewsAdapter(MainActivity.this, R.layout.list_item, newsList);

        LinearLayoutManager llm = new LinearLayoutManager(this);
//        横向布局，如需铺满全屏需要在activity_main中设置
//        LinearLayoutManager llmH = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
//      表格形式显⽰列表(2列)
//        GridLayoutManager Glm = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(newsAdapter);

        swipe = findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
    }

    private void initData() {
        int length;
        titles = getResources().getStringArray(R.array.titles);
        authors = getResources().getStringArray(R.array.authors);

        TypedArray images = getResources().obtainTypedArray(R.array.images);

        if (titles.length > authors.length) {
            length = authors.length;
        } else {
            length = titles.length;
        }

        for (int i = 0; i < length; i++) {
            News news = new News();
            news.setTitle(titles[i]);
            news.setAuthor(authors[i]);
            news.setImageId(images.getResourceId(i, 0));

            newsList.add(news);
        }
    }

    private void refreshData() {
        Random random = new Random();
        int index = random.nextInt(19);

        News news = new News();

        TypedArray images = getResources().obtainTypedArray(R.array.images);
        news.setTitle(titles[index]);
        news.setAuthor(authors[index]);
//        news.setContent(contents[index]);
        news.setImageId(images.getResourceId(index,0));

        newsAdapter.add(news);
        newsAdapter.notifyDataSetChanged();
        swipe.setRefreshing(false);
    }
}