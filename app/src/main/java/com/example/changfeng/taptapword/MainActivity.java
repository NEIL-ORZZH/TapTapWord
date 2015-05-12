package com.example.changfeng.taptapword;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import net.youmi.android.AdManager;
import net.youmi.android.spot.SpotManager;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";
    private static final String FRAGMENT_TAG = "CURRENT_FRAGMENT";
    public static final String SELECTED_COLOR = "#4caf50";
    public static final String WORD_TEXT_COLOR = "#293835";

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private boolean isWatchOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        RecyclerView drawerOptions = (RecyclerView) findViewById(R.id.drawer_options);
        setSupportActionBar(toolbar);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);
        drawerLayout.setStatusBarBackground(R.color.selected_green);
        drawerLayout.setDrawerListener(drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        final List<DrawerItem> drawerItems = Arrays.asList(
                new DrawerItem(DrawerItem.Type.HEADER),
                new DrawerMenu().setIconRes(R.drawable.ic_recent).setText(getString(R.string.menu_recent_words)),
                new DrawerMenu().setIconRes(R.drawable.ic_word).setText(getString(R.string.menu_words)),
                new DrawerItem(DrawerItem.Type.DIVIDER),
                new DrawerMenu().setIconRes(R.drawable.ic_watch).setText(getString(R.string.menu_turn_tap_watching_on_off)),
                new DrawerItem(DrawerItem.Type.DIVIDER),
                new DrawerMenu().setIconRes(R.drawable.ic_settings).setText(getString(R.string.menu_settings)),
                new DrawerItem(DrawerItem.Type.DIVIDER),
                new DrawerMenu().setIconRes(R.drawable.ic_help).setText(getString(R.string.menu_help)),
                new DrawerItem(DrawerItem.Type.DIVIDER),
                new DrawerMenu().setIconRes(R.drawable.abc_ic_menu_share_mtrl_alpha).setText(getString(R.string.menu_share)),
                new DrawerMenu().setIconRes(R.drawable.ic_contact).setText(getString(R.string.menu_contact_us)),
                new DrawerMenu().setIconRes(R.drawable.ic_about).setText(getString(R.string.menu_about)));
//                new DrawerMenu().setIconRes(R.drawable.ic_about).setText("test"));
        drawerOptions.setLayoutManager(new LinearLayoutManager(this));
        final DrawerItemAdapter adapter = new DrawerItemAdapter(drawerItems);
        adapter.setOnItemClickListener(new DrawerItemAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                onDrawerMenuSelected(position);
                switch (position) {
                    case 0:

                        break;
                    case 1:
                        setupFragment(new RecentWordFragment());
                        setTitle(getString(R.string.menu_recent_words));
                        break;
                    case 2:
                        setupFragment(new WordsFragment());
                        setTitle(getString(R.string.menu_words));
                        break;
                    case 3: // Divider
                        break;
                    case 4:
                        // TODO:need to be fixed
                        if (!isWatchOn) {
                            startClipboardService();
                            showToast("单词忍者正在监听");
                        } else {
                            startClipboardService();
                            showToast("单词忍者正在监听");
                        }
                        isWatchOn = !isWatchOn;
                        break;
                    case 5:// Divider
                        break;
                    case 6:
                        setupFragment(new SettingsFragment());
                        setTitle(getString(R.string.menu_settings));
                        break;
                    case 7: // Divider
                        break;
                    case 8:
                        setupFragment(new HelpFragment());
                        setTitle(getString(R.string.menu_help));
                    case 9:// Divider
                        break;
                    case 10:
                        shareByIntent();
                        break;
                    case 11:
                        sendMailByIntent();
                        break;
                    case 12:
                        setupFragment(new AboutFragment());
                        setTitle(getString(R.string.menu_about));
                        break;
//                    case 13:
//                        setupFragment(new TestFragment());
//                        break;
                    default:
                        break;
                }
            }
        });
        drawerOptions.setAdapter(adapter);
        drawerOptions.setHasFixedSize(true);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(!prefs.getBoolean("first_time", false)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("first_time", true);
            editor.commit();

            setupFragment(new HelpFragment());

            Word word = new Word();
            word.setName(getString(R.string.word_word_name));
            word.setAmPhone(getString(R.string.word_word_ph_am));
            word.setEnPhone(getString(R.string.word_word_ph_en));
            word.setMeans(getString(R.string.word_word_means));

            Word ninjaWord = new Word();
            ninjaWord.setName(getString(R.string.word_ninja_name));
            ninjaWord.setAmPhone(getString(R.string.word_ninja_ph_am));
            ninjaWord.setEnPhone(getString(R.string.word_ninja_ph_en));
            ninjaWord.setMeans(getString(R.string.word_ninja_means));

            Word recentWord = new Word();
            recentWord.setName(getString(R.string.word_recent_name));
            recentWord.setAmPhone(getString(R.string.word_recent_ph_am));
            recentWord.setEnPhone(getString(R.string.word_recent_ph_en));
            recentWord.setMeans(getString(R.string.word_recent_means));

            Word archiveWord = new Word();
            archiveWord.setName(getString(R.string.word_archive_name));
            archiveWord.setAmPhone(getString(R.string.word_archive_ph_am));
            archiveWord.setEnPhone(getString(R.string.word_archive_ph_en));
            archiveWord.setMeans(getString(R.string.word_archive_means));

            Word deleteWord = new Word();
            deleteWord.setName(getString(R.string.word_delete_name));
            deleteWord.setAmPhone(getString(R.string.word_delete_ph_am));
            deleteWord.setEnPhone(getString(R.string.word_delete_ph_en));
            deleteWord.setMeans(getString(R.string.word_delete_means));

            Word unarchiveWord = new Word();
            unarchiveWord.setName(getString(R.string.word_unarchive_name));
            unarchiveWord.setAmPhone(getString(R.string.word_unarchive_ph_am));
            unarchiveWord.setEnPhone(getString(R.string.word_unarchive_ph_en));
            unarchiveWord.setMeans(getString(R.string.word_unarchive_means));
            unarchiveWord.setArchived(true);

            Word delete2Word = new Word();
            delete2Word.setName(getString(R.string.word_delete2_name));
            delete2Word.setAmPhone(getString(R.string.word_delete2_ph_am));
            delete2Word.setEnPhone(getString(R.string.word_delete2_ph_en));
            delete2Word.setMeans(getString(R.string.word_delete2_means));
            delete2Word.setArchived(true);

            ArrayList<Word> words = new ArrayList<>();
            words.add(delete2Word);
            words.add(unarchiveWord);
            words.add(deleteWord);
            words.add(archiveWord);
            words.add(recentWord);
            words.add(ninjaWord);
            words.add(word);

            WordManger.get(getApplicationContext()).insertWords(words);

        } else {
            setupFragment(new RecentWordFragment());
            setTitle(getString(R.string.menu_recent_words));
        }

        startClipboardService();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
 //       Log.d(TAG, "onActivityResult() called resultCode :" + requestCode + " requestCode :" + requestCode);
    }



    private void setupFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG);

        if (currentFragment == null || !currentFragment.getClass().equals(fragment.getClass())) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment, FRAGMENT_TAG)
                    .commit();
        }
    }

    private void onDrawerMenuSelected(int position) {
        drawerLayout.closeDrawers();
    }

    void startClipboardService() {
        stopClipboardService();
        Intent startIntent = new Intent(MainActivity.this, ClipboardService.class);
        startService(startIntent);
    }

    void stopClipboardService() {
        Intent stopIntent = new Intent(MainActivity.this, ClipboardService.class);
        stopService(stopIntent);

    }

    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(30);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    private void shareByIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + " " + getString(R.string.uri_download_wandoujia));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, "分享给你的亲友们"));
    }

    public void sendMailByIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/octet-stream");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.mail_address)});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_subject));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_text));
        startActivity(Intent.createChooser(intent, "请选择你的邮箱应用"));
    }



    void showToast(String info) {
        Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
    }
}