package com.taoge.firstproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.taoge.firstproject.R;
import com.taoge.firstproject.adapters.TeaAdapter;
import com.taoge.firstproject.beans.Tea;
import com.taoge.firstproject.uri.Constants;
import com.taoge.firstproject.utils.HttpUtils;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private String context;
    private TextView empty;
    private ListView mListView;
    private ImageView searchBack;
    private List<Tea.DataBean> data = new ArrayList<>();
    private BaseAdapter adapter;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 5:
                    byte[] bytes = (byte[]) msg.obj;
                    Tea tea = JSON.parseObject(new String(bytes),Tea.class);
                    data.addAll(tea.getData());
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().hide();
        Intent intent = getIntent();
        context = intent.getStringExtra("context");

        initView();

        initData();

        initListView();

        initListViewListener();
    }

    //点击监听事件
    private void initListViewListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchActivity.this,DetailActivity.class);
                Tea.DataBean dataBean = data.get(position);
                //intent.putExtra("tea",dataBean);
                intent.putExtra("title",dataBean.getTitle());
                intent.putExtra("source",dataBean.getSource());
                intent.putExtra("create_time",dataBean.getCreate_time());
                intent.putExtra("nickname",dataBean.getNickname());
                intent.putExtra("wap_thumb",dataBean.getWap_thumb());
                intent.putExtra("description",dataBean.getDescription());
                String teaId = data.get(position).getId();
                intent.putExtra("id",teaId);
                SearchActivity.this.startActivity(intent);
            }
        });
    }

    private void initListView() {
        adapter = new TeaAdapter(this,data);
        mListView.setAdapter(adapter);
        mListView.setEmptyView(empty);
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.searchListView);
        empty = (TextView) findViewById(R.id.searchEmpty);
        searchBack = (ImageView) findViewById(R.id.searchBack);

        searchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.this.finish();
            }
        });
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] bytes = HttpUtils.loadbytes(Constants.SEARCH_URL+context);
                Message msg = Message.obtain();
                msg.what = 5;
                msg.obj = bytes;
                mHandler.sendMessage(msg);
            }
        }).start();
    }
}
