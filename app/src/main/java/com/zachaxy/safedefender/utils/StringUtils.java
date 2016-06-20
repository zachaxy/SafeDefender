package com.zachaxy.safedefender.utils;

import android.util.Log;

import com.zachaxy.safedefender.bean.UpdateInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by zhangxin on 2016/6/18.
 */
public class StringUtils {
    /***
     * 将输入流读取成字符串后返回
     *
     * @param inputStream
     * @return
     */
    public static String readFromStream(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder updateInfo = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                updateInfo.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return updateInfo.toString();
    }

    public static UpdateInfo parseJsonWithJSONObject(String jsonData) throws JSONException {
        UpdateInfo updateInfo = null;

        /*JSONArray jsonArray = new JSONArray(jsonData);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String versionName = jsonObject.getString("versionName");
            int versionCode = jsonObject.getInt("versionCode");
            String description = jsonObject.getString("description");
            String downloadUrl = jsonObject.getString("downloadUrl");
            updateInfo = new UpdateInfo(versionName, versionCode, description, downloadUrl);
            Log.d("###", "parseJsonWithJSONObject: " + versionName + "---" + versionCode + "---" + description + "---" + downloadUrl);
        }*/
        JSONObject jsonObject = new JSONObject(jsonData);
        String versionName = jsonObject.getString("versionName");
        int versionCode = jsonObject.getInt("versionCode");
        String description = jsonObject.getString("description");
        String downloadUrl = jsonObject.getString("downloadUrl");
        updateInfo = new UpdateInfo(versionName, versionCode, description, downloadUrl);
        Log.d("###", "parseJsonWithJSONObject: " + versionName + "---" + versionCode + "---" + description + "---" + downloadUrl);

    return updateInfo;
}

}
