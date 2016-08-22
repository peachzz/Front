package com.terry.app.front.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.terry.app.front.NeirongActivity;
import com.terry.app.front.R;
import com.terry.app.front.bean.Article;
import com.terry.app.front.listeners.OnItemClickListener;
import com.terry.app.front.adapter.ArticleAdapter;
import com.terry.app.front.util.RecycleViewDivider;
import com.terry.app.front.widgets.AutoLoadRecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Taozi on 2016/6/15.
 */
public class NewsListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AutoLoadRecyclerView.OnLoadListener {

    protected int mCategory = Article.ALL;
    protected ArticleAdapter mAdapter;

    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected AutoLoadRecyclerView mRecyclerView;
    protected List<Article> mDataSet = new ArrayList<Article>();
    private int mPageIndex = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView = (AutoLoadRecyclerView) rootView.findViewById(R.id.articles_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()
                .getApplicationContext()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerView.setOnLoadListener(this);

        mAdapter = new ArticleAdapter(mDataSet);
        mAdapter.setOnItemClickListener(new OnItemClickListener<Article>() {
            @Override
            public void onClick(Article item) {
//                Toast.makeText(getContext(),""+item.getTitle(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), NeirongActivity.class);
                intent.putExtra("post_id", item.getPost_id());
                intent.putExtra("title", item.getTitle());
                startActivity(intent);
            }
        });
        mSwipeRefreshLayout.setRefreshing(true);
        mRecyclerView.setAdapter(mAdapter);
        getArticles(1);
        return rootView;

    }

    private void getArticles(final int page) {
        new AsyncTask<Void, Void, List<Article>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mSwipeRefreshLayout.setRefreshing(true);
            }

            @Override
            protected List<Article> doInBackground(Void... params) {
                return performRequest(page);
            }

            @Override
            protected void onPostExecute(List<Article> articles) {
//                List<Article> datas = getNewList(articles);
                articles.removeAll(mDataSet);
                mDataSet.addAll(articles);
                mAdapter.notifyDataSetChanged();
                DataSupport.saveAll(mDataSet);
                mSwipeRefreshLayout.setRefreshing(false);

                if (articles.size()>0) {
                    mPageIndex++;
                }
            }
        }.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        mDataSet.clear();
        mDataSet.addAll(DataSupport.findAll(Article.class));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        getArticles(1);
    }

    @Override
    public void onLoad() {
//        mSwipeRefreshLayout.setRefreshing(true);
        getArticles(mPageIndex);
    }

    private List<Article> performRequest(int page) {
        HttpURLConnection urlConnection = null;
        try {
            String getUrl =
                    "http://www.devtf.cn/api/v1/?type=articles&page=" + mPageIndex
                            + "&count=20&category=1";
            urlConnection = (HttpURLConnection) new URL(getUrl)
                    .openConnection();
            urlConnection.connect();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                sBuilder.append(line).append("\n");
            }
            String result = sBuilder.toString();
            return parse(new JSONArray(result));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return new ArrayList<Article>();
    }

    private List<Article> parse(JSONArray jsonArray) {
        List<Article> articleLists = new LinkedList<Article>();
        int count = jsonArray.length();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (int i = 0; i < count; i++) {
            JSONObject itemObject = jsonArray.optJSONObject(i);
            Article articleItem = new Article();
            articleItem.setTitle(itemObject.optString("title"));
            articleItem.setAuthor(itemObject.optString("author"));
            articleItem.setPost_id(itemObject.optString("post_id"));
            String category = itemObject.optString("category");
            articleItem.setCategory(TextUtils.isEmpty(category) ? 0 : Integer.valueOf(category));
            articleItem.setPublishTime(formatDate(dateformat, itemObject.optString("date")));
            Log.d("", "title : " + articleItem.getTitle() + ", id = " + articleItem.getPost_id());
            articleLists.add(articleItem);
        }

        return articleLists;
    }

    private String formatDate(SimpleDateFormat dateFormat, String dateString) {
        try {
            Date date = dateFormat.parse(dateString);
            return dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public List<Article> getNewList(List<Article> li) {
        List<Article> list = new ArrayList<Article>();
        for (int i = 0; i < li.size(); i++) {
            Article str = li.get(i); //获取传入集合对象的每一个元素
            if (!list.contains(str)) { //查看新集合中是否有指定的元素，如果没有则加入
                list.add(str);
            }
        }
        return list; //返回集合
    }

}
