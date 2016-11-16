package com.taoge.firstproject.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.softpo.viewpagertransformer.ForegroundToBackgroundTransformer;
import com.taoge.firstproject.R;
import com.taoge.firstproject.adapters.MainFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {


    private TabLayout tabs;
    private ViewPager viewPager;
    private ImageView more,back;
    private TextView collection,history;
    private EditText input;
    private Button goSearch;
    private List<Fragment> listPager = new ArrayList<>();
    private DrawerLayout drawer;
    private String[] titles = new String[]{"头条","百科","咨询","经营","数据"};
    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.fragment_main, container, false);


        initView(ret);

        initDrawer();
        
        initTabLayout();
        
        initViewPager();

        return ret;
    }

    private void initDrawer() {

//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(),drawer,0,0);
//        drawer.addDrawerListener(toggle);
//        //调用toggle  同步一下状态
//        toggle.syncState();
    }

    private void initViewPager() {
        for (int i = 0; i < 5; i++) {
            Fragment fragment = new DisplayFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("index",i);
            fragment.setArguments(bundle);
            listPager.add(fragment);
        }
        FragmentPagerAdapter adapter = new MainFragmentPagerAdapter(getFragmentManager(),listPager);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));

        viewPager.setPageTransformer(false,new ForegroundToBackgroundTransformer());
    }




    private void initTabLayout() {
        for(int i=0;i<5;i++){
            TabLayout.Tab tab = tabs.newTab();
            tab.setText(titles[i]);
            //tab.setTag(i);
            tabs.addTab(tab);
        }
        //tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }

    //按钮点击事件
    private void initView(View ret) {
        tabs = (TabLayout) ret.findViewById(R.id.tabs);
        viewPager = (ViewPager) ret.findViewById(R.id.viewPager);
        more = (ImageView) ret.findViewById(R.id.more);
        drawer = (DrawerLayout) ret.findViewById(R.id.drawer);
        back = (ImageView) ret.findViewById(R.id.back);
        history = (TextView) ret.findViewById(R.id.history);
        collection = (TextView) ret.findViewById(R.id.collection);
        input = (EditText) ret.findViewById(R.id.input);
        goSearch = (Button) ret.findViewById(R.id.goSearch);

        //抽屉的点击事件
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!drawer.isDrawerOpen(GravityCompat.END)){
                    drawer.openDrawer(GravityCompat.END);
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawer.isDrawerOpen(GravityCompat.END)){
                    drawer.closeDrawer(GravityCompat.END);
                }
            }
        });

        //我的收藏的点击事件
        collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(),CollectActivity.class));
            }
        });
        //历史记录的点击事件
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(),HistoryActivity.class));
            }
        });

        goSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                String context = input.getText().toString().trim();
                intent.putExtra("context",context);
                getContext().startActivity(intent);
            }
        });
    }

}
