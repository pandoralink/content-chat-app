package com.example.newslist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * @author 庞旺
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private List<News> mNewsData;
    private Context mContext;
    private int resourceId;
    private OnItemClickListener mOnItemClickListener;

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

    public NewsAdapter(Context context, int resourceId, List<News> data) {
//        为什么要删除super()?
//        super(context, resourceId, data);
        this.mContext = context;
        this.mNewsData = data;
        this.resourceId = resourceId;
    }

    //    高中生的做法 Cursor 绑定
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(resourceId, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        News news = mNewsData.get(position);
        holder.tvTitle.setText(news.getTitle());
        holder.tvAuthor.setText(news.getAuthor());

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
