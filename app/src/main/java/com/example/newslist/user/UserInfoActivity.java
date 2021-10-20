package com.example.newslist.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.text.InputType;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.newslist.LoginActivity;
import com.example.newslist.MainActivity;
import com.example.newslist.R;
import com.example.newslist.data.Constants;
import com.example.newslist.utils.UserInfoManager;
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserInfoActivity extends AppCompatActivity {
    private EditText etUserInfoPwd;
    private EditText etUserInfoName;
    private Boolean bPwdSwitch = false;
    private String oldPassword;
    private String oldName;
    private MaterialButton btnModifyUserInfo;
    private Toast toast;
    OkHttpClient okHttpClient;
    private Integer userId;
    private static final String SUCCESS = "success";
    UserInfoManager userInfoManager;
    Drawable rotate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        ImageView ivInfoOut = findViewById(R.id.iv_info_out);
        etUserInfoPwd = findViewById(R.id.et_user_info_pwd);
        etUserInfoName = findViewById(R.id.et_user_info_name);
        btnModifyUserInfo = findViewById(R.id.btn_modify_user_info);
        toast = Toast.makeText(UserInfoActivity.this, null, Toast.LENGTH_SHORT);
        okHttpClient = new OkHttpClient();
        userInfoManager = new UserInfoManager(UserInfoActivity.this);
        rotate = ContextCompat.getDrawable(UserInfoActivity.this, R.drawable.animated_rotate);
        rotate.setBounds(0, 0, rotate.getMinimumWidth(), rotate.getMinimumHeight());

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
        if (etUserInfoPwd.getText().toString().equals(oldPassword) && etUserInfoName.getText().toString().equals(oldName)) {
            /**
             * EditText.getText() 可能是不同编码字符串
             * 需要再使用 toString() 与 old 进行比较
             */
            toast.setText("没有做出任何修改");
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            update();
        }
    }

    public void changePw() {
        final ImageView ivPwdSwitch = findViewById(R.id.iv_pwd_switch);
        ivPwdSwitch.setOnClickListener(view -> {
            bPwdSwitch = !bPwdSwitch;
            if (bPwdSwitch) {
                ivPwdSwitch.setImageResource(
                        R.drawable.ic_eye_fill_24);
                etUserInfoPwd.setInputType(
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                ivPwdSwitch.setImageResource(
                        R.drawable.ic_eye_slash_fill_32);
                etUserInfoPwd.setInputType(
                        InputType.TYPE_TEXT_VARIATION_PASSWORD |
                                InputType.TYPE_CLASS_TEXT);
                etUserInfoPwd.setTypeface(Typeface.DEFAULT);
            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        oldName = intent.getStringExtra("name");
        oldPassword = intent.getStringExtra("password");
        userId = intent.getIntExtra("userId", 0);
    }

    private void initView() {
        etUserInfoName.setText(oldName);
        etUserInfoPwd.setText(oldPassword);
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

    private void update() {
        startAnimation();

        String password = etUserInfoPwd.getText().toString();
        String name = etUserInfoName.getText().toString();
        Request request = new Request.Builder()
                .url(Constants.ARTICLE_AUTHOR_INFO_UPDATE_BASE_URL + "?user_id=" + userId
                        + "&user_name=" + name
                        + "&user_password=" + password)
                .get().build();
        try {
            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response)
                        throws IOException {
                    if (response.isSuccessful()) {
                        final String body = response.body().string();
                        runOnUiThread(() -> {
                            if (SUCCESS.equals(body)) {
                                if (password.equals(oldPassword)) {
                                    // 密码未修改则不转到 LoginActivity
                                    stopAnimation();
                                    userInfoManager.initEditor();
                                    userInfoManager.updateUserName(name);
                                    toastShowCenter(toast, "修改成功");
                                    Intent intent = new Intent(UserInfoActivity.this, UserFragment.class);
                                    intent.putExtra("name", name);
                                    UserInfoActivity.this.setResult(10, intent);
                                    finish();
                                    return;
                                }
                                stopAnimation();
                                toastShowCenter(toast, "身份认证过期，请重新登录");
                                handleUserInfo();
                                Intent intent = new Intent();
                                intent.setClass(UserInfoActivity.this,
                                        LoginActivity.class);
                                startActivity(intent);
                            } else {
                                toastShowCenter(toast, "修改失败，请重新修改或者检查网络");
                            }
                        });
                    }
                }
            });
        } catch (NetworkOnMainThreadException ex) {
            runOnUiThread(() -> {
                stopAnimation();
                toastShowCenter(toast, "出现意外错误");
            });
            ex.printStackTrace();
        }
    }

    private void toastShowCenter(Toast toast, String msg) {
        toast.setText(msg);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void stopAnimation() {
        ((Animatable) rotate).stop();
        btnModifyUserInfo.setIcon(null);
    }

    private void startAnimation() {
        btnModifyUserInfo.setIcon(rotate);
        btnModifyUserInfo.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_START);
        ((Animatable) rotate).start();
    }
}