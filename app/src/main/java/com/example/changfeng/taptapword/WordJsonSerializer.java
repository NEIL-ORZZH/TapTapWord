package com.example.changfeng.taptapword;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by changfeng on 2015/4/19.
 */
public class WordJsonSerializer {
    private Context context;
    private String filename;

    public WordJsonSerializer(Context context, String filename) {
        this.context = context;
        this.filename = filename;
    }

    public ArrayList<Word> loadWords() throws IOException, JSONException {
        ArrayList<Word> words = new ArrayList<>();
        BufferedReader  reader = null;
        try {
            InputStream in = context.openFileInput(filename);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }

            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();

            for (int i = 0; i < array.length(); i++) {
                words.add(new Word(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {

        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return words;
    }

    public ArrayList<Word> restoreWords(File externFile) throws IOException, JSONException {
        ArrayList<Word> words = new ArrayList<>();
        BufferedReader  reader = null;
        try {
            InputStream in = new FileInputStream(externFile);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }

            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();

            for (int i = 0; i < array.length(); i++) {
                words.add(new Word(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {

        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return words;
    }

    public void saveWords(ArrayList<Word> words) throws JSONException, IOException {
        JSONArray array = new JSONArray();
        for (Word word : words) {
            array.put(word.toJSON());
        }

        Writer writer = null;
        try {
            OutputStream out = context.openFileOutput(filename, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public void backupWords(File externFile , ArrayList<Word> words) throws JSONException, IOException {

        JSONArray array = new JSONArray();
        for (Word word : words) {
            array.put(word.toJSON());
        }

        Writer writer = null;
        try {
            if (externFile.createNewFile()) {
                OutputStream out = new FileOutputStream(externFile);
                writer = new OutputStreamWriter(out);
                writer.write(array.toString());
            }
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

}
