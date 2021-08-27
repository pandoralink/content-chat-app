package com.example.newslist.createNew;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;

import com.example.newslist.R;
import com.example.newslist.data.Constants;

/**
 * @author 庞旺
 */
public class CreateNewFragment extends Fragment {
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_create_new, container, false);
        }
        String url = Constants.SERVER_URL;
        WebView webView = rootView.findViewById(R.id.wv_edit);
        webView.setWebViewClient(new WebViewClient() {
            //设置在webView点击打开的新网页在当前界面显示,而不跳转到新的浏览器中
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        /* 设置WebView属性,运行执行js脚本 */
        webView.getSettings().setJavaScriptEnabled(true);
        /* //调用loadUrl方法为WebView加入链接 */
        webView.loadUrl(url);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (rootView != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }

}