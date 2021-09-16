package com.example.newslist;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.newslist.createNew.CreateNewFragment;
import com.example.newslist.data.Constants;
import com.example.newslist.data.MsgTip;
import com.example.newslist.message.Messages;
import com.example.newslist.message.MsgFragment;
import com.example.newslist.news.ArticleFragment;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
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
    private static final String CHANNEL_ID = "Music channel";
    private static int defaultPage = 0;
    Gson gson = new Gson();
    MsgFragment msgFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        defaultPage = getIntent().getIntExtra("frgamentIndex", 0);
        initViews();
        initWebSocket();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
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
        mViewPager.setOffscreenPageLimit(2);

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

    private WebSocket mWebSocket;


    private WebSocketListener webSocketListener = new WebSocketListener() {
        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            super.onOpen(webSocket, response);
            Log.e(TAG, "连接成功");
            // 发送 uid，暂时用 1005 代替
            webSocket.send(Integer.toString(1005));
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            super.onMessage(webSocket, text);
            Log.e(TAG, "连接成功" + text);
            Messages message = new Messages();
            message.setFriendName("defaultName");
            message.setFirstMsg(text);
            MsgTip msgTip = gson.fromJson(text, MsgTip.class);
            sendNotification(msgTip.getContent(), msgTip.getName());
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
            Log.e(TAG, "连接失败");
        }
    };

    private void initWebSocket() {
        String wsUrl = Constants.LOCAL_WEBSOCKET_URL;
        //构造request对象
        Request request = new Request.Builder()
                .url(wsUrl)
                .build();
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .pingInterval(10, TimeUnit.SECONDS)
                    .build();
            client.newWebSocket(request, webSocketListener);
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }
    }

    private void sendNotification(String content, String name) {
        Log.i(TAG, "sendNotification: " + content);
        Intent intent = new Intent();
        intent.putExtra("frgamentIndex", 1);
        intent.setClass(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        this,
                        CHANNEL_ID);
        Notification notification = builder
                .setAutoCancel(true)
                // 设置该通知优先级
                .setSmallIcon(R.drawable.ic_article_24)
                .setContentTitle(name + "回复了你")
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setPriority(2)
                .build();
//        notification.flags = Notification.FLAG_ONGOING_EVENT;
        NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        startForeground(ONGOING_NOTIFICATION_ID, notification);
        String CHANNEL_NAME = "your_custom_name";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notifyManager.createNotificationChannel(notificationChannel);
        }
        notifyManager.notify(1, notification);//id要保证唯一
        Messages messages = new Messages();
        messages.setFriendName(name + "回复了你");
        messages.setFirstMsg(content);
        messages.setType(2);
        msgFragment.addTip(messages);
    }

    private void test() {
        Messages messages = new Messages();
        messages.setFriendName("name" + "回复了你");
        messages.setFirstMsg("content");
        messages.setType(2);
        msgFragment.addTip(messages);
    }
}