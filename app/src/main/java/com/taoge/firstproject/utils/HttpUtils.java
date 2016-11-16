package com.taoge.firstproject.utils;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by my on 2016/11/11.
 */
public class HttpUtils {
    public static byte[] loadbytes(String path) {

        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        builder.url(path);
        Request request = builder.build();
        try {
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                return response.body().bytes();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
