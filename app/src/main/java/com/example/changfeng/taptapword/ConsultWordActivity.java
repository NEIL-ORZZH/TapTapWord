package com.example.changfeng.taptapword;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.util.Calendar;


public class ConsultWordActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "ConsultWordActivity";
    private static final int TRANSLATE_RESULT = 1;
    private static final String NO_RESULT = "无法找到结果！";
    private static final int STATE_ORIGIN = 1;
    private static final int STATE_LOWERCASE = 2;
    private static String result = "";
    private EditText wordEditText;
    private String clip;
    private int state;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TRANSLATE_RESULT:
                    MyLog.d(TAG, "handleMessage() " + result + clip);
                    resultTextView.setVisibility(View.VISIBLE);
                    if (state == STATE_ORIGIN) {
                        if (result.isEmpty()) {
                            state = STATE_LOWERCASE;
                            clip = clip.toLowerCase();
                            sendRequestWithHttpClient(clip, "en", "zh");

                        } else {
                            resultTextView.setText(result);
                            resultTextView.setTextColor(Color.parseColor(MainActivity.SELECTED_COLOR));
                            saveWord();
                        }
                    } else if (state == STATE_LOWERCASE) {
                        if (result.isEmpty()) {
                            resultTextView.setText(NO_RESULT);
                        } else {
                            resultTextView.setText(result);

                            saveWord();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private TextView resultTextView;
    private String word_name;
    private String ph_en;
    private String ph_am;
    private StringBuilder word_means;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_consult_word);

        ImageButton addButton = (ImageButton) findViewById(R.id.add_new_word_button);
        addButton.setOnClickListener(this);
        wordEditText = (EditText) findViewById(R.id.word_edit_text);

        resultTextView = (TextView) findViewById(R.id.result_text_view);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_new_word_button:
                resultTextView.setText("");
                resultTextView.setVisibility(View.GONE);
                String word = wordEditText.getText().toString();
                if (isEnglishWord(word)) {
                    state = STATE_ORIGIN;
                    sendRequestWithHttpClient(word, "en", "zh");
                } else {
                    showToast(getResources().getString(R.string.msg_not_support_other_language));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_consult_word, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendRequestWithHttpClient(final String query, final String from, final String to) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    String httpGetUri = "http://apistore.baidu.com/microservice/dictionary?query=" + query + "&from=" + from + "&to=" + to;
                    httpGetUri = httpGetUri.replace(" ", "%20");
//                    HttpGet httpGet = new HttpGet("http://apistore.baidu.com/microservice/dictionary?query=hello&from=en&to=zh");
                    HttpGet httpGet = new HttpGet(httpGetUri);
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        HttpEntity entity = httpResponse.getEntity();
                        String response = EntityUtils.toString(entity, "utf-8");
//                        Log.d(TAG, response);
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
//                    Log.d(TAG, "dict_result " + jsonObject.getString("dict_result"));
                    if (!jsonObject.getString("dict_result").equals("[]")) {
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

                        if (!(ph_am.isEmpty() && ph_en.isEmpty())) {
                            result += "\n\n";
                        }

//                        Log.d(TAG, ph_am);
//                        Log.d(TAG, ph_en);

                        jsonArray = jsonObject.getJSONArray("parts");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            String part = jsonObject.getString("part");
                            result += part + "\n";
                            word_means.append(part);
                            word_means.append("\n");

//                            Log.d(TAG, part);
                            JSONArray means;
                            means = jsonObject.getJSONArray("means");
                            for (int j = 0; j < means.length(); j++) {
                                String mean = means.getString(j);
                                result += mean + "；";
                                word_means.append(mean);
                                word_means.append("；");
//                                Log.d(TAG, mean);
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

        word.setLanguage("English");

        word.setName(word_name);
        word.setEnPhone(ph_en);
        word.setAmPhone(ph_am);
        word.setMeans(word_means.toString());
        word.setArchived(false);

        Calendar c = Calendar.getInstance();
        word.setYear(c.get(Calendar.YEAR));
        word.setMonth(c.get(Calendar.MONTH) + 1);
        word.setDate(c.get(Calendar.DAY_OF_MONTH));
        word.setHour(c.get(Calendar.HOUR_OF_DAY));
        word.setMinute(c.get(Calendar.MINUTE));
        word.setSecond(c.get(Calendar.SECOND));

        WordManger.get(getApplicationContext()).insertWord(word);

    }

    void showToast(String info) {
        Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
    }


    private Boolean isEnglishWord(String word) {
        char[] array = word.toCharArray();
        for (char a : array) {
            if ((char) (byte) a != a) {
                return false;
            }
        }
        return true;
    }
}
