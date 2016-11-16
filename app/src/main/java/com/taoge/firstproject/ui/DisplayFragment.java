package com.taoge.firstproject.ui;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.LoadingLayoutProxy;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.taoge.firstproject.R;
import com.taoge.firstproject.adapters.MyHeadPagerAdapter;
import com.taoge.firstproject.adapters.TeaAdapter;
import com.taoge.firstproject.beans.Head;
import com.taoge.firstproject.beans.Tea;
import com.taoge.firstproject.callback.ListImageCallback;
import com.taoge.firstproject.nets.ListImageAsyncTask;
import com.taoge.firstproject.uri.Constants;
import com.taoge.firstproject.utils.HttpUtils;
import com.taoge.firstproject.utils.NetWorkUtils;
import com.taoge.firstproject.utils.SdCardUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.taoge.firstproject.utils.SdCardUtils.getDataFromCache;

/**
 * A simple {@link Fragment} subclass.
 */
public class DisplayFragment extends Fragment implements View.OnClickListener {


    private SwipeRefreshLayout refresh;
    private ImageView backTop;
    //private ListView listView;
    private PullToRefreshListView listView;
    private List<Tea.DataBean> data = new ArrayList<>();
    //头布局
    private List<Head.DataBean> datas = new ArrayList<>();
    private int currentPosition = 0;

    private BaseAdapter adapter;
    private int index = 0;
    private int page = 1;
    private String path;
    private ViewPager headViewPager;
    private int current;

    //Handler传递消息
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    byte[] bytes = (byte[]) msg.obj;
                    Tea tea = JSON.parseObject(new String(bytes), Tea.class);
                    data.addAll(tea.getData());
                    if (page == 1) {
                        initListView();
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case 2:
                    byte[] bytess = (byte[]) msg.obj;
                    Head head = JSON.parseObject(new String(bytess), Head.class);
                    datas.addAll(head.getData());
                    initData();
                    break;
            }
        }
    };
    private Handler mHandler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 3:
                    if (this.hasMessages(3)) {
                        this.removeMessages(3);
                    }
                    currentPosition++;
                    if (currentPosition > 2) {
                        currentPosition = 0;
                        headViewPager.setCurrentItem(currentPosition, false);
                        smallCircle[currentPosition].setEnabled(false);
                        smallCircle[current].setEnabled(true);
                        current = currentPosition;
                    } else {
                        headViewPager.setCurrentItem(currentPosition);
                        smallCircle[currentPosition].setEnabled(false);
                        smallCircle[current].setEnabled(true);
                        current = currentPosition;
                    }
