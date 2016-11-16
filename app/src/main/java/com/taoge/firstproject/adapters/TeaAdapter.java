package com.taoge.firstproject.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.taoge.firstproject.R;
import com.taoge.firstproject.beans.Tea;
import com.taoge.firstproject.cache.MyLruCache;
import com.taoge.firstproject.callback.ListImageCallback;
import com.taoge.firstproject.nets.ListImageAsyncTask;
import com.taoge.firstproject.utils.SdCardUtils;

import java.io.File;
import java.util.List;

/**
 * Created by my on 2016/11/11.
 */
public class TeaAdapter extends BaseAdapter {
    private static final int TYPE_ONE = 0;
    private static final int TYPE_TWO = 1;
    private List<Tea.DataBean> data;
    private Context context;
    private MyLruCache mLruCache;
    public TeaAdapter(Context context, List<Tea.DataBean> data) {
        this.context = context;
        this.data = data;
        //分配八分之一的内存
        int maxSize = (int) (Runtime.getRuntime().maxMemory()/8);
        mLruCache = new MyLruCache(maxSize);
    }

    @Override
    public int getCount() {
        return data!=null?data.size():0;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View ret = null;
        ViewHolderOne holderOne = null;
        ViewHolderTwo holderTwo = null;
        int type = getItemViewType(position);

        switch (type){
            case TYPE_ONE:
                if(convertView!=null){
                    ret = convertView;
                    holderOne = (ViewHolderOne) ret.getTag();
                }else{
                    ret = LayoutInflater.from(context).inflate(R.layout.item_one,parent,false);
                    holderOne = new ViewHolderOne();
                    holderOne.create_timeOne = (TextView) ret.findViewById(R.id.create_timeOne);
                    holderOne.descriptionOne = (TextView) ret.findViewById(R.id.descriptionOne);
                    holderOne.nicknameOne = (TextView) ret.findViewById(R.id.nicknameOne);
                    holderOne.sourceOne = (TextView) ret.findViewById(R.id.sourceOne);
                    holderOne.titleOne = (TextView) ret.findViewById(R.id.titleOne);
                    ret.setTag(holderOne);
                }
                holderOne.titleOne.setText(data.get(position).getTitle());
                holderOne.sourceOne.setText(data.get(position).getSource());
                holderOne.nicknameOne.setText(data.get(position).getNickname());
                holderOne.create_timeOne.setText(data.get(position).getCreate_time());
                String description = data.get(position).getDescription();
                if(description.length()>0&&description.length()<10){
                    holderOne.descriptionOne.setText(description);
                }else if(description.length()>10){
                    holderOne.descriptionOne.setText(description.substring(0,10)+"...");
                }else{
                    holderOne.descriptionOne.setText("");
                }
                break;
            case TYPE_TWO:
                if(convertView!=null){
                    ret = convertView;
                    holderTwo = (ViewHolderTwo) ret.getTag();
                }else{
                    ret = LayoutInflater.from(context).inflate(R.layout.item_two,parent,false);
                    holderTwo = new ViewHolderTwo();
                    holderTwo.create_timeTwo = (TextView) ret.findViewById(R.id.create_timeTwo);
                    holderTwo.descriptionTwo = (TextView) ret.findViewById(R.id.descriptionTwo);
                    holderTwo.nicknameTwo = (TextView) ret.findViewById(R.id.nicknameTwo);
                    holderTwo.sourceTwo = (TextView) ret.findViewById(R.id.sourceTwo);
                    holderTwo.titleTwo = (TextView) ret.findViewById(R.id.titleTwo);
                    holderTwo.wap_thumbTwo = (ImageView) ret.findViewById(R.id.wap_thumbTwo);
                    ret.setTag(holderTwo);
                }
                Tea.DataBean dataBean = data.get(position);
                holderTwo.titleTwo.setText(dataBean.getTitle());
                holderTwo.sourceTwo.setText(dataBean.getSource());
                holderTwo.nicknameTwo.setText(dataBean.getNickname());
                holderTwo.create_timeTwo.setText(dataBean.getCreate_time());
                String description2 = data.get(position).getDescription();
                if(description2.length()>0&&description2.length()<10){
                    holderTwo.descriptionTwo.setText(description2);
                }else if(description2.length()>10){
                    holderTwo.descriptionTwo.setText(description2.substring(0,10)+"...");
                }else{
                    holderTwo.descriptionTwo.setText("");
                }
                String path = dataBean.getWap_thumb();
                //final ViewHolderTwo finalHolder = holderTwo;
                if(path!=null&&position!=9){
                    Bitmap cacheBitmap = getCacheBitmap(path);
                    if (cacheBitmap != null) {
                        holderTwo.wap_thumbTwo.setImageBitmap(cacheBitmap);
                    }else {
                        holderTwo.wap_thumbTwo.setTag(path);
                        getNetImage(path,holderTwo.wap_thumbTwo);

                    }
                }
                break;
        }
        return ret;
    }

    private void getNetImage(final String path, final ImageView wap_thumbTwo) {
        new ListImageAsyncTask(new ListImageCallback() {
            @Override
            public void callback(byte[] bytes) {
                String tag = (String) wap_thumbTwo.getTag();
                if (tag.equals(path)) {
                    if (bytes != null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        Log.d("flag", "---------------->: 联网获取");
                        wap_thumbTwo.setImageBitmap(bitmap);
                        mLruCache.put(path.substring(path.lastIndexOf("/") + 1), bitmap);//存取到内存
                        String fileName = context.getExternalCacheDir().getAbsolutePath()
                                + File.separator+path.substring(path.lastIndexOf("/")+1);
                        SdCardUtils.saveData(bytes,fileName);
                    }
                }
            }
        }).execute(path);
    }

    //从内存 和 缓存中读取图片
    private Bitmap getCacheBitmap(String path) {
        String name = path.substring(path.lastIndexOf("/")+1);
        Bitmap bitmap = mLruCache.get(name);
        if (bitmap != null) {
            Log.d("flag", "---------------->:从内存中读取" );
            return bitmap;
        }else{
            String fileName = context.getExternalCacheDir().getAbsolutePath()+ File.separator+name;
//            String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                    .getAbsolutePath()+File.separator+name;
            byte[] bytes = SdCardUtils.getDataFromCache(fileName);
            if (bytes != null) {
                Log.d("flag", "---------------->: 从缓存中读取");
                Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                mLruCache.put(name,bitmap1);
                return bitmap1;
            }
        }
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        String wap_thumb = data.get(position).getWap_thumb();
        if("".equals(wap_thumb)){
            return TYPE_ONE;
        }else {
            return TYPE_TWO;
        }
    }

    private static class ViewHolderOne{
        private TextView titleOne,sourceOne,descriptionOne,create_timeOne,nicknameOne;
    }

    private static class ViewHolderTwo{
        private TextView titleTwo,sourceTwo,descriptionTwo,create_timeTwo,nicknameTwo;
        private ImageView wap_thumbTwo;
    }
}
