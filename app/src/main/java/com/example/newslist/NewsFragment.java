package com.example.newslist;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.newslist.news.NewsContentActivity;
import com.example.newslist.popup.OperationDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * @author 庞旺
 */
public class NewsFragment extends Fragment {

    View rootView;
    private RecyclerView rvNewsList;
    private NewsAdapter newsAdapter;
    private List<News> newsData;
    private SwipeRefreshLayout swipe;
    private String[] titles = null;
    private String[] authors = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.news_fragment, container, false);
        }

        rvNewsList = rootView.findViewById(R.id.lv_news_list);

        initData();

        newsAdapter = new NewsAdapter(getContext(), R.layout.list_item, newsData);
        newsAdapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                new OperationDialogFragment().show(getActivity().getSupportFragmentManager(),"OperationDialogFragment");
            }

            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), NewsContentActivity.class);
                startActivity(intent);
            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rvNewsList.setLayoutManager(llm);
        rvNewsList.setAdapter(newsAdapter);

        swipe = rootView.findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
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

            newsData.add(news);
        }
    }

    private void refreshData() {
        Random random = new Random();
        int index = random.nextInt(19);

        News news = new News();

        TypedArray images = getResources().obtainTypedArray(R.array.images);
        news.setTitle(titles[index]);
        news.setAuthor(authors[index]);
        news.setImageId(images.getResourceId(index,0));

        newsAdapter.add(news);
        newsAdapter.notifyDataSetChanged();
        swipe.setRefreshing(false);
    }
}