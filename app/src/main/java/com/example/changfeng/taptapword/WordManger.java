package com.example.changfeng.taptapword;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        mHelper.deleteExistingWords(word);
        mHelper.insertWord(word);
    }

    public void deleteExistingWords(Word word) {
        mHelper.deleteExistingWords(word);
    }
    public void insertWords(ArrayList<Word> words) {
        for (Word word : words) {
            mHelper.insertWord(word);
        }
    }

    public DatabaseHelper.WordCursor queryWords() {
        return mHelper.queryWords();
    }

    public ArrayList<Word> getWords() {
        return mHelper.loadWords();
    }

    public ArrayList<Word> getArchivedWords() {
        return mHelper.loadArchivedWords();
    }

    public ArrayList<Word> getUnarchivedWords() {
        return mHelper.loadUnarchivedWords();
    }

    public void replaceWord(Word word) {
        mHelper.replaceWord(word);
    }

    public void updateWord(Word word) {
        mHelper.updateWord(word);
    }

    public void deleteWord(Word word) {
        mHelper.deleteWord(word);
    }

    public boolean copyDbToSdcard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String destinationFile = Environment.getExternalStorageDirectory() + File.separator + "word_ninja_" + timeStamp;
            return MyFile.copyFile(mAppContext.getDatabasePath(dataBase).getAbsolutePath(), destinationFile);
        } else {
            return false;
        }

    }

    public boolean restorDb(String filename) {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && MyFile.copyFile(filename, mAppContext.getDatabasePath(dataBase).getAbsolutePath());
    }

}
