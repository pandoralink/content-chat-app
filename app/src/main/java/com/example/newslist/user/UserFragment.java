package com.example.newslist.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.newslist.LoginActivity;
import com.example.newslist.MainActivity;
import com.example.newslist.R;
import com.example.newslist.data.local.ArticleLocalDataSource;

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
    TextView tvUserName;
    private final Integer userNameResultCode = 10;
    private final Integer userInfoRequestCode = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_user, container, false);
        }

        Button btnLogin = rootView.findViewById(R.id.btn_user_out);
        RelativeLayout rlUserManagerIn = rootView.findViewById(R.id.rl_user_manager_in);
        RelativeLayout rlUserReadManager = rootView.findViewById(R.id.rl_user_read_manager);
        RelativeLayout rlUserCreateManager = rootView.findViewById(R.id.rl_user_create_manager);
        tvUserName = rootView.findViewById(R.id.tv_user_page_name);

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
            startActivityForResult(intent, userInfoRequestCode);
        });
        rlUserReadManager.setOnClickListener(view -> {
            Log.d("PW", "onCreateView: " + "in");
            Intent intent = new Intent(getContext(), UserReadActivity.class);
            startActivity(intent);
        });
        rlUserCreateManager.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), UserCreateArticleActivity.class);
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

        clearUserLocalData();
    }

    private void clearUserLocalData() {
        ArticleLocalDataSource.getInstance(getContext()).clearAll();
    }

    private void initView() {
        spFileName = getResources().getString(R.string.shared_preferences_file_name);
        accountKey = getResources().getString(R.string.login_account_name);
        passwordKey = getResources().getString(R.string.login_password);
        userNameKey = getResources().getString(R.string.userName);
        userHeadKey = getResources().getString(R.string.userHead);
        userIdKey = getResources().getString(R.string.userId);
        TextView tvUserAccount = rootView.findViewById(R.id.tv_user_account);
        Context context = getContext();

        name = getStringValue(context, spFileName, userNameKey);
        password = getStringValue(context, spFileName, passwordKey);
        userId = getIntegerValue(context, spFileName, userIdKey);

        tvUserName.setText(name);
        tvUserAccount.setText(getStringValue(context, spFileName, accountKey));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (userNameResultCode == resultCode && userInfoRequestCode == requestCode) {
            String tempName = data.getStringExtra("name");
            tvUserName.setText(tempName);
            name = tempName;
        }
    }

    private String getStringValue(Context context, String fileName, String key) {
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE).getString(key, null);
    }

    private Integer getIntegerValue(Context context, String fileName, String key) {
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE).getInt(key, 0);
    }
}