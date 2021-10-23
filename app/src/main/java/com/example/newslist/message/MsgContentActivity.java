package com.example.newslist.message;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newslist.MainActivity;
import com.example.newslist.R;
import com.example.newslist.data.local.ArticleLocalDataSource;
import com.example.newslist.message.core.ItemAdapter;
import com.example.newslist.message.core.Msg;
import com.example.newslist.message.core.MySQLiteHelper;
import com.example.newslist.utils.UserInfoManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 庞旺
 */
public class MsgContentActivity extends AppCompatActivity implements View.OnClickListener {

    public static List<Msg> msgList = new ArrayList<>();
    public static RecyclerView msgRecyclerView;
    public static ItemAdapter adapter;
    private ImageView msgContentOut;
    private ImageView ivMsgOption;
    private GridLayout glMsgOption;
    private EditText etMsgContent;
    private Button msgSendBtn;
    private Socket socketSend;
    private String ip = "122.9.150.124";
    private String port = "6666";
    DataInputStream in;
    DataOutputStream out;
    boolean isRunning = false;
    private String myName = "我";
    private String friendName;
    private int friendId;
    private String recMsg;
    private boolean isSend = false;
    MySQLiteHelper openHelper;
    UserInfoManager userInfoManager;
    public static int readStatus = 0;


    private void initDB() {
        openHelper = new MySQLiteHelper(this, "chat.db", null, 1);
    }

    private void readChat() {
        String msg_content;
        String msg_date;
        int msg_type;
        SQLiteDatabase db = openHelper.getReadableDatabase();
        String sql = "select msg_content,msg_date,msg_type from Chat where friend_name=?";
        Cursor cursor = db.rawQuery(sql, new String[]{friendName});
        while (cursor.moveToNext()) {
            msg_content = cursor.getString(cursor.getColumnIndex("msg_content"));
            msg_date = cursor.getString(cursor.getColumnIndex("msg_date"));
            msg_type = cursor.getInt(cursor.getColumnIndex("msg_type"));
            StringBuilder sb = new StringBuilder();
            sb.append(msg_date).append("\n" + msg_content);
            Log.d("sql语句", sql);
            Log.d("获取本地数据库中“我”和" + friendName + "的聊天记录", msg_content + " " + msg_date + " " + msg_type);
            Msg msg = new Msg(sb.toString(), msg_type);
            msgList.add(msg);
        }
        db.close();

        msgList = ArticleLocalDataSource.getInstance(this).getMsgList(friendName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_content);

        Intent intent = getIntent();
        friendName = intent.getStringExtra("friendName");
        friendId = intent.getIntExtra("authorId", 0);

        msgContentOut = findViewById(R.id.msg_content_out);
        glMsgOption = findViewById(R.id.gl_msg_option);
        etMsgContent = findViewById(R.id.et_msg_content);
        msgSendBtn = findViewById(R.id.msg_send_btn);
        //用于获取用户id
        userInfoManager = new UserInfoManager(MsgContentActivity.this);

        msgContentOut.setOnClickListener(this);

        initDB();
        if (readStatus == 0) {
            readChat();
        }
        Log.d("friendId", String.valueOf(friendId));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayoutManager layoutManager = new LinearLayoutManager(MsgContentActivity.this);
                msgRecyclerView = findViewById(R.id.lv_article_list);
                msgRecyclerView.setLayoutManager(layoutManager);
                adapter = new ItemAdapter(msgList);
                msgRecyclerView.setAdapter(adapter);
            }
        });

        msgSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = etMsgContent.getText().toString();
                @SuppressLint("SimpleDateFormat")
                String date = new SimpleDateFormat("hh:mm:ss").format(new Date());
                StringBuilder sb = new StringBuilder();
                sb.append(date).append("\n" + inputText);
                if (!"".equals(inputText)) {
                    MainActivity.setConfigure(inputText, friendName, friendId, true);
                    inputText = sb.toString();
                    Msg msg = new Msg(inputText, Msg.TYPE_SENT);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size() - 1);
                    msgRecyclerView.scrollToPosition(msgList.size() - 1);
                } else {
                    Toast.makeText(MsgContentActivity.this, "消息不能为空", Toast.LENGTH_SHORT).show();
                }
                sb.delete(0, sb.length());
                etMsgContent.setText("");
            }
        });
    }

    public static void setReadStatus(int readStatus1) {
        readStatus = readStatus1;
    }

    @Override
    public void onClick(View v) {
        finish();
        MainActivity.setPosition(0);

        //设置消息列表中显示与每个好友的最后一条消息
        Msg msg = msgList.get(msgList.size() - 1);

        msgList.clear();
    }
}

