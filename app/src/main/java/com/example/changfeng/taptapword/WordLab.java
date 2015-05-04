package com.example.changfeng.taptapword;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by changfeng on 2015/4/19.
 */
public class WordLab {

    private static final String TAG = "WordLab";
    public static final String FILENAME = "words.json";

    private ArrayList<Word> mWords;
    private WordJsonSerializer mSerializer;

    private static WordLab sWordLab;
    private Context mAppContext;



    /**
     * Getter for property 'mWords'.
     *
     * @return Value for property 'mWords'.
     */
    public ArrayList<Word> getWords() {
        Log.d(TAG, "getWords() called");
        return mWords;
    }

    /**
     * Setter for property 'mWords'.
     *
     * @param mWords Value to set for property 'mWords'.
     */
    public void setWords(ArrayList<Word> mWords) {
        this.mWords = mWords;
    }

    public void addWord(Word word) {
        mWords.add(word);
    }

    public boolean saveWords() {
        try {
            mSerializer.saveWords(mWords);
            Log.d(TAG, "words saved to file");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving words: ", e);
            return false;
        }
    }

    public boolean backupWords(File externFile) {
        try {
            mSerializer.backupWords(externFile, mWords);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error backup words:", e);
            return false;
        }
    }

    public boolean restoreWords(File externFile) {
        Log.d(TAG, "WordLab() called");
        try {
            mWords = mSerializer.restoreWords(externFile);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error loading words: ", e);
            return false;
        }
    }

    public boolean isAllAchived() {
        for (Word word : mWords) {
            if (!word.isArchived()) {
                return false;
            }
        }
        return true;
    }




    private WordLab(Context context) {
        Log.d(TAG, "WordLab() called");
        mAppContext = context;
        mSerializer = new WordJsonSerializer(mAppContext, FILENAME);

        try {
            mWords = mSerializer.loadWords();
        } catch (Exception e) {
            mWords = new ArrayList<>();
            Log.e(TAG, "Error loading words: ", e);
        }
    }

    public static WordLab get(Context context) {
        Log.d(TAG, "get() called");
        if (sWordLab == null) {
            sWordLab = new WordLab(context.getApplicationContext());
        }
        return sWordLab;
    }

    public void clear() {
        mWords.clear();
        saveWords();
    }
}