//                    headViewPager.setCurrentItem(currentPosition);
//                    smallCircle[currentPosition%3].setEnabled(false);
//                    smallCircle[current].setEnabled(true);
//                    current = currentPosition%3;
                    this.sendEmptyMessageDelayed(3, 3000);
                    break;
            }
        }
    };
    private ListView mLv;
    private LoadingLayoutProxy mLoadingLayoutProxy;

    public DisplayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.fragment_display, container, false);
        adapter = new TeaAdapter(getContext(), data);
        initView(ret);
        Bundle arguments = getArguments();
        if (arguments != null) {
            index = arguments.getInt("index");
        }
        //Log.d("flag", "---------------->: " +index);
        //下拉刷新
        //initSwipeRefresh();
        //初始化网址
        initPath();

        if (index == 0) {
            initViewPagerData();
        } else {
            initData();
        }

        return ret;
    }

    private ImageView[] smallCircle = new ImageView[3];

    private void initSmallCircle(View header) {
        LinearLayout ll = (LinearLayout) header.findViewById(R.id.smallCircle);
        for (int i = 0; i < 3; i++) {
            ImageView child = (ImageView) ll.getChildAt(i);
            child.setTag(i);
            child.setOnClickListener(this);
            child.setEnabled(true);
            smallCircle[i] = child;
        }
        smallCircle[0].setEnabled(false);
        current = 0;
    }

    //监听listView滑动事件
    private boolean isBottom = false;

    private void initListViewScroll() {
        mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (isBottom && scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    page++;
                    //Log.d("flag", "---------------->: page" +page);
                    initData();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                Log.d("flag", "---------------->: firstVisibleItem" +firstVisibleItem);
//                Log.d("flag", "---------------->: visibleItemCount" +visibleItemCount);
//                Log.d("flag", "---------------->: totalItemCount" +totalItemCount);
                if (totalItemCount != 0) {
                    if (firstVisibleItem + visibleItemCount == totalItemCount) {
                        // Log.d("flag", "---------------->onScroll: " +page);
                        isBottom = true;
                    } else {
                        isBottom = false;
                    }
                }
            }
        });
    }

    //点击监听
    private void initListViewOnClick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), DetailActivity.class);
                Tea.DataBean dataBean = data.get(position - 2);
                //intent.putExtra("tea",dataBean);
                intent.putExtra("title", dataBean.getTitle());
                intent.putExtra("source", dataBean.getSource());
                intent.putExtra("create_time", dataBean.getCreate_time());
                intent.putExtra("nickname", dataBean.getNickname());
                intent.putExtra("wap_thumb", dataBean.getWap_thumb());
                intent.putExtra("description", dataBean.getDescription());
                String teaId = data.get(position - 2).getId();
                intent.putExtra("id", teaId);
                getContext().startActivity(intent);
            }
        });
    }

    //长按监听
    private void initListViewListener() {
        mLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setIcon(R.mipmap.icon_dialog);
                builder.setTitle("提示");
                builder.setMessage("您确定要删除吗?");
                builder.setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final TranslateAnimation translate = new TranslateAnimation(
                                Animation.RELATIVE_TO_SELF, 0
                                , Animation.RELATIVE_TO_SELF, -1
                                , Animation.RELATIVE_TO_SELF, 0
                                , Animation.RELATIVE_TO_SELF, 0);
                        translate.setDuration(2000);
                        view.startAnimation(translate);
                        translate.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                //listView.removeView(view);
                                //data.clear();
                                if(index==0) {
                                    data.remove(position - 2);
                                }else{
                                    data.remove(position-1);
                                }
                                TranslateAnimation trans = new TranslateAnimation(
                                        Animation.RELATIVE_TO_SELF,0
                                       ,Animation.RELATIVE_TO_SELF,0
                                       ,Animation.RELATIVE_TO_SELF,1
                                       ,Animation.RELATIVE_TO_SELF,0);
                                trans.setDuration(1000);
                                int count = mLv.getChildCount();
                                int current = view.getTop();
                                for(int i=0;i<count;i++){
                                    View itemView = mLv.getChildAt(i);
                                    if(itemView.getTop()>=current) {
                                        itemView.setAnimation(trans);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                });
                builder.create().show();
                return true;
            }
        });
    }

    //
    private void initListView() {
        mLv = listView.getRefreshableView();
        if (index == 0) {
            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
            View header = LayoutInflater.from(getContext()).inflate(R.layout.header, listView, false);
            headViewPager = (ViewPager) header.findViewById(R.id.headViewPager);

            initHeadViewPagerTwo();
            //初始化小圆点
            initSmallCircle(header);
            header.setLayoutParams(layoutParams);
            mLv.addHeaderView(header);
        }

        //View footer = LayoutInflater.from(getContext()).inflate(R.layout.footer, listView, false);
      //  mLv.addFooterView(footer);
        adapter = new TeaAdapter(getContext(), data);
        listView.setAdapter(adapter);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        initLoadingLayout();
        //滑动
        //initListViewScroll();
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                return ;
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                Log.d("flag", "---------------->: " +page);
                initData();
            }
        });
        //listView条目长安点击事件
        initListViewListener();
        //listView条目点击事件
        initListViewOnClick();
    }

    private void initLoadingLayout() {

        mLoadingLayoutProxy = (LoadingLayoutProxy) listView.getLoadingLayoutProxy();

        mLoadingLayoutProxy.setPullLabel("使劲拉......");

        mLoadingLayoutProxy.setReleaseLabel("快了......");

        mLoadingLayoutProxy.setRefreshingLabel("拼命加载中");
        //显示上次刷新的时间
        String time = DateUtils.formatDateTime(getContext(), System.currentTimeMillis(),
                DateUtils.FORMAT_ABBREV_TIME | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE);
        mLoadingLayoutProxy.setLastUpdatedLabel(time);

        //设置加载动画 , 有时候动画不起作用 ，模式为both 不起作用  建议在布局中使用自定义属性来设置
        // mLoadingLayoutProxy.setLoadingDrawable();

    }



    private PagerAdapter adapterHead;
    //第二种头布局
    private List<RelativeLayout> headTwo = new ArrayList<>();

    private void initHeadViewPagerTwo() {
        for (int i = 0; i < 3; i++) {
            RelativeLayout rl = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.head_content, headViewPager, false);
            initHeadView(rl, i);
            headTwo.add(rl);
        }
        adapterHead = new MyHeadPagerAdapter(headTwo);
        headViewPager.setAdapter(adapterHead);
        headViewPager.setCurrentItem(0);
        mHandler2.sendEmptyMessageDelayed(3, 3000);
    }

    private void initHeadView(RelativeLayout rl, final int index) {
        final ImageView headImage = (ImageView) rl.findViewById(R.id.headImage);
        TextView headName = (TextView) rl.findViewById(R.id.headName);
        final String path = datas.get(index).getImage();
        String name = datas.get(index).getName();
        Bitmap bitmap = getCacheBitmap(path);
        if (bitmap != null) {
            headImage.setImageBitmap(bitmap);
        }else {
            new ListImageAsyncTask(new ListImageCallback() {
                @Override
                public void callback(byte[] bytes) {
                    if (bytes != null) {
                        String fileName = getContext().getExternalCacheDir().getAbsolutePath()
                                + File.separator + path.substring(path.lastIndexOf("/") + 1);
                        SdCardUtils.saveData(bytes, fileName);
                        headImage.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    }
                }
            }).execute(path);
        }
        headName.setText(name);
    }

    //头布局的数据源
    private void initViewPagerData() {
        if(NetWorkUtils.isConnected(getContext())) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    byte[] bytes = HttpUtils.loadbytes(Constants.HEADERIMAGE_URL);
                    String fileName = getContext().getExternalCacheDir().getAbsolutePath() + File.separator + "head";
                    SdCardUtils.saveData(bytes, fileName);
                    Message msg = Message.obtain();
                    msg.what = 2;
                    msg.obj = bytes;
                    mHandler.sendMessage(msg);
                }
            }).start();
        }else{
            String fileName = getContext().getExternalCacheDir().getAbsolutePath() + File.separator + "head";
            byte[] bytes = SdCardUtils.getDataFromCache(fileName);
            Message msg = Message.obtain();
            msg.what = 2;
            msg.obj = bytes;
            mHandler.sendMessage(msg);
        }
    }

    //初始化地址
    private void initPath() {
        switch (index) {
            case 0:
                path = Constants.HEADLINE_URL + Constants.HEADLINE_TYPE;
                break;
            case 1:
                path = Constants.BASE_URL + Constants.CYCLOPEDIA_TYPE;
                break;
            case 2:
                path = Constants.BASE_URL + Constants.CONSULT_TYPE;
                break;
            case 3:
                path = Constants.BASE_URL + Constants.OPERATE_TYPE;
                break;
            case 4:
                path = Constants.BASE_URL + Constants.DATA_TYPE;
                break;
        }
    }

    //初始化数据源
    private void initData() {
        if(NetWorkUtils.isConnected(getContext())) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    byte[] bytes = HttpUtils.loadbytes(path + page);
                    String fileName = getContext().getExternalCacheDir().getAbsolutePath() + File.separator + "tea" + index;
                    SdCardUtils.saveData(bytes, fileName);
                    Message msg = Message.obtain();
                    msg.what = 1;
                    msg.obj = bytes;
                    mHandler.sendMessage(msg);
                }
            }).start();
        }else{
            Log.d("flag", "---------------->: 没有网络");
            String fileName = getContext().getExternalCacheDir().getAbsolutePath() + File.separator + "tea" + index;
            byte[] bytes = SdCardUtils.getDataFromCache(fileName);
            Message msg = Message.obtain();
            msg.what = 1;
            msg.obj = bytes;
            mHandler.sendMessage(msg);
        }
    }

    private void initSwipeRefresh() {

    }

    private void initView(View ret) {
        //refresh = (SwipeRefreshLayout) ret.findViewById(R.id.refresh);
        listView = (PullToRefreshListView) ret.findViewById(R.id.listView);
        backTop = (ImageView) ret.findViewById(R.id.goToTop);
        backTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLv.setSelection(0);
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    private Bitmap getCacheBitmap(String path) {
        String name = path.substring(path.lastIndexOf("/") + 1);
        String fileName = getContext().getExternalCacheDir().getAbsolutePath() + File.separator + name;
        byte[] bytes = getDataFromCache(fileName);
        if (bytes != null) {
            Log.d("flag", "---------------->: 从缓存中读取");
            Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            return bitmap1;
        }
        return null;
    }
}
