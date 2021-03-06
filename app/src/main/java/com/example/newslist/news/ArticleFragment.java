package com.example.newslist.news;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.newslist.R;
import com.example.newslist.data.Constants;
import com.example.newslist.utils.UserInfoManager;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class ArticleFragment extends Fragment {
    View rootView;
    private ViewPager vpArticleContent;
    private TabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_article, container, false);
        }
        initView();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (rootView != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }

    private void initView() {
        if (rootView != null) {
            tabLayout = rootView.findViewById(R.id.tl_tabs);
            vpArticleContent = rootView.findViewById(R.id.vp_article_content);
            List<Fragment> articleFragment = new ArrayList<>();
            UserInfoManager userInfoManager = new UserInfoManager(getContext());

            articleFragment.add(new ArticleListFragment(Constants.FOLLOW_AUTHOR_ARTICLE_URL + "?fan_id=" + userInfoManager.getUserId(),1));
            articleFragment.add(new ArticleListFragment());
            vpArticleContent.setAdapter(new ArticleFragmentPagerAdapter(getChildFragmentManager(), articleFragment));
            vpArticleContent.setOffscreenPageLimit(1);
            vpArticleContent.setCurrentItem(1);
            tabLayout.setupWithViewPager(vpArticleContent);
        }
    }
}

