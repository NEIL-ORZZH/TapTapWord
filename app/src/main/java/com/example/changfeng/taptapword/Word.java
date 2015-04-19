package com.example.changfeng.taptapword;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import javax.crypto.EncryptedPrivateKeyInfo;

/**
 * Created by changfeng on 2015/4/19.
 */
public class Word {
    private UUID mId;
    private String mName;
    private String mEnPhone;
    private String mAmPhone;
    private String mMeans;
    private Date date;

    private static final String JSON_ID = "id";
    private static final String JSON_NAME = "name";
    private static final String JSON_EN_PHONE = "en_phone";
    private static final String JSON_AM_PHONE = "am_phone";
    private static final String JSON_MEANS = "means";

    public Word(JSONObject jsonObject) throws JSONException {
        mId = UUID.fromString(jsonObject.getString(JSON_ID));
        mName = jsonObject.getString(JSON_NAME);
        if (jsonObject.has(JSON_EN_PHONE)) {
            mEnPhone = jsonObject.getString(JSON_EN_PHONE);
        }
        if (jsonObject.has(JSON_AM_PHONE)) {
            mAmPhone = jsonObject.getString(JSON_AM_PHONE);
        }

        if (jsonObject.has(JSON_MEANS)) {
            mMeans = jsonObject.getString(JSON_MEANS);
        }

    }

    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_ID, mId.toString());
        jsonObject.put(JSON_NAME, mName);
        jsonObject.put(JSON_EN_PHONE, mEnPhone);
        jsonObject.put(JSON_AM_PHONE, mAmPhone);
        jsonObject.put(JSON_MEANS, mMeans);
        return jsonObject;
    }

    /**
     * Getter for property 'mName'.
     *
     * @return Value for property 'mName'.
     */
    public String getName() {
        return mName;
    }

    /**
     * Setter for property 'mName'.
     *
     * @param name Value to set for property 'mName'.
     */
    public void setName(String name) {
        this.mName = name;
    }

    /**
     * Getter for property 'mEnPhone'.
     *
     * @return Value for property 'mEnPhone'.
     */
    public String getEnPhone() {
        return mEnPhone;
    }

    /**
     * Setter for property 'mEnPhone'.
     *
     * @param enPhone Value to set for property 'mEnPhone'.
     */
    public void setEnPhone(String enPhone) {
        this.mEnPhone = enPhone;
    }

    /**
     * Getter for property 'mAmPhone'.
     *
     * @return Value for property 'mAmPhone'.
     */
    public String getAmPhone() {
        return mAmPhone;
    }

    /**
     * Setter for property 'mAmPhone'.
     *
     * @param amPhone Value to set for property 'mAmPhone'.
     */
    public void setAmPhone(String amPhone) {
        this.mAmPhone = amPhone;
    }

    public void setMeans(String means) {
        this.mMeans = means;
    }

    public String getMeans() {
        return mMeans;
    }

    public Word() {
        mId = UUID.randomUUID();
    }

    public boolean hasEnPhone() {
       return mEnPhone != null;
    }

    public boolean hasAmPhone() {
        return mAmPhone != null;
    }

    public boolean hasMeans() {
        return mMeans != null;
    }


}
