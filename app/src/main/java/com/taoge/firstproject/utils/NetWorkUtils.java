package com.taoge.firstproject.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by my on 2016/11/4.
 */

public class NetWorkUtils {
    public static boolean isConnected(Context context){

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo == null) {
            return false;
        }

        switch (activeNetworkInfo.getType()){
            case ConnectivityManager.TYPE_WIFI:
                Toast.makeText(context,"当前的网络是wifi",Toast.LENGTH_LONG).show();
                return true;
            case ConnectivityManager.TYPE_MOBILE:
                Toast.makeText(context,"当前的网络是移动网络",Toast.LENGTH_LONG).show();
                return true;
        }

        return false;
    }
}
