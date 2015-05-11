package com.example.changfeng.taptapword;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by changfeng on 2015/5/10.
 */
public class WordManger {

    private static final String TAG = "RunManager";

    private static WordManger wordManger;
    private static String dataBase = "Words.db";



    private Context mAppContext;
    private DatabaseHelper mHelper;

    private WordManger(Context appContext) {
        mAppContext = appContext;
        mHelper = new DatabaseHelper(mAppContext, dataBase, null, 1);
    }

    public static WordManger get(Context context) {
        if (wordManger == null) {
            wordManger = new WordManger(context.getApplicationContext());
        }
        return wordManger;
    }

    public void insertWord(Word word) {
        mHelper.insertWord(word);
    }

    public DatabaseHelper.WordCursor queryWords() {
        return mHelper.queryWords();
    }

}
