package com.example.newslist;

import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsFragment extends Fragment {

    View rootView;
    private ListView lvNewsList;
    private NewsAdapter adapter;
    private List<News> newsData;
    private int[] mCols = new int[]{Constants.NEWS_COL5,
            Constants.NEWS_COL7, Constants.NEWS_COL8,
            Constants.NEWS_COL10, Constants.NEWS_COL11};
    private int mCurrentColIndex = 0;
    private static final String TAG = "SERVER";
    private String currentUrl = "";
    private int currentPage = 1;
    private SwipeRefreshLayout swipe;

    public NewsFragment(String url) {
        currentUrl = url;
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
                        Type jsonType = new TypeToken<BaseResponse<List<News>>>() {}.getType();
                        BaseResponse<List<News>> newsListResponse = gson.fromJson(body, jsonType);
                        Log.d("PW", "run: " + newsListResponse.getData().size());
                        for (News news : newsListResponse.getData()) {
                            adapter.insert(news,0);
//                            adapter.add(0,news);
                        }
                        adapter.notifyDataSetChanged();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_blank, container, false);
        }

        lvNewsList = rootView.findViewById(R.id.lv_news_list);

        lvNewsList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(getActivity(), DetailActivity.class);

                        News news = adapter.getItem(i);
                        intent.putExtra(Constants.NEWS_DETAIL_URL_KEY, news.getContentUrl());

                        startActivity(intent);
                    }
                });

        initData();

        swipe = rootView.findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage++;
                Log.d(TAG, new Date().toString() + "当前页面值为：" + currentPage);
                refreshData(currentPage);
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
        adapter = new NewsAdapter(getActivity(),
                R.layout.list_item, newsData);
        lvNewsList.setAdapter(adapter);

        refreshData(currentPage);
    }

    private void refreshData(final int page) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                NewsRequest requestObj = new NewsRequest();

                requestObj.setCol(mCols[mCurrentColIndex]);
                requestObj.setNum(Constants.NEWS_NUM);
                requestObj.setPage(page);
                String urlParams = requestObj.toString();

                Request request = new Request.Builder()
                        .url(currentUrl + urlParams)
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