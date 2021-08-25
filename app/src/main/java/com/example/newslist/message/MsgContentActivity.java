package com.example.newslist.message;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newslist.LoginActivity;
import com.example.newslist.MainActivity;
import com.example.newslist.R;

/**
 * @author 庞旺
 */
public class MsgContentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_content);

        ImageView msgContentOut = findViewById(R.id.msg_content_out);
        ImageView ivMsgOption = findViewById(R.id.iv_msg_option);
        GridLayout glMsgOption = findViewById(R.id.gl_msg_option);

        msgContentOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ivMsgOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(glMsgOption.getVisibility() == View.VISIBLE) {
                    glMsgOption.setVisibility(View.GONE);
                } else {
                    glMsgOption.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
