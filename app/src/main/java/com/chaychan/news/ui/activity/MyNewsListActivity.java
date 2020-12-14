package com.chaychan.news.ui.activity;

import com.chaychan.news.R;
import com.chaychan.news.database.MySQLiteOpenHelper;
import com.chaychan.news.model.entity.News;
import com.chaychan.news.model.entity.NewsRecord;
import com.chaychan.news.presenter.NewsListPresenter;
import com.chaychan.news.presenter.view.lNewsListView;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MyNewsListActivity extends AppCompatActivity implements lNewsListView, View.OnClickListener {

    TextView newsTextView;
    Button loadNewsButton;

    private MySQLiteOpenHelper mySQLiteOpenHelper;

    //新闻记录
    private NewsRecord mNewsRecord;
    protected NewsListPresenter mPresenter;

    protected NewsListPresenter createPresenter() {
        return new NewsListPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_news_list);

        mPresenter = createPresenter();
        //创建一个没有数据的对象
        mNewsRecord = new NewsRecord();

        //初始化数据库
        mySQLiteOpenHelper = new MySQLiteOpenHelper(this, "News.db", null, 1);

        newsTextView = findViewById(R.id.news_text_view);
        loadNewsButton = findViewById(R.id.load_news_button);
        loadNewsButton.setOnClickListener(this);


        loadLatestNews();
    }



    @Override
    public void onGetNewsListSuccess(List<News> newList, String tipInfo) {
        StringBuffer newsTitleList = new StringBuffer("");
        for (News news :
                newList) {
            newsTitleList.append(news.title + "\n");
        }

        newsTextView.setText(newsTitleList);

        SQLiteDatabase sqLiteDatabase = mySQLiteOpenHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("title", newsTitleList.toString());
        sqLiteDatabase.insert("News", null, contentValues);
        sqLiteDatabase.close();
    }

    //当用户重新进入该Activity，从sqlite中加载最近存储的一组新闻
    private void loadLatestNews() {
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query("News", null, null, null, null, null, null);
        cursor.moveToLast();
        if (cursor.getCount() > 0) {
            String latestNews = cursor.getString(cursor.getColumnIndex("title"));
            newsTextView.setText(latestNews);
        }
    }

    @Override
    public void onError() {

    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.load_news_button) {
            //拉取网络数据，读取 推荐 标签 下的一组新闻
            mPresenter.getNewsList("");
        }
    }
}