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
import android.widget.Toast;

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
import com.example.newslist.utils.UserInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
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
    /**
     * 用 public static 关 ws 似乎有些 Bug
     * 但我还没有遇见
     */
    public static WebSocket mWebSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        defaultPage = getIntent().getIntExtra("fragmentIndex", 0);
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
                    sendNotification(msgTip.getContent(), msgTip.getName(), msgTip.getHeadUrl(), msgTip.getContentUrl());
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
            mWebSocket= client.newWebSocket(request, webSocketListener);
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }
    }

    private void sendNotification(String content, String name, String headUrl, String contentUrl) {
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
                // 设置该通知优先级
                .setSmallIcon(R.drawable.ic_article_24)
                .setContentTitle(name + "回复了你")
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
        notifyManager.notify(1, notification);
        Messages messages = new Messages();
        messages.setFriendName(name + "回复了你");
        messages.setFirstMsg(content);
        messages.setType(2);
        messages.setHeadUrl(headUrl);
        messages.setContentUrl(contentUrl);
        msgFragment.addTip(messages);
    }
}