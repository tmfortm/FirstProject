package com.taoge.firstproject.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by my on 2016/11/11.
 */
public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> listPager;

    public MainFragmentPagerAdapter(FragmentManager fragmentManager, List<Fragment> listPager) {
        super(fragmentManager);
        this.listPager = listPager;
    }

    @Override
    public Fragment getItem(int position) {
        return listPager.get(position);
    }

    @Override
    public int getCount() {
        return listPager!=null?listPager.size():0;
    }
}
