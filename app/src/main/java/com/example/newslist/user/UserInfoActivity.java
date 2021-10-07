package com.example.newslist.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.newslist.LoginActivity;
import com.example.newslist.MainActivity;
import com.example.newslist.R;

public class UserInfoActivity extends AppCompatActivity {
    private EditText etPwd;
    private EditText etUserInfoName;
    private Boolean bPwdSwitch = false;
    private String oldPassword;
    private String oldName;
    private Button btnModifyUserInfo;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        ImageView ivInfoOut = findViewById(R.id.iv_info_out);
        etPwd = findViewById(R.id.et_pwd);
        etUserInfoName = findViewById(R.id.et_user_info_name);
        btnModifyUserInfo = findViewById(R.id.btn_modify_user_info);
        toast = Toast.makeText(UserInfoActivity.this, null, Toast.LENGTH_SHORT);

        ivInfoOut.setOnClickListener(view -> {
            finish();
        });
        btnModifyUserInfo.setOnClickListener(view -> {
            modifyUserInfo();
        });
        changePw();
        initData();
        initView();
    }

    private void modifyUserInfo() {
//        if(!etPwd.getText().equals(oldPassword)) {
//
//        }
//        else {
//            finish();
//        }
        toast.setText("身份认证过期，请重新登录");
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        handleUserInfo();
        Intent intent = new Intent();
        intent.setClass(UserInfoActivity.this,
                LoginActivity.class);
        startActivity(intent);
    }

    public void changePw() {
        final ImageView ivPwdSwitch = findViewById(R.id.iv_pwd_switch);
        ivPwdSwitch.setOnClickListener(view -> {
            bPwdSwitch = !bPwdSwitch;
            if (bPwdSwitch) {
                ivPwdSwitch.setImageResource(
                        R.drawable.ic_eye_fill_24);
                etPwd.setInputType(
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                ivPwdSwitch.setImageResource(
                        R.drawable.ic_eye_slash_fill_32);
                etPwd.setInputType(
                        InputType.TYPE_TEXT_VARIATION_PASSWORD |
                                InputType.TYPE_CLASS_TEXT);
                etPwd.setTypeface(Typeface.DEFAULT);
            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        oldName = intent.getStringExtra("name");
        oldPassword = intent.getStringExtra("password");
    }

    private void initView() {
        etUserInfoName.setText(oldName);
        etPwd.setText(oldPassword);
    }

    private void handleUserInfo() {
        MainActivity.mWebSocket.close(1000, "out");
        String spFileName = getResources().getString(R.string.shared_preferences_file_name);
        String userIdKey = getResources().getString(R.string.userId);
        SharedPreferences spFile = getSharedPreferences(
                spFileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spFile.edit();
        editor.remove(userIdKey);
        editor.apply();
    }
}