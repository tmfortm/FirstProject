package com.taoge.firstproject.nets;

import android.os.AsyncTask;

import com.taoge.firstproject.callback.ListImageCallback;
import com.taoge.firstproject.utils.HttpUtils;

/**
 * Created by my on 2016/11/14.
 */

public class ListImageAsyncTask extends AsyncTask<String,Void,byte[]> {

    private ListImageCallback mListImageCallback;

    public ListImageAsyncTask(ListImageCallback mListImageCallback) {
        this.mListImageCallback = mListImageCallback;
    }

    @Override
    protected byte[] doInBackground(String... params) {
        byte[] data = HttpUtils.loadbytes(params[0]);
        return data;
    }

    @Override
    protected void onPostExecute(byte[] bytes) {
        super.onPostExecute(bytes);
        mListImageCallback.callback(bytes);
    }
}
