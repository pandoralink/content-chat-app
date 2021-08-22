package com.example.newslist.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.newslist.News;
import com.example.newslist.R;

import java.util.List;

/**
 * @author 庞旺
 */
public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private List<News> mNewsData;
    private Context mContext;
    private int resourceId;

    public MessagesAdapter(Context context, int resourceId, List<News> data) {
        this.mContext = context;
        this.mNewsData = data;
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
        News news = mNewsData.get(position);
        holder.tvTitle.setText(news.getTitle());
        holder.tvAuthor.setText(news.getAuthor());

        if (news.getImageId() != -1) {
            holder.ivImage.setImageResource(news.getImageId());
        }
    }

    @Override
    public int getItemCount() {
        return mNewsData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvAuthor;
        ImageView ivImage;

        public ViewHolder(View view) {
            super(view);

            tvTitle = view.findViewById(R.id.tv_title);
            tvAuthor = view.findViewById(R.id.tv_subtitle);
            ivImage = view.findViewById(R.id.iv_image);
        }
    }

    public void add(News data) {
        mNewsData.add(0, data);
        notifyItemInserted(0);
        //刷新下标，不然下标就不连续
        notifyItemRangeChanged(0, mNewsData.size());
    }

}

