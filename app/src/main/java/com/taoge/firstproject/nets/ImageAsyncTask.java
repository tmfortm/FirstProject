package com.taoge.firstproject.nets;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.taoge.firstproject.callback.ImageCallback;
import com.taoge.firstproject.utils.HttpUtils;

/**
 * Created by my on 2016/11/11.
 */

public class ImageAsyncTask extends AsyncTask<String,Void,Bitmap>{

    private ImageCallback mImageCallback;

    public ImageAsyncTask(ImageCallback imageCallback) {
        mImageCallback = imageCallback;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        byte[] data = HttpUtils.loadbytes(params[0]);
        if(data==null||data.length==0){
            Bitmap bitmap = null;
        }
        Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        mImageCallback.callback(bitmap);
    }

}
