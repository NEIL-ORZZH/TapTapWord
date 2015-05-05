package com.example.changfeng.taptapword;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class ResultActivity extends Activity {

    private static final String TAG = "ResultActivity";
    private static int count = 0;
    private static final int TRANSLATE_RESULT = 1;
    private static String result = "";
    private static final String NO_RESULT = "无法找到结果！";
    private String clip;
    private int state;
    private static final int STATE_ORIGIN = 1;
    private static final int STATE_LOWERCASE = 2;

    private TextView translateResultTextView;

    private String word_name;
    private String ph_en;
    private String ph_am;
    private StringBuilder word_means;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_result);

        translateResultTextView = (TextView) findViewById(R.id.translate_result);

        clip = getIntent().getStringExtra(ClipboardService.clipboardText);

        result = "";
        state = STATE_ORIGIN;
        sendRequestWithHttpClient(clip, "en", "zh");
        count++;
//        showToast(TAG + " onCreate() " + count + " times.");
    }

    @Override
    protected void onDestroy() {
//        showToast(TAG + "onDestroy() called.");
        super.onDestroy();
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TRANSLATE_RESULT:
                    Log.d(TAG,"handleMessage() " + result + clip);
                    if (state == STATE_ORIGIN) {
                        if (result.isEmpty()) {
                            state = STATE_LOWERCASE;
                            clip = clip.toLowerCase();
                            sendRequestWithHttpClient(clip,"en", "zh");

                        } else {
                            translateResultTextView.setText(result);
                            translateResultTextView.setTextColor(Color.parseColor(MainActivity.SELECTED_COLOR));
                            saveWord();
                        }
                    } else if (state == STATE_LOWERCASE) {
                        if (result.isEmpty()) {
                            translateResultTextView.setText(NO_RESULT);
                        } else {
                            translateResultTextView.setText(result);
                            saveWord();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };



    private void sendRequestWithHttpClient(final String query, final String from, final String to) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    String httpGetUri = "http://apistore.baidu.com/microservice/dictionary?query="+query+"&from="+from+"&to="+to;
                    httpGetUri = httpGetUri.replace(" ", "%20");
//                    HttpGet httpGet = new HttpGet("http://apistore.baidu.com/microservice/dictionary?query=hello&from=en&to=zh");
                    HttpGet httpGet = new HttpGet(httpGetUri);
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        HttpEntity entity = httpResponse.getEntity();
                        String response = EntityUtils.toString(entity, "utf-8");
                        Log.d(TAG, response);
                        parseJsonWithJsonObject(response);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // Do nothing
                }
            }
        }).start();
    }


    private void parseJsonWithJsonObject(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            word_means = new StringBuilder();

            if (jsonObject.getInt("errNum") == 0 && jsonObject.getString("errMsg").equals("success")) {
                jsonObject = jsonObject.getJSONObject("retData");
                if (jsonObject.has("dict_result")) {
                    Log.d(TAG, "dict_result " + jsonObject.getString("dict_result"));
                    if (!jsonObject.getString("dict_result").equals("[]")){
                        jsonObject = jsonObject.getJSONObject("dict_result");
                        word_name = jsonObject.getString("word_name");
                        JSONArray jsonArray = jsonObject.getJSONArray("symbols");
                        jsonObject = jsonArray.getJSONObject(0);
                        result = word_name + "\n\n";
                        ph_am = jsonObject.getString("ph_am");
                        if (!ph_am.isEmpty()) {
                            result += "美:[" + ph_am + "] ";
                        }
                        ph_en = jsonObject.getString("ph_en");
                        if (!ph_en.isEmpty()) {
                            result += "英:[" + ph_en + "] ";
                        }

                        if (!(ph_am.isEmpty() && ph_en.isEmpty())){
                            result += "\n\n";
                        }

                        Log.d(TAG, ph_am);
                        Log.d(TAG, ph_en);

                        jsonArray = jsonObject.getJSONArray("parts");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            String part = jsonObject.getString("part");
                            result += part + "\n";
                            word_means.append(part);
                            word_means.append("\n");

                            Log.d(TAG, part);
                            JSONArray means;
                            means = jsonObject.getJSONArray("means");
                            for (int j = 0; j < means.length(); j++) {
                                String mean = means.getString(j);
                                result += mean + "；";
                                word_means.append(mean);
                                word_means.append("；");
                                Log.d(TAG, mean);
                            }
                            result += "\n";
                            word_means.append("\n");
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Message message = new Message();
        message.what = TRANSLATE_RESULT;
        handler.sendMessage(message);
    }

    private void saveWord() {
        Word word = new Word();
        word.setName(word_name);
        word.setEnPhone(ph_en);
        word.setAmPhone(ph_am);
        word.setMeans(word_means.toString());

        WordLab.get(getApplicationContext()).addWord(word);
        WordLab.get(getApplicationContext()).saveWords();

    }
    void showToast(String info) {
        Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
    }

}
