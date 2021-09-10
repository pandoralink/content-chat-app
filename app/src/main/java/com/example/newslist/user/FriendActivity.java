package com.example.newslist.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.newslist.R;
import com.example.newslist.data.Constants;
import com.example.newslist.message.MsgContentActivity;
import com.example.newslist.news.NewsContentActivity;

public class FriendActivity extends AppCompatActivity {
    Boolean userRelate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        Intent intent = getIntent();
        ImageView iv_author_head = findViewById(R.id.iv_author_head);
        TextView tvAuthorName = findViewById(R.id.tv_title);
        Button btnFollow = findViewById(R.id.btn_follow_user);
        String authorName = intent.getStringExtra(Constants.AUTHOR_NAME_KEY);
        String authorHeadUrl = intent.getStringExtra(Constants.AUTHOR_HEAD_URL_KEY);
        userRelate = intent.getBooleanExtra(Constants.USER_RELATE_KEY,false);
        tvAuthorName.setText(authorName);
        Glide.with(FriendActivity.this).load(authorHeadUrl).into(iv_author_head);
        if (userRelate) {
            btnFollow.setText("√已关注");
        } else {
            btnFollow.setText("+关注");
        }
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRelate = !userRelate;
                if (userRelate) {
                    btnFollow.setText("√已关注");
                } else {
                    btnFollow.setText("+关注");
                }
            }
        });
        // 私信按钮
        Button btnDm = findViewById(R.id.btn_dm);
        btnDm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MsgContentActivity.class);
                startActivity(intent);
            }
        });
        ImageView ivFriendOut = findViewById(R.id.iv_friend_out);
        ivFriendOut.setOnClickListener(view -> finish());
    }
}