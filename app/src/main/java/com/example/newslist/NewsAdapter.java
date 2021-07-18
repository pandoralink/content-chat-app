package com.example.newslist;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private List<News> mNewsData;
    private Context mContext;
    private int resourceId;
    long startTime = 0;
    long endTime = 0;

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
        startTime = System.currentTimeMillis(); // 获取开始时间
        View view = LayoutInflater.from(mContext).inflate(resourceId, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        News news = mNewsData.get(position);
        holder.tvTitle.setText(news.getTitle());
        holder.tvAuthor.setText(news.getAuthor());

        if(news.getImageId() != -1) {
            holder.ivImage.setImageResource(news.getImageId());
        }
        endTime = System.currentTimeMillis(); // 获取结束时间
        Log.e("PW","位置在" + position +"代码运行时间： " + (endTime - startTime) + "ms");
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
