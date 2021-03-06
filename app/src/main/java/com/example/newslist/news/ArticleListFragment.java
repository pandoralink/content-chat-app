package com.example.newslist.news;

import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.newslist.Articles;
import com.example.newslist.NewsAdapter;
import com.example.newslist.R;
import com.example.newslist.data.local.ArticleLocalDataSource;
import com.example.newslist.data.BaseResponse;
import com.example.newslist.data.Constants;
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


/**
 * @author 庞旺
 */
public class ArticleListFragment extends Fragment {

    private static final String TAG = "PW";
    View rootView;
    private RecyclerView rvNewsList;
    private NewsAdapter newsAdapter;
    private List<Articles> articlesData;
    private SwipeRefreshLayout swipe;
    private String CURRENT_URL;
    private int offset = 0;
    /**
     * type: 关注 or 推荐
     * 关注: 1 推荐: 0
     * 默认为 0
     */
    private Integer type = 0;

    public ArticleListFragment() {
        CURRENT_URL = Constants.ARTICLE_URL;
    }

    public ArticleListFragment(String currentUrl, Integer type) {
        CURRENT_URL = currentUrl;
        this.type = type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.article_content, container, false);
        }

        rvNewsList = rootView.findViewById(R.id.lv_article_list);

        swipe = rootView.findViewById(R.id.swipe);
        swipe.setOnRefreshListener(() -> refreshData());

        initData();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (rootView != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }

    private void initData() {
        articlesData = new ArrayList<>();
        newsAdapter = new NewsAdapter(getContext(), R.layout.list_item, articlesData);
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
                operationDialogFragment.show(getActivity().getSupportFragmentManager(), "OperationDialogFragment");
            }

            @Override
            public void onItemClick(View view, int position) {
                insertLocalCache(articlesData.get(position));
                Intent intent = new Intent(getActivity(), ArticleContentActivity.class);
                intent.putExtra(Constants.ARTICLE_URL_KEY, articlesData.get(position).getArticle());
                intent.putExtra("NewIDKey", articlesData.get(position).getaId());
                intent.putExtra(Constants.ARTICLE_AUTHOR_INFO_KEY, articlesData.get(position).getAuthorId());
                intent.putExtra(Constants.ARTICLE_NAME_KEY, articlesData.get(position).getTitle());
                intent.putExtra(Constants.AUTHOR_NAME_KEY, articlesData.get(position).getUser_name());
                intent.putExtra(Constants.AUTHOR_ACCOUNT_KEY, articlesData.get(position).getUser_account());
                intent.putExtra(Constants.AUTHOR_HEAD_URL_KEY, articlesData.get(position).getAuthorHeadUrl());
                intent.putExtra("testUserKey", 1005);
                startActivity(intent);
            }
        });
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rvNewsList.setLayoutManager(llm);
        rvNewsList.setAdapter(newsAdapter);
        refreshData();
    }

    /**
     * 加入浏览记录
     *
     * @param article
     */
    private void insertLocalCache(Articles article) {
        ArticleLocalDataSource.getInstance(getContext()).insertArticle(article);
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
                        Type jsonType = new TypeToken<BaseResponse<List<Articles>>>() {
                        }.getType();
                        BaseResponse<List<Articles>> newsListResponse = gson.fromJson(body, jsonType);
                        for (Articles article : newsListResponse.getData()) {
                            newsAdapter.add(article);
                        }
                        newsAdapter.notifyDataSetChanged();
                        offset += 10;
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

    private void refreshData() {
        swipe.setRefreshing(true);
        if (!type.equals(1)) {
            CURRENT_URL = CURRENT_URL + "?";
        } else {
            articlesData.clear();
        }
        Request request = new Request.Builder()
                .url(CURRENT_URL + "&offset=" + offset)
                .get().build();
        try {
            Log.d(TAG, "refreshData: " + request + " type: " + type);
            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(callback);
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }
    }
}

