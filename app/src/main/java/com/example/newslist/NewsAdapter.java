package com.example.newslist;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author 庞旺
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private List<Articles> mArticlesData;
    private Context mContext;
    private int resourceId;
    private OnItemClickListener mOnItemClickListener;
    private String TAG = "PW";

    public interface OnItemClickListener {
        /**
         * RecyclerView Item 的长按点击事件
         *
         * @param view
         * @param position
         */
        void onItemLongClick(View view, int position);

        /**
         * NewsFragment item 的点击事件
         *
         * @param view
         * @param position
         */
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public NewsAdapter(Context context, int resourceId, List<Articles> data) {
        this.mContext = context;
        this.mArticlesData = data;
        this.resourceId = resourceId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(resourceId, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        /**
         * onBindViewHolder 作
         * 初始化，防止出现复用 Bug
         */
        Articles articles = mArticlesData.get(position);
        holder.tvTitle.setText(articles.getTitle());
        holder.ivImage.setVisibility(View.VISIBLE);

        if (articles.getUser_name() != null) {
            holder.tvAuthor.setText(articles.getUser_name());
        }
        if (articles.getArticle_cover_url().isEmpty()) {
            holder.ivImage.setVisibility(View.GONE);
        } else {
            Glide.with(mContext).load(articles.getArticle_cover_url()).into(holder.ivImage);
        }

        if (mOnItemClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onItemLongClick(holder.itemView, position);
                    return false;
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mArticlesData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvAuthor;
        ImageView ivImage;

        public ViewHolder(View view) {
            super(view);

            tvTitle = view.findViewById(R.id.tv_title);
            tvAuthor = view.findViewById(R.id.tv_subtitle);
            ivImage = view.findViewById(R.id.iv_author_head);
        }
    }

    public void add(Articles data) {
        mArticlesData.add(0, data);
        notifyItemInserted(0);
        notifyItemRangeChanged(0, mArticlesData.size());
    }

}
