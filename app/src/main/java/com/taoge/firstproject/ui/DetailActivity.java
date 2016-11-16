package com.taoge.firstproject.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.taoge.firstproject.R;
import com.taoge.firstproject.beans.Web;
import com.taoge.firstproject.database.MySQliteOpenHelper;
import com.taoge.firstproject.uri.Constants;
import com.taoge.firstproject.utils.HttpUtils;
import com.taoge.firstproject.utils.NetWorkUtils;

public class DetailActivity extends AppCompatActivity {

    private WebView mWebView;
    private Button back,collect;
    private String id;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    byte[] data = (byte[]) msg.obj;
                    Web.WebItem item = JSON.parseObject(new String(data),Web.class).getData();
                    mWebView.loadDataWithBaseURL(null,item.getWap_content(),"text/html","utf-8",null);
                    break;
            }
        }
    };
    private String title;
    private String source;
    private String create_time;
    private String nickname;
    private String wap_thumb;
    private String description;
    private Cursor mCursor;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().hide();

        MySQliteOpenHelper helper = new MySQliteOpenHelper(DetailActivity.this);
        db = helper.getReadableDatabase();
        initView();

        //得到传过来的id
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        getIntentValues(intent);
        //Tea.DataBean dataBead = intent.getParcelableExtra("tea");
        //Log.d("flag", "---------------->DetailActivity: " +dataBead.toString());
        //设置webView
        initWebView();

        initData();

        initListener();

        initHistoryData();
    }

    //添加记录到history
    private void initHistoryData() {
        boolean isExist = false;
        mCursor = this.db.query("history", new String[]{"_id", "id"}, null, null, null, null, null);
        while(mCursor.moveToNext()){
            String id1 = mCursor.getString(mCursor.getColumnIndex("id"));
            if(id1.equals(id)){
                isExist = true;
                break;
            }
        }
        if(!isExist) {
            ContentValues values = new ContentValues();
            values.put("title", title);
            values.put("source", source);
            values.put("create_time", create_time);
            values.put("nickname", nickname);
            values.put("wap_thumb", wap_thumb);
            values.put("id", id);
            values.put("description", description);
            db.insert("history", null, values);
        }
    }

    //得到传递过来的值
    private void getIntentValues(Intent intent) {
        title = intent.getStringExtra("title");
        source = intent.getStringExtra("source");
        create_time = intent.getStringExtra("create_time");
        nickname = intent.getStringExtra("nickname");
        wap_thumb = intent.getStringExtra("wap_thumb");
        description = intent.getStringExtra("description");
    }
    //按钮的监听
    private void initListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DetailActivity.this.startActivity(new Intent(DetailActivity.this, MainActivity.class));
                DetailActivity.this.finish();
            }
        });
        //收藏
        collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isExist = false;
                mCursor = db.query("data", new String[]{"_id", "id"}, null, null, null, null, null);
                while(mCursor.moveToNext()){
                    String idDb = mCursor.getString(mCursor.getColumnIndex("id"));
                    if(idDb.equals(id)){
                        isExist = true;
                        Toast.makeText(DetailActivity.this, "已经收藏了", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                if(!isExist) {
                    ContentValues values = new ContentValues();
                    values.put("title", title);
                    values.put("source", source);
                    values.put("create_time", create_time);
                    values.put("nickname", nickname);
                    values.put("wap_thumb", wap_thumb);
                    values.put("id", id);
                    values.put("description", description);
                    db.insert("data", null, values);
                    Toast.makeText(DetailActivity.this, "收藏成功", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    //数据源
    private void initData() {
        if(!NetWorkUtils.isConnected(this)){
            Toast.makeText(this, "没有网络", Toast.LENGTH_SHORT).show();
            return;
        }
        final String path = Constants.CONTENT_URL+id;
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] data = HttpUtils.loadbytes(path);
                Message msg = Message.obtain();
                msg.obj = data;
                msg.what = 1;
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    private void initWebView() {
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setDatabaseEnabled(true);
//        mWebView.getSettings().setLoadWithOverviewMode(true);
//        mWebView.getSettings().setUseWideViewPort(true);
    }

    private void initView() {
        mWebView = (WebView) findViewById(R.id.webView);
        back = (Button) findViewById(R.id.detailBack);
        collect = (Button) findViewById(R.id.collect);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}
