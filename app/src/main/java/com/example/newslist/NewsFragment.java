package com.example.newslist;

import android.content.Intent;
import android.content.res.TypedArray;
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

import com.example.newslist.data.BaseResponse;
import com.example.newslist.data.Constants;
import com.example.newslist.news.NewsContentActivity;
import com.example.newslist.popup.OperationDialogFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * @author 庞旺
 */
public class NewsFragment extends Fragment {

    private static final String TAG = "PW";
    View rootView;
    private RecyclerView rvNewsList;
    private NewsAdapter newsAdapter;
    private List<News> newsData;
    private SwipeRefreshLayout swipe;
    private String[] titles = null;
    private String[] authors = null;
    private String CURRENT_URL;

    public NewsFragment() {
        CURRENT_URL = Constants.ARTICLE_URL;
    }

    public NewsFragment(String currentUrl) {
        CURRENT_URL = currentUrl;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.news_fragment, container, false);
        }

        rvNewsList = rootView.findViewById(R.id.lv_news_list);

        initData();
        swipe = rootView.findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                refreshData();
            }
        });
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
        newsData = new ArrayList<>();
        newsAdapter = new NewsAdapter(getContext(), R.layout.list_item, newsData);
        newsAdapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                new OperationDialogFragment().show(getActivity().getSupportFragmentManager(), "OperationDialogFragment");
            }

            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), NewsContentActivity.class);
                intent.putExtra(Constants.ARTICLE_URL_KEY, newsData.get(position).getArticle());
                intent.putExtra(Constants.ARTICLE_AUTHOR_INFO_KEY,newsData.get(position).getaId());
                startActivity(intent);
            }
        });
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rvNewsList.setLayoutManager(llm);
        rvNewsList.setAdapter(newsAdapter);
        refreshData();
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
                        for (News news : newsListResponse.getData()) {
                            newsAdapter.add(news);
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

    private void refreshData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url(CURRENT_URL)
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

