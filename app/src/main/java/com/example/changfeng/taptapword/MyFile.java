package com.example.changfeng.taptapword;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by changfeng on 2015/4/23.
 */
public class MyFile {
    private static final String TAG = "MyFile";
    private Context mAppComtext;

    MyFile(Context context) {
        mAppComtext = context;
    }


    public static boolean createDir() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            String sdDir = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
            sdDir += File.separator + "command";
            File newFile = new File(sdDir);
            if (!newFile.exists()) {
                newFile.mkdir();
                Log.d(TAG, "createFile() called() "+ newFile.getAbsolutePath());
                return true;
            }
        }
        return false;
    }

    public static boolean createFile() {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "sharedMemory.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                // TODO: handle exception
                Log.e(TAG, "createFile() called " + file.getAbsolutePath(), e);
            }
        }
        return false;
    }
    public static File getOutputBackupFile() {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File backDir = Environment.getExternalStorageDirectory();

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            return new File(backDir.getPath() + File.separator + "TapTapWord_" + timeStamp + ".json");
        } else {
            Log.d(TAG, "getOutputBackupFile() called error");
            return null;
        }
    }

    public  static boolean copyFile(File srcPath, File dstPath) {
        try {
            int byteSum = 0;
            int byteRead;

            if (srcPath.exists() && dstPath.createNewFile() && dstPath.setWritable(true)) { //文件存在时
                InputStream inStream = new FileInputStream(srcPath); //读入原文件
                RandomAccessFile raf = new RandomAccessFile(dstPath, "rw");


                byte[] buffer = new byte[1444];
                while ( (byteRead = inStream.read(buffer)) != -1) {
                    byteSum += byteRead; //字节数 文件大小
                    System.out.println(byteSum);
                    raf.seek(dstPath.length());
                    raf.write(buffer);
                }
                inStream.close();
                raf.close();
            }
            return true;
        }
        catch (Exception e) {
            Log.e(TAG, "copyFile() failed", e);
            e.printStackTrace();
            return false;
        }
    }

}