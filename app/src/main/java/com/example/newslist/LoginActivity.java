package com.example.newslist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newslist.data.Constants;
import com.example.newslist.user.User;
import com.example.newslist.utils.UserInfo;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "PW";

    private EditText etPwd;
    private EditText etAccount;
    private CheckBox cbRememberPwd;
    private Boolean bPwdSwitch = false;
    private Toast toast;
    private static final String FAILURE = "failure";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button btnLogin = findViewById(R.id.login);
        etPwd = findViewById(R.id.et_pwd);
        etAccount = findViewById(R.id.account);
        cbRememberPwd = findViewById(R.id.rememberPwd);
        toast = Toast.makeText(LoginActivity.this, null, Toast.LENGTH_SHORT);
        // 初始化 view
        initLoginView();
        changePw();

        btnLogin.setOnClickListener(view -> {
            login();
        });
    }

    /**
     * 切换密码显示
     */
    public void changePw() {
        final ImageView ivPwdSwitch = findViewById(R.id.iv_pwd_switch);
        ivPwdSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                bPwdSwitch = !bPwdSwitch;
                if (bPwdSwitch) {
                    ivPwdSwitch.setImageResource(
                            R.drawable.eye_fill);
                    etPwd.setInputType(
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    ivPwdSwitch.setImageResource(
                            R.drawable.eye_slash_fill);
                    etPwd.setInputType(
                            InputType.TYPE_TEXT_VARIATION_PASSWORD |
                                    InputType.TYPE_CLASS_TEXT);
                    etPwd.setTypeface(Typeface.DEFAULT);
                }
            }
        });
    }

    public void savePw() {
        String spFileName = getResources().getString(R.string.shared_preferences_file_name);
        String accountKey = getResources().getString(R.string.login_account_name);
        String passwordKey = getResources().getString(R.string.login_password);
        String rememberPasswordKey = getResources().getString(R.string.login_remember_password);
        SharedPreferences spFile = getSharedPreferences(
                spFileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spFile.edit();

        if (cbRememberPwd.isChecked()) {
            String password = etPwd.getText().toString();
            String account = etAccount.getText().toString();

            editor.putString(accountKey, account);
            editor.putString(passwordKey, password);
            editor.putBoolean(rememberPasswordKey, true);
        } else {
            editor.remove(accountKey);
            editor.remove(passwordKey);
            editor.remove(rememberPasswordKey);
        }
        editor.apply();

    }

    public void initLoginView() {
        String spFileName = getResources().getString(R.string.shared_preferences_file_name);
        String accountKey = getResources().getString(R.string.login_account_name);
        String passwordKey = getResources().getString(R.string.login_password);
        String rememberPasswordKey = getResources().getString(R.string.login_remember_password);
        SharedPreferences spFile = getSharedPreferences(
                spFileName,
                Context.MODE_PRIVATE);

        String account = spFile.getString(accountKey, null);
        String password = spFile.getString(passwordKey, null);
        Boolean rememberPassword = spFile.getBoolean(
                rememberPasswordKey,
                false);

        if (account != null && !TextUtils.isEmpty(account)) {
            etAccount.setText(account);
        }

        if (password != null && !TextUtils.isEmpty(password)) {
            etPwd.setText(password);
        }

        cbRememberPwd.setChecked(rememberPassword);
    }

    private void login() {
        Request request = new Request.Builder()
                .url(Constants.REMOTE_LOGIN_BASE_URL + "?user_account=" + etAccount.getText().toString() + "&user_password=" + etPwd.getText().toString())
                .get().build();
        try {
            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(callback);
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }
    }

    private okhttp3.Callback callback = new okhttp3.Callback() {
        @Override
        public void onResponse(Call call, Response response)
                throws IOException {
            if (response.isSuccessful()) {
                final String body = response.body().string();
                if (!FAILURE.equals(body)) {
                    savePw();
                    User user = new Gson().fromJson(body, User.class);
                    new UserInfo(LoginActivity.this, user);
                    Log.d(TAG, "onResponse: " + UserInfo.userId);
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this,
                            MainActivity.class);
                    startActivity(intent);
                } else {
                    runOnUiThread(() -> {
                        toast.setText("账号/密码错误");
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    });
                }
            }
        }

        @Override
        public void onFailure(Call call, IOException e) {
            Log.e(TAG, "Failed to connect server!");
            e.printStackTrace();
        }
    };
}