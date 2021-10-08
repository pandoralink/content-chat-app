package com.example.newslist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.newslist.user.UserInfoActivity;

/**
 * @author 庞旺
 */
public class UserFragment extends Fragment {
    View rootView;
    String spFileName;
    String accountKey;
    String passwordKey;
    private String userIdKey;
    String userNameKey;
    String userHeadKey;
    String name;
    String password;
    private Integer userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_user, container, false);
        }

        Button btnLogin = rootView.findViewById(R.id.btn_user_out);
        RelativeLayout rlUserManagerIn = rootView.findViewById(R.id.rl_user_manager_in);

        initView();
        btnLogin.setOnClickListener(view -> {
            // 退出时断开 ws 连接
            handleUserInfo();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.putExtra("id", 3);
            startActivity(intent);
        });
        rlUserManagerIn.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), UserInfoActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("password", password);
            intent.putExtra("userId", userId);
            startActivity(intent);
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

    public void handleUserInfo() {
        MainActivity.mWebSocket.close(1000, "out");
        String spFileName = getResources().getString(R.string.shared_preferences_file_name);
        String userIdKey = getResources().getString(R.string.userId);
        SharedPreferences spFile = getContext().getSharedPreferences(
                spFileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spFile.edit();
        editor.remove(userIdKey);
        editor.apply();
    }

    private void initView() {
        spFileName = getResources().getString(R.string.shared_preferences_file_name);
        accountKey = getResources().getString(R.string.login_account_name);
        passwordKey = getResources().getString(R.string.login_password);
        userNameKey = getResources().getString(R.string.userName);
        userHeadKey = getResources().getString(R.string.userHead);
        userIdKey = getResources().getString(R.string.userId);
        TextView tvUserName = rootView.findViewById(R.id.tv_user_name);
        TextView tvUserAccount = rootView.findViewById(R.id.tv_user_account);
        Context context = getContext();

        name = getStringValue(context, spFileName, userNameKey);
        password = getStringValue(context, spFileName, passwordKey);
        userId = getIntegerValue(context, spFileName, userIdKey);

        tvUserName.setText(name);
        tvUserAccount.setText(getStringValue(context, spFileName, accountKey));
    }

    private String getStringValue(Context context, String fileName, String key) {
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE).getString(key, null);
    }

    private Integer getIntegerValue(Context context, String fileName, String key) {
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE).getInt(key, 0);
    }
}