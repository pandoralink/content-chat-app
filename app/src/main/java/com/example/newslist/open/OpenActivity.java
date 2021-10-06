package com.example.newslist.open;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.newslist.LoginActivity;
import com.example.newslist.MainActivity;
import com.example.newslist.R;

public class OpenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                Intent intent = new Intent(OpenActivity.this, LoginActivity.class);
//                startActivity(intent);
//                finish();
                String spFileName = getResources().getString(R.string.shared_preferences_file_name);
                String userIdKey = getResources().getString(R.string.userId);
                SharedPreferences spFile = getSharedPreferences(
                        spFileName,
                        Context.MODE_PRIVATE);
                int userId = spFile.getInt(userIdKey, 0);
                Log.d("PW", "run: " + userId);
                Intent intent;
                if (userId == 0) {
                    intent = new Intent(OpenActivity.this, LoginActivity.class);
                } else {
                    intent = new Intent(OpenActivity.this, MainActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 1000);
    }
}