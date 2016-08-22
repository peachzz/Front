package com.terry.app.front.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.terry.app.front.R;
import com.terry.app.front.bean.Article;
import com.terry.app.front.listeners.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 文章列表
 * Created by Taozi on 2016/6/15.
 */
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private List<Article> mDatas = new ArrayList<>();
    OnItemClickListener<Article> mOnItemClickListener;

    public ArticleAdapter(List<Article> datas) {
        this.mDatas.clear();
        this.mDatas = datas;
    }

    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ArticleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_article_item, parent,false));
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public void onBindViewHolder(ArticleViewHolder holder, int position) {
        final Article item = mDatas.get(position);
        holder.mArticleTitleTv.setText(item.title);
        holder.mArticleTimeTv.setText(item.publishTime);
        holder.mArticleAuthorTv.setText(item.author);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onClick(item);
                }
            }
        });
    }

    static class ArticleViewHolder extends RecyclerView.ViewHolder {

        private TextView mArticleTitleTv;
        private TextView mArticleAuthorTv;
        private TextView mArticleTimeTv;


        public ArticleViewHolder(View itemView) {
            super(itemView);

            mArticleTitleTv = (TextView) itemView.findViewById(R.id.article_title_tv);
            mArticleAuthorTv = (TextView) itemView.findViewById(R.id.article_author_tv);
            mArticleTimeTv = (TextView) itemView.findViewById(R.id.article_time_tv);
        }
    }

    public void setOnItemClickListener(OnItemClickListener<Article> listener) {
        this.mOnItemClickListener = listener;
    }
}
