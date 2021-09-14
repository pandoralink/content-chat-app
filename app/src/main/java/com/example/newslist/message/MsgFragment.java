package com.example.newslist.message;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
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
    private static final String CHANNEL_ID = "comment channel";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_msg, container, false);
        }
        Log.i(TAG, "onCreate: " + "是否重新创建");

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
}