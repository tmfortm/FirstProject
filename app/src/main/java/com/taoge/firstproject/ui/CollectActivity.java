package com.taoge.firstproject.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.taoge.firstproject.R;
import com.taoge.firstproject.adapters.TeaAdapter;
import com.taoge.firstproject.beans.Tea;
import com.taoge.firstproject.database.MySQliteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class CollectActivity extends AppCompatActivity {

    private ListView mListView;
    private TextView empty;
    private BaseAdapter adapter;
    private ImageView collectBack;
    private List<Tea.DataBean> data = new ArrayList<>();
    private SQLiteDatabase db;
    private Cursor cursor;
    private MySQliteOpenHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        getSupportActionBar().hide();

        initView();
        
        initData();
        
        initListView();

        initListener();
    }


    private void initListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CollectActivity.this,DetailActivity.class);
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
                CollectActivity.this.startActivity(intent);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CollectActivity.this);
                builder.setIcon(R.mipmap.icon_dialog);
                builder.setTitle("提示");
                builder.setMessage("您确定要删除吗?");
                builder.setNegativeButton("取消",null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TranslateAnimation translate = new TranslateAnimation(
                                Animation.RELATIVE_TO_SELF,0
                                ,Animation.RELATIVE_TO_SELF,-1
                                ,Animation.RELATIVE_TO_SELF,0
                                ,Animation.RELATIVE_TO_SELF,0);
                        translate.setDuration(2000);
                        view.startAnimation(translate);
                        translate.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                data.remove(position);
                                adapter.notifyDataSetChanged();
                                TranslateAnimation trans = new TranslateAnimation(
                                        Animation.RELATIVE_TO_SELF,0
                                        ,Animation.RELATIVE_TO_SELF,0
                                        ,Animation.RELATIVE_TO_SELF,0
                                        ,Animation.RELATIVE_TO_SELF,-1);
                                trans.setDuration(1000);
                                int count = mListView.getChildCount();
                                int current = view.getTop();
                                for(int i=0;i<count;i++){
                                    View itemView = mListView.getChildAt(i);
                                    if(itemView.getTop()>=current) {
                                        itemView.setAnimation(trans);
                                    }
                                }
                                String id1 = data.get(position).getId();
                                db.delete("data","id='"+id1+"'",null);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                });
                builder.create().show();
            }
        });
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.collectListView);
        empty = (TextView) findViewById(R.id.empty);
        collectBack = (ImageView) findViewById(R.id.collectBack);

        collectBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CollectActivity.this.finish();
            }
        });
    }

    private void initListView() {
        adapter = new TeaAdapter(this,data);
        mListView.setAdapter(adapter);
        mListView.setEmptyView(empty);
    }

    //数据库的数据源
    private void initData() {
        //String path = this.getDatabasePath("myDate.db").getAbsolutePath();
        helper = new MySQliteOpenHelper(this);
        //db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
        db = helper.getReadableDatabase();
        cursor = db.query("data", null
                , null, null, null, null, null);

        while (cursor.moveToNext()) {
            Tea.DataBean tdb = new Tea.DataBean();
            String title = cursor.getString(cursor.getColumnIndex("title"));
            tdb.setTitle(title);
            String source = cursor.getString(cursor.getColumnIndex("source"));
            tdb.setSource(source);
            String nickname = cursor.getString(cursor.getColumnIndex("nickname"));
            tdb.setNickname(nickname);
            String create_time = cursor.getString(cursor.getColumnIndex("create_time"));
            tdb.setCreate_time(create_time);
            String wap_thumb = cursor.getString(cursor.getColumnIndex("wap_thumb"));
            tdb.setWap_thumb(wap_thumb);
            String id = cursor.getString(cursor.getColumnIndex("id"));
            tdb.setId(id);
            String description = cursor.getString(cursor.getColumnIndex("description"));
            tdb.setDescription(description);
            data.add(tdb);
            //adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
        cursor.close();
    }
}
