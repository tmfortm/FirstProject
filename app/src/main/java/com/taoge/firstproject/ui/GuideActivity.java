package com.taoge.firstproject.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.taoge.firstproject.MainActivity;
import com.taoge.firstproject.R;
import com.taoge.firstproject.adapters.MyPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private ViewPager guideViewPager;
    private List<ImageView> data = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                ,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initView();

        initData();
    }

    private void initData() {
        for (int i = 0; i < 3; i++) {
            ImageView imageView = new ImageView(this);
            switch (i){
                case 0:
                    imageView.setImageResource(R.mipmap.slide1);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                case 1:
                    imageView.setImageResource(R.mipmap.slide2);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                case 2:
                    imageView.setImageResource(R.mipmap.slide3);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setOnClickListener(this);
                    break;
            }
            data.add(imageView);
        }
        PagerAdapter adapter = new MyPagerAdapter(data);
        guideViewPager.setAdapter(adapter);
    }

    private void initView() {
        guideViewPager = (ViewPager) findViewById(R.id.guideViewPager);
        guideViewPager.addOnPageChangeListener(this);
    }

    private int index;
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        index = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        if(index == 2){
            SharedPreferences sp = getSharedPreferences("appConfig",MODE_PRIVATE);
            SharedPreferences.Editor edit = sp.edit();
            edit.putBoolean("isFirst",false);
            edit.commit();
            this.startActivity(new Intent(this, MainActivity.class));
            this.finish();
        }
    }
}
