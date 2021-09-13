package com.example.newslist.message;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.newslist.News;
import com.example.newslist.R;
import com.example.newslist.data.Constants;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
public class MsgFragment extends Fragment {
    private static final String TAG = "PW";
    View rootView;
    private List<Messages> messagesData;
    private MessagesAdapter messagesAdapter;
    private RecyclerView rvMessagesList;
    private String[] friendNames = null;
    private String[] firstMsgs = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_msg, container, false);
        }

        rvMessagesList = rootView.findViewById(R.id.rv_msg_list);

        initData();

        messagesAdapter = new MessagesAdapter(getContext(), R.layout.list_msg_item, messagesData);
        messagesAdapter.setOnItemClickListener(new MessagesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), MsgContentActivity.class);
                startActivity(intent);
            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rvMessagesList.setLayoutManager(llm);
        rvMessagesList.setAdapter(messagesAdapter);
        initWebSocket();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (rootView != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }

    private void initData() {
        messagesData = new ArrayList<>();
        int length;
        friendNames = getResources().getStringArray(R.array.titles);
        firstMsgs = getResources().getStringArray(R.array.authors);

        TypedArray images = getResources().obtainTypedArray(R.array.images);

        if (friendNames.length > firstMsgs.length) {
            length = friendNames.length;
        } else {
            length = firstMsgs.length;
        }

        for (int i = 0; i < length; i++) {
            Messages message = new Messages();
            message.setFriendName(friendNames[i]);
            message.setFirstMsg(firstMsgs[i]);
            message.setHead(images.getResourceId(i, 0));

            messagesData.add(message);
        }
    }

    private WebSocket mWebSocket;


    private WebSocketListener webSocketListener = new WebSocketListener() {
        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            super.onOpen(webSocket, response);
            Log.e(TAG, "连接成功");
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            super.onMessage(webSocket, text);
            Log.e(TAG, "连接成功");
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
}