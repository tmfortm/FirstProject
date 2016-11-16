package com.taoge.firstproject.adapters;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.List;

/**
 * Created by my on 2016/11/14.
 */
public class MyHeadPagerAdapter extends PagerAdapter {
    private List<RelativeLayout> list;
    public MyHeadPagerAdapter(List<RelativeLayout> headTwo) {
        list = headTwo;
    }

    @Override
    public int getCount() {
        return list!=null?list.size():0;
//        return Integer.MAX_VALUE;
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
//        return super.instantiateItem(container, position);
        View ret = list.get(position%3);
//        if(ret.getParent()!=null){
//            container.removeView(ret);
//        }
        container.addView(ret);
        return ret;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
        container.removeView(list.get(position%3));
        //Log.d("flag", "---------------->: ssssssssssssssss" );
    }
}
