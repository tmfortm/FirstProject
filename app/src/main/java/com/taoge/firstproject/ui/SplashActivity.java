package com.taoge.firstproject.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.taoge.firstproject.MainActivity;
import com.taoge.firstproject.R;

public class SplashActivity extends AppCompatActivity {

    private ImageView splash;
    private TextView showTime;
    private int second = 3;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if(second>0) {
                        showTime.setText(second + "秒后进入界面");
                        second--;
                        mHandler.sendEmptyMessageDelayed(0,1000);
                    }else{
                        SplashActivity.this.startActivity(new Intent(SplashActivity.this,GuideActivity.class));
                        SplashActivity.this.finish();
                    }
                    break;
                case 1:
                    if(second>0){
                        showTime.setText(second + "秒后进入界面");
                        second--;
                        mHandler.sendEmptyMessageDelayed(1,1000);
                    }else {
                        SplashActivity.this.startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        SplashActivity.this.finish();
                    }
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initView();

        initListener();

    }

    private void initListener() {
        SharedPreferences sp = getSharedPreferences("appConfig",MODE_PRIVATE);
        boolean isFirst = sp.getBoolean("isFirst", true);

        if(isFirst){//第一次进入
            mHandler.sendEmptyMessage(0);
        }else{//不是第一次
            mHandler.sendEmptyMessage(1);
        }
    }

    private void initView() {
        splash = (ImageView) findViewById(R.id.splash);
        showTime = (TextView) findViewById(R.id.showTime);
    }
}
