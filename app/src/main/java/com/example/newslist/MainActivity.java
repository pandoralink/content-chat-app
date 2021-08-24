package com.example.newslist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.newslist.message.MsgFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 庞旺
 */
public class MainActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private RadioGroup rgTabBar;
    List<Fragment> fragments = new ArrayList<>();
    private static final String TAG = "PW";
    private static int defaultPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        defaultPage = getIntent().getIntExtra("id",0);

        initViews();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        String test = getIntent().getStringExtra("FragmentPosition");
        Log.d("PW", "onRestart: " + test);
    }

    private void initViews() {

        mViewPager = (ViewPager) findViewById(R.id.vp_content);
        rgTabBar = (RadioGroup) findViewById(R.id.rg_tab_bar);

        fragments.add(new NewsFragment());
        fragments.add(new MsgFragment());
        fragments.add(new NewsFragment());
        fragments.add(new UserFragment());

        mViewPager.setAdapter(new NewsFragmentPagerAdapter(getSupportFragmentManager(), fragments));
        mViewPager.setOffscreenPageLimit(2);

        mViewPager.addOnPageChangeListener(mPageChangeListener);
        rgTabBar.setOnCheckedChangeListener(mOnCheckedChangeListener);

        mViewPager.setCurrentItem(defaultPage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager.removeOnPageChangeListener(mPageChangeListener);
    }

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            RadioButton radioButton = (RadioButton) rgTabBar.getChildAt(position);
            radioButton.setChecked(true);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            for (int i = 0; i < group.getChildCount(); i++) {
                if (group.getChildAt(i).getId() == checkedId) {
                    mViewPager.setCurrentItem(i);
                    return;
                }
            }
        }
    };

}