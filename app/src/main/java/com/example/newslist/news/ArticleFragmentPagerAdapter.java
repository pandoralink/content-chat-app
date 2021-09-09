package com.example.newslist.news;

import android.util.Log;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ArticleFragmentPagerAdapter extends FragmentPagerAdapter {
    private String[] mTitles = new String[]{"关注", "推荐"};
    List<Fragment> fragments = new ArrayList<>();

    public ArticleFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragments = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("PW", "getItem: " + position);
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    /**
     * ViewPager与TabLayout绑定后，这里获取到PageTitle就是Tab的Text
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}


