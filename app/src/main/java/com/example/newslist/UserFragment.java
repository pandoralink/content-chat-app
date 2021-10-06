package com.example.newslist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

/**
 * @author 庞旺
 */
public class UserFragment extends Fragment {
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_user, container, false);
        }

        Button btnLogin = rootView.findViewById(R.id.btn_user_out);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 退出时断开 ws 连接
                handleUserInfo();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.putExtra("id",3);
                startActivity(intent);
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

    public void handleUserInfo() {
        MainActivity.mWebSocket.close(1000,"out");
        String spFileName = getResources().getString(R.string.shared_preferences_file_name);
        String userIdKey = getResources().getString(R.string.userId);
        SharedPreferences spFile = getContext().getSharedPreferences(
                spFileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spFile.edit();
        editor.remove(userIdKey);
        editor.apply();
    }
}