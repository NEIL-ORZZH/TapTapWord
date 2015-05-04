package com.example.changfeng.taptapword;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import java.util.Arrays;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";
    private static final String FRAGMENT_TAG = "CURRENT_FRAGMENT";
    public static final String SELECTED_COLOR = "#4caf50";
    public static final String SEPERATIOR_COLOR = "#1bbc9b";
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
        List<DrawerItem> drawerItems = Arrays.asList(
                new DrawerItem(DrawerItem.Type.HEADER),
                new DrawerMenu().setIconRes(R.drawable.ic_group).setText(getString(R.string.menu_recent_words)),
                new DrawerMenu().setIconRes(R.drawable.ic_group).setText(getString(R.string.menu_turn_tap_watching_on_off)),
                new DrawerMenu().setIconRes(R.drawable.ic_group).setText(getString(R.string.menu_words)),
                new DrawerMenu().setIconRes(R.drawable.ic_group).setText(getString(R.string.menu_settings)),
                new DrawerMenu().setIconRes(R.drawable.ic_group).setText(getString(R.string.menu_share)),
                new DrawerMenu().setIconRes(R.drawable.ic_group).setText(getString(R.string.menu_contact_us)),
                new DrawerMenu().setIconRes(R.drawable.ic_group).setText(getString(R.string.menu_about)));
//                new DrawerMenu().setIconRes(R.drawable.ic_group).setText(getString(R.string.menu_template, 1)),
//                new DrawerMenu().setIconRes(R.drawable.ic_map).setText(getString(R.string.menu_template, 2)),
//                new DrawerItem(DrawerItem.Type.DIVIDER),
//                new DrawerMenu().setIconRes(R.drawable.ic_person).setText(getString(R.string.menu_template, 3)),
//                new DrawerMenu().setIconRes(R.drawable.ic_search).setText(getString(R.string.menu_template, 4)),
//                new DrawerItem(DrawerItem.Type.DIVIDER),
//                new DrawerMenu().setIconRes(R.drawable.ic_settings).setText(getString(R.string.menu_settings)));
        drawerOptions.setLayoutManager(new LinearLayoutManager(this));
        DrawerItemAdapter adapter = new DrawerItemAdapter(drawerItems);
        adapter.setOnItemClickListener(new DrawerItemAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                onDrawerMenuSelected(position);
                switch (position) {
                    case 1:
                        setupFragment(new RecentWordFragment());
                        setTitle(getString(R.string.menu_recent_words));
                        break;
                    case 2: {
                        // TODO:need to be fixed
                        if (!isWatchOn) {
                            startClipboardService();
                            showToast("单词忍者正在监听");
                        } else {
                            startClipboardService();
                            showToast("单词忍者正在监听");
                        }
                        isWatchOn = !isWatchOn;
                    }
                    break;
                    case 3:
                        setupFragment(new WordsFragment());
                        setTitle(getString(R.string.menu_words));
                        break;
                    case 4:
                        setupFragment(new SettingsFragment());
                        setTitle(getString(R.string.menu_settings));
                        break;
                    case 5:
                        shareByIntent();
                        break;
                    case 6:
                        sendMailByIntent();
                        break;
                    case 7:
                        setupFragment(new AboutFragment());
                        setTitle(getString(R.string.menu_about));
                        break;
                    default:
                        break;
                }
            }
        });
        drawerOptions.setAdapter(adapter);
        drawerOptions.setHasFixedSize(true);
        if (savedInstanceState == null) {
            setupFragment(new RecentWordFragment());
        }
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
        super.onActivityResult(requestCode, resultCode,data);
        Log.d(TAG, "onActivityResult() called resultCode :" + requestCode + " requestCode :" + requestCode);
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
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, getTitle()));
    }

    public void sendMailByIntent() {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("application/octet-stream");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.mail_address)});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_subject));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_text));
        startActivity(Intent.createChooser(intent, "Mail Chooser"));
    }

    void showToast(String info) {
        Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
    }
}