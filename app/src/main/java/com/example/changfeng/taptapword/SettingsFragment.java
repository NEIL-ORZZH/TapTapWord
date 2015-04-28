package com.example.changfeng.taptapword;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gc.materialdesign.widgets.Dialog;
import com.rey.material.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SettingsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "SettingsFragment";

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
                        MyFile.copyFile(new File(getActivity().getFileStreamPath(WordLab.FILENAME).getPath()), MyFile.getOutputBackupFile());
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
                    }
                });
                clearDialog.setCancelable(true);
                clearDialog.show();
            default:
                break;
        }
    }
}