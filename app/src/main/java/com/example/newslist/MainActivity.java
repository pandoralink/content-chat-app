package com.example.newslist;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.newslist.createNew.CreateNewFragment;
import com.example.newslist.data.Constants;
import com.example.newslist.data.MsgTip;
import com.example.newslist.message.Messages;
import com.example.newslist.message.MsgContentActivity;
import com.example.newslist.message.MsgFragment;
import com.example.newslist.message.core.ListSQLiteHelper;
import com.example.newslist.message.core.Msg;
import com.example.newslist.message.core.MySQLiteHelper;
import com.example.newslist.news.ArticleFragment;
import com.example.newslist.user.UserFragment;
import com.example.newslist.utils.UserInfoManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * @author 庞旺
 */
public class MainActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private RadioGroup rgTabBar;
    List<Fragment> fragments = new ArrayList<>();
    private static final String TAG = "PW";
    private static final String CHANNEL_ID = "Comment Channel";
    private static int defaultPage = 0;
    Gson gson = new Gson();
    MsgFragment msgFragment;
    /**
     * num: 通知/提醒数量
     */
    public int num = 0;
    /**
     * 用 public static 关 ws 似乎有些 Bug
     * 但我还没有遇见
     */
    public static WebSocket mWebSocket;

    /* 分割线 */
    private Socket socketSend;
    private String ip = "172.20.10.2";
    private String port = "6666";
    DataInputStream in;
    DataOutputStream out;
    private String myName = "我";
    private String recMsg;
    private String recId1;
    private String recId2;
    private String recName;
    private String recContent;
    private String strStart = "(";
    private String strMidStart = ",";
    private String strMidEnd = ";";
    private String strEnd = ")";
    public static String friendName;
    public static int friendId;
    public static String content;
    boolean isRunning = false;
    public static boolean isSend = false;
    MySQLiteHelper openHelper;
    ListSQLiteHelper listSQLiteHelper;
    UserInfoManager userInfoManager;
    public static int position = 0;

    String table = "ChatList";
    String[] columns = new String[]{"friend_name", "friend_id"};
    String selection = "friend_id=?";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        defaultPage = getIntent().getIntExtra("fragmentIndex", 0);

        initViews();
        initWebSocket();
        startThread();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: " + "defaultPage" + getIntent().getIntExtra("fragmentIndex", 0));
    }

    private void initViews() {
        mViewPager = findViewById(R.id.vp_content);
        rgTabBar = findViewById(R.id.rl_tab_bar);
        msgFragment = new MsgFragment();

        fragments.add(new ArticleFragment());
        fragments.add(msgFragment);
        fragments.add(new CreateNewFragment());
        fragments.add(new UserFragment());

        mViewPager.setAdapter(new NewsFragmentPagerAdapter(getSupportFragmentManager(), fragments));
        /**
         * setOffscreenPageLimit()
         * 会导致 ViewPager 中所有
         * 页面都加载完才显示第一个页面
         */
        mViewPager.setOffscreenPageLimit(4);

        mViewPager.addOnPageChangeListener(mPageChangeListener);
        rgTabBar.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mViewPager.setCurrentItem(defaultPage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager.removeOnPageChangeListener(mPageChangeListener);
    }

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            RadioButton radioButton = (RadioButton) rgTabBar.getChildAt(position);
            radioButton.setChecked(true);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            for (int i = 0; i < group.getChildCount(); i++) {
                if (group.getChildAt(i).getId() == checkedId) {
                    mViewPager.setCurrentItem(i);
                    return;
                }
            }
        }
    };

    private WebSocketListener webSocketListener = new WebSocketListener() {
        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            super.onOpen(webSocket, response);
            Log.e(TAG, "连接成功");
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            super.onMessage(webSocket, text);
            Log.d(TAG, "来消息了");
            Messages message = new Messages();
            message.setFriendName("defaultName");
            message.setFirstMsg(text);
            Type type = new TypeToken<List<MsgTip>>() {
            }.getType();
            List<MsgTip> msgTipList = gson.fromJson(text, type);
            runOnUiThread(() -> {
                for (MsgTip msgTip : msgTipList) {
                    sendNotification(msgTip.getContent(), msgTip.getName(), msgTip.getHeadUrl(), msgTip.getContentUrl(), msgTip.getAid());
                }
            });
        }

        @Override
        public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosing(webSocket, code, reason);
            Log.d(TAG, "连接关闭");
        }

        @Override
        public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosed(webSocket, code, reason);
            Log.d(TAG, "连接关闭");
        }

        @Override
        public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
            super.onFailure(webSocket, t, response);
            // 出于服务器和网络状态等原因，必须多次重连
            Log.d(TAG, "onFailure: " + t);
            Log.e(TAG, "连接失败");
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "连接失败，请重启APP", Toast.LENGTH_SHORT).show());
        }
    };

    private void initWebSocket() {
        String wsUrl = Constants.REMOTE_WEBSOCKET_URL + "/?id=1005";
        //构造request对象
        Request request = new Request.Builder()
                .url(wsUrl)
                .build();
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .pingInterval(10, TimeUnit.SECONDS)
                    .build();
            mWebSocket = client.newWebSocket(request, webSocketListener);
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }
    }

    private void sendNotification(String content, String name, String headUrl, String contentUrl, Integer aid) {
        Log.i(TAG, "sendNotification: " + content);
        Intent intent = new Intent();
        intent.putExtra("fragmentIndex", 1);
        intent.setClass(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        this,
                        CHANNEL_ID);
        Notification notification = builder
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_article_24)
                .setContentTitle(name + "回复了你")
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setPriority(2)
                .build();
        NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_NAME = "your_custom_name";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notifyManager.createNotificationChannel(notificationChannel);
        }
        //id要保证唯一
        notifyManager.notify(num++, notification);
        Messages messages = new Messages();
        messages.setFriendName(name + "回复了你");
        messages.setFirstMsg(content);
        messages.setType(2);
        messages.setHeadUrl(headUrl);
        messages.setContentUrl(contentUrl);
        messages.setAid(aid);
        msgFragment.addTip(messages);
    }

    private void startThread() {
        initDB();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if ((socketSend = new Socket(ip, Integer.parseInt(port))) == null) {
                        Log.d("SSS", "Socket失败");
                    } else {
                        isRunning = true;
                        Log.d("SSS", "即将开启线程");
                        in = new DataInputStream(socketSend.getInputStream());
                        out = new DataOutputStream(socketSend.getOutputStream());
//                        out.writeUTF(String.valueOf(userId));//使用用户id辨别身份
                        new Thread(new MainActivity.receive(), "接收线程").start();
                        new Thread(new MainActivity.send(), "发送线程").start();
                    }
                } catch (Exception e) {
                    isRunning = false;
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(MainActivity.this, "连接服务器失败！！！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    try {
                        socketSend.close();
                        socketSend = null;
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    finish();
                }
            }
        }).start();
    }

    /* 分割线 */
    //获取当前进程的Looper对象传给handler
    private Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {//?
            Log.d("handler", "handler");
            if (!recContent.isEmpty()) {
                addNewMessage(recContent, Msg.TYPE_RECEIVED);//添加新数据
            }
        }
    };

    public void addNewMessage(String msg, int type) {
        @SuppressLint("SimpleDateFormat")
        String date = new SimpleDateFormat("hh:mm:ss").format(new Date());
        StringBuilder sb = new StringBuilder();
        sb.append(date).append("\n" + msg);
        msg = sb.toString();
        Msg message = new Msg(msg, type);
        MsgContentActivity.msgList.add(message);
        MsgContentActivity.adapter.notifyItemInserted(MsgContentActivity.msgList.size() - 1);
        MsgContentActivity.msgRecyclerView.scrollToPosition(MsgContentActivity.msgList.size() - 1);
        sb.delete(0, sb.length());
    }

    private void initDB() {
        openHelper = new MySQLiteHelper(this, "chat.db", null, 1);
        listSQLiteHelper = new ListSQLiteHelper(this, "chat.db", null, 1);
    }

    class receive implements Runnable {
        @Override
        public void run() {
            recMsg = "";
            recId1 = "";
            recId2 = "";
            recName = "";
            recContent = "";
            while (isRunning) {
                Log.d("所在位置", String.valueOf(position));
                try {
                    recMsg = in.readUTF();
                    recId1 = recMsg.substring(recMsg.indexOf(strMidStart) + 1, recMsg.indexOf(strMidEnd));
                    recId2 = recMsg.substring(recMsg.indexOf(strMidEnd) + 1, recMsg.lastIndexOf(strEnd));
                    recName = recMsg.substring(recMsg.indexOf(strStart) + 1, recMsg.lastIndexOf(strMidStart));
                    recContent = recMsg.substring(0, recMsg.indexOf(strStart));
                    Log.d("信息组成", recMsg);
                    Log.d("Id", String.valueOf(userInfoManager.getUserId()));
                    if (Integer.valueOf(recId2).equals(userInfoManager.getUserId())) {
                        Log.d("RRR", "收到了一条消息" + "recMsg: " + recMsg);

                        Log.d("friendName", recName);

                        @SuppressLint("SimpleDateFormat")
                        String date = new SimpleDateFormat("hh:mm:ss").format(new Date());

                        ContentValues con = new ContentValues();
                        con.put("user_name", myName);
                        con.put("friend_name", recName);
                        con.put("msg_content", recContent);
                        con.put("msg_date", date);
                        con.put("msg_type", 0);

                        SQLiteDatabase db = openHelper.getReadableDatabase();
                        ContentValues values = new ContentValues();
                        values.put("user_name", myName);
                        values.put("friend_name", recName);
                        values.put("msg_content", recContent);
                        values.put("msg_date", date);
                        values.put("msg_type", 0);
                        db.insert("Chat", null, values);
                        db.close();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!TextUtils.isEmpty(recContent) && Integer.valueOf(recId2).equals(userInfoManager.getUserId()) && position == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Messages messages = new Messages();
                            messages.setFirstMsg(recContent);
                            messages.setFriendName(recName);
                            messages.setUserId(Integer.valueOf(recId1));
                            msgFragment.addTip(messages);
                        }
                    });

                } else if (!TextUtils.isEmpty(recContent) && Integer.valueOf(recId2).equals(userInfoManager.getUserId()) && position == 1) {
                    Log.d("RRR", "inputStream:" + in);
                    Message message = new Message();
                    message.obj = recContent;
                    handler.sendMessage(message);
                }
            }
        }
    }

    public static void setConfigure(String content1, String otherName, int authorId, boolean state) {
        content = content1;
        friendName = otherName;
        friendId = authorId;
        isSend = state;
    }

    public static void setPosition(int position1) {
        position = position1;
    }

    class send implements Runnable {
        @Override
        public void run() {
            while (isRunning) {
                if (!"".equals(content) && isSend) {
                    Log.d("A", "发送了一条消息：" + content);
                    @SuppressLint("SimpleDateFormat")
                    String date = new SimpleDateFormat("hh:mm:ss").format(new Date());

                    //将发送的消息存入本地
                    ContentValues con = new ContentValues();
                    con.put("user_name", myName);
                    con.put("friend_name", friendName);
                    con.put("msg_content", content);
                    con.put("msg_date", date);
                    con.put("msg_type", 1);
                    //保存到本地SQLite
                    Log.d("CtrlS", "将发送给" + friendName + "的消息存入本地");
                    SQLiteDatabase db = openHelper.getReadableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("user_name", myName);
                    values.put("friend_name", friendName);
                    values.put("msg_content", content);
                    values.put("msg_date", date);
                    values.put("msg_type", 1);
                    db.insert("Chat", null, values);
                    db.close();

                    try {
                        String Msg = content + "(" + userInfoManager.getUserName() + "," + userInfoManager.getUserId() + ";" + friendId + ")";
                        Log.d("发出的消息", Msg);
                        out.writeUTF(Msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    isSend = false;
                }
            }
        }
    }
}