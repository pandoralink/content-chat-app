package com.example.newslist.message;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newslist.R;
import com.example.newslist.data.Constants;
import com.example.newslist.news.ArticleContentActivity;
import com.example.newslist.popup.DeleteMsgDialogFragment;

import java.util.ArrayList;
import java.util.List;


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
    private int[] userIds = null;
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
                if (messagesData.get(position).getType() == 1) {
                    Intent intent = new Intent(getActivity(), MsgContentActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), ArticleContentActivity.class);
                    intent.putExtra(Constants.ARTICLE_URL_KEY, messagesData.get(position).getContentUrl());
                    intent.putExtra("articleId", messagesData.get(position).getAid());
                    intent.putExtra("type", 2);
                    startActivity(intent);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                confirmDeleteMsg(position);
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
        friendNames = getResources().getStringArray(R.array.userName);
        firstMsgs = getResources().getStringArray(R.array.firstMsg);
        userIds = getResources().getIntArray(R.array.userId);

        if (friendNames.length > firstMsgs.length) {
            length = friendNames.length;
        } else {
            length = firstMsgs.length;
        }

        for (int i = 0; i < length; i++) {
            Messages message = new Messages();
            message.setFriendName(friendNames[i]);
            message.setFirstMsg(firstMsgs[i]);
            message.setUserId(userIds[i]);
            message.setHeadUrl("http://116.63.152.202:5002/userHead/default_head.png");
            messagesData.add(message);
        }
    }

    public void addTip(Messages messages) {
        messagesData.add(0, messages);
        messagesAdapter.notifyDataSetChanged();
    }

    public void confirmDeleteMsg(int index) {
        DeleteMsgDialogFragment newFragment = new DeleteMsgDialogFragment(index);
        newFragment.setOnNoticeDialogListener(new DeleteMsgDialogFragment.NoticeDialogListener() {
            @Override
            public void onDialogPositiveClick(DialogFragment dialog, int index) {
                messagesData.remove(index);
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {

            }
        });
        newFragment.show(getFragmentManager(), "deleteMsg");
    }
}