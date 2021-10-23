package com.example.newslist;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.newslist.data.Constants;
import com.example.newslist.user.User;
import com.example.newslist.utils.UserInfoManager;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "PW";

    private EditText etRegisterPwd;
    private EditText etUsername;
    private Boolean bPwdSwitch = false;
    private Toast toast;
    private final int MIN_PASSWORD_LENGTH = 6;
    private final int MAX_PASSWORD_LENGTH = 10;
    MaterialButton btnRegister;
    Drawable rotate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etRegisterPwd = findViewById(R.id.et_register_pwd);
        etUsername = findViewById(R.id.et_register_name);
        btnRegister = findViewById(R.id.btn_register);
        toast = Toast.makeText(this, null, Toast.LENGTH_SHORT);
        rotate = ContextCompat.getDrawable(this, R.drawable.animated_rotate);
        rotate.setBounds(0, 0, rotate.getMinimumWidth(), rotate.getMinimumHeight());

        changePw();
        btnRegister.setOnClickListener(view -> {
            register();
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
                    etRegisterPwd.setInputType(
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    ivPwdSwitch.setImageResource(
                            R.drawable.eye_slash_fill);
                    etRegisterPwd.setInputType(
                            InputType.TYPE_TEXT_VARIATION_PASSWORD |
                                    InputType.TYPE_CLASS_TEXT);
                    etRegisterPwd.setTypeface(Typeface.DEFAULT);
                }
            }
        });
    }

    private void register() {
        String userName = etUsername.getText().toString();
        String password = etRegisterPwd.getText().toString();

        if (userName.isEmpty() || password.isEmpty()) {
            toastShowCenter(toast, "用户名或密码不能为空");
            return;
        } else if (password.length() < MIN_PASSWORD_LENGTH) {
            toastShowCenter(toast, "密码长度不能小于 6");
            return;
        } else if (password.length() > MAX_PASSWORD_LENGTH) {
            toastShowCenter(toast, "密码长度不能大于 10");
            return;
        }

        startAnimation();

        Request request = new Request.Builder()
                .url(Constants.REMOTE_REGISTER_BASE_URL + "?user_name=" + userName + "&user_password=" + password)
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
                User user = new Gson().fromJson(body, User.class);
                UserInfoManager userInfoManager = new UserInfoManager(RegisterActivity.this);
                userInfoManager.initUserInfo(user);

                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }

        @Override
        public void onFailure(Call call, IOException e) {
            Log.e(TAG, "Failed to connect server!");
            runOnUiThread(() -> {
                stopAnimation();
                toastShowCenter(toast, "出现意外错误");
            });
            e.printStackTrace();
        }
    };

    private void stopAnimation() {
        ((Animatable) rotate).stop();
        btnRegister.setIcon(null);
    }

    private void startAnimation() {
        btnRegister.setIcon(rotate);
        btnRegister.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_START);
        ((Animatable) rotate).start();
    }

    private void toastShowCenter(Toast toast, String msg) {
        toast.setText(msg);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}