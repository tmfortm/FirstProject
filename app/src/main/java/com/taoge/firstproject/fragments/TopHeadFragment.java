package com.taoge.firstproject.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.taoge.firstproject.R;
import com.taoge.firstproject.callback.ImageCallback;
import com.taoge.firstproject.nets.ImageAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class TopHeadFragment extends Fragment implements ImageCallback {

    private ImageView headImage;
    private TextView headName;
    private String name;
    private String path;

    public TopHeadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.fragment_top_head, container, false);
        initView(ret);
        Bundle arguments = getArguments();
        if (arguments != null) {
            path = arguments.getString("path");
            Log.d("flag", "---------------->: argument" +path);
            name = arguments.getString("name");
        }

        initData();

        return ret;
    }

    private void initData() {
        new ImageAsyncTask(this).execute(path);
    }

    private void initView(View ret) {
        headImage = (ImageView) ret.findViewById(R.id.headImage);
        headName = (TextView) ret.findViewById(R.id.headName);
    }

    @Override
    public void callback(Bitmap bitmap) {
        headImage.setImageBitmap(bitmap);
        headName.setText(name);
    }
}
