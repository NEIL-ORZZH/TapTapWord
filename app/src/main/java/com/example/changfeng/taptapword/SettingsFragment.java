package com.example.changfeng.taptapword;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Toast;

import com.gc.materialdesign.widgets.Dialog;
import com.rey.material.widget.TextView;

import junit.framework.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class SettingsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "SettingsFragment";
    static final int REQUEST_BACKUP_FILE = 1;
    private static final String FILE_KEY_WORD = "word_ninja";

    private TextView resetAllDataTextView;
    private TextView resetAllPreferencesTextView;
    private TextView backupDataTextView;
    private TextView restoreDataTextView;
    private TextView clearBackupDataTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        resetAllDataTextView = (TextView) view.findViewById(R.id.reset_all_data);
        resetAllPreferencesTextView = (TextView) view.findViewById(R.id.reset_all_preferences);
        backupDataTextView = (TextView) view.findViewById(R.id.backup_data);
        restoreDataTextView = (TextView) view.findViewById(R.id.restore_data);
        clearBackupDataTextView = (TextView) view.findViewById(R.id.clear_backup_data);

        resetAllDataTextView.setOnClickListener(this);
        resetAllPreferencesTextView.setOnClickListener(this);
        backupDataTextView.setOnClickListener(this);
        restoreDataTextView.setOnClickListener(this);
        clearBackupDataTextView.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset_all_data:
                Dialog dataDialog = new Dialog(getActivity(), getString(R.string.reset_all_data), getString(R.string.message_reset_all_data));
                dataDialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WordLab.get(getActivity()).clear();
                        WordLab.get(getActivity()).saveWords();
                    }
                });
                dataDialog.setCancelable(true);
                dataDialog.show();
                break;
            case R.id.reset_all_preferences:
                Dialog preferenceDialog = new Dialog(getActivity(), getString(R.id.reset_all_preferences), getString(R.string.message_reset_all_preferences));
                preferenceDialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                preferenceDialog.setCancelable(true);
                preferenceDialog.show();
                break;
            case R.id.backup_data:
                final Dialog backupDialog = new Dialog(getActivity(), getString(R.string.backup), getString(R.string.message_backup_data));
                backupDialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (WordManger.get(getActivity()).copyDbToSdcard()) {
                            showToast(getString(R.string.message_data_backupped), Toast.LENGTH_SHORT);
                        } else {
                            showToast(getString(R.string.message_data_backupped_failed), Toast.LENGTH_SHORT);
                        }
                    }

                });
                backupDialog.setCancelable(true);
                backupDialog.show();
                break;
            case R.id.restore_data:
                Dialog restoreDialog = new Dialog(getActivity(), getString(R.string.restore), getString(R.string.message_restore_data));
                restoreDialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        Intent intent = new Intent(getActivity(), FileListActivity.class);
                        intent.putExtra(FileListActivity.FILE_KEY_WORD, "word_ninja");
                        startActivityForResult(intent, REQUEST_BACKUP_FILE);
                    }
                });
                restoreDialog.setCancelable(true);
                restoreDialog.show();
                break;
            case R.id.clear_backup_data:
                Dialog clearDialog = new Dialog(getActivity(), getString(R.string.clear_backup_data), getString(R.string.message_clear_backup_data));
                clearDialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clearAllBackupData();
                        showToast(getString(R.string.message_all_backup_data_cleared), Toast.LENGTH_SHORT);
                    }
                });
                clearDialog.setCancelable(true);
                clearDialog.show();
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d(TAG, "onActivityResult() called resultCode :" + requestCode + " requestCode :" + requestCode);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_BACKUP_FILE:
                    if (WordManger.get(getActivity()).restorDb(data.getExtras().getString(FileListActivity.FILE_KEY_WORD))) {
                        showToast(getString(R.string.message_data_restored), Toast.LENGTH_SHORT);
                    } else {
                        showToast(getString(R.string.message_data_restored_failed), Toast.LENGTH_SHORT);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void clearAllBackupData() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File path = Environment.getExternalStorageDirectory();
            File files[] = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        continue;
                    }
                    if (file.getName().contains(FILE_KEY_WORD)) {
                        MyFile.deleleFile(file.getAbsolutePath());
                    }
                }
            }
        }
    }

    private void showToast(String message, int duration) {
        Toast.makeText(getActivity(), message, duration).show();
    }
}