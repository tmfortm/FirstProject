package com.taoge.firstproject.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by my on 2016/11/12.
 */

public class MySQliteOpenHelper extends SQLiteOpenHelper {
    public MySQliteOpenHelper(Context context) {
        super(context, "myData.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table data(_id integer primary key autoincrement" +
                ",title varchar,source varchar,nickname varchar,create_time varchar," +
                "wap_thumb varchar,id varchar,description varchar)");

        db.execSQL("create table history(_id integer primary key autoincrement" +
                ",title varchar,source varchar,nickname varchar,create_time varchar," +
                "wap_thumb varchar,id varchar,description varchar)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
