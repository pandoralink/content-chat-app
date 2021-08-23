package com.example.newslist.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.newslist.R;

import java.util.List;

/**
 * @author 庞旺
 */
public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private List<Messages> messagesData;
    private Context mContext;
    private int resourceId;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        /**
         * RecyclerView Item 的点击事件
         *
         * @param view
         * @param position
         */
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public MessagesAdapter(Context context, int resourceId, List<Messages> data) {
        this.mContext = context;
        this.messagesData = data;
        this.resourceId = resourceId;
    }

    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(resourceId, parent, false);
        MessagesAdapter.ViewHolder holder = new MessagesAdapter.ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(MessagesAdapter.ViewHolder holder, int position) {
        Messages messages = messagesData.get(position);
        holder.friendName.setText(messages.getFriendName());
        holder.firstMsg.setText(messages.getFirstMsg());

        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }

        if (messages.getHead() != -1) {
            holder.head.setImageResource(messages.getHead());
        }
    }

    @Override
    public int getItemCount() {
        return messagesData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView friendName;
        TextView firstMsg;
        ImageView head;

        public ViewHolder(View view) {
            super(view);

            friendName = view.findViewById(R.id.msg_title);
            firstMsg = view.findViewById(R.id.msg_content);
            head = view.findViewById(R.id.iv_image);
        }
    }

    public void add(Messages data) {
        messagesData.add(0, data);
        notifyItemInserted(0);
        notifyItemRangeChanged(0, messagesData.size());
    }
}

