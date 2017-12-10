package com.goghox.stieifoodorderingclerkapp.util;

import android.util.Log;

import com.goghox.stieifoodorderingclerkapp.AppConstant;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by goghox on 11/12/17.
 */

public class NetworkUtils {
    public static OkHttpClient httpClient = new OkHttpClient();
    public static final String URL_ROOT = "http://192.168.1.101:8080";
    private static String TAG = "TAG";

    public static String getOrderList() throws IOException {

        Request request = new Request.Builder()
                .url(URL_ROOT + "/order")
                .addHeader("token", AppConstant.token)
                .build();

        Response response = httpClient.newCall(request).execute();
        String json = response.body().string();
        Log.i(TAG, "onResponse: " + json);
        return json;
    }

    public static String setOrderToServed(CharSequence comboID, CharSequence pickupTime, CharSequence lockerNumber) throws IOException {
        // time time is "12:00", isn't "12:00:00"
        final String comboTime = pickupTime.toString().substring(0, 5);

        MediaType mediaType = MediaType.parse("application/json");
        String json = "{\"combo_id\": "+comboID+", \"pickup_time\": \""+comboTime+"\", \"locker_nr\": "+lockerNumber+"}";
        RequestBody body = RequestBody.create(mediaType, json);

        Request request = new Request.Builder()
                .url(URL_ROOT + "/close/order")
                .addHeader("content-type", "application/json; charset=utf-8")
                .addHeader("token", AppConstant.token)
                .put(body)
                .build();

        Response response = httpClient.newCall(request).execute();
        String resJson = response.body().string();
        Log.i(TAG, "onResponse: " + resJson);
        return resJson;
    }

    public static String getComboList() throws IOException {

        Request request = new Request.Builder()
                .url(URL_ROOT + "/combo")
                .addHeader("token", AppConstant.token)
                .build();

        Response response = httpClient.newCall(request).execute();
        String json = response.body().string();
        Log.i(TAG, "onResponse: comboList" + json);
        return json;
    }

    public static String setComboAvailable(int comboID, CharSequence comboName, double comboPrice, int combo_available) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        String json = "{" +
                "  \"combo_id\": "+comboID+"," +
                "  \"combo_name\": \""+comboName+"\"," +
                "  \"combo_price\": "+comboPrice+"," +
                "  \"combo_available\": "+combo_available+"," +
                "  \"photo\": \"\"" +
                "}";
        RequestBody body = RequestBody.create(mediaType, json);

        Request request = new Request.Builder()
                .url(URL_ROOT + "/close/combo")
                .addHeader("content-type", "application/json; charset=utf-8")
                .addHeader("token", AppConstant.token)
                .put(body)
                .build();

        Response response = httpClient.newCall(request).execute();
        String resJson = response.body().string();
        Log.i(TAG, "onResponse: " + resJson);
        return resJson;
    }

    public static String login(String username, String password) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{ \"name\": \""+username+"\",\n  \"password\": \""+password+"\" }");
        Request request = new Request.Builder()
                .url(URL_ROOT + "/auth")
                .post(body)
                .addHeader("content-type", "application/json")
                .build();

        Response response = httpClient.newCall(request).execute();
        if(response.code() == 200) {
            return response.body().source().readUtf8();
        }
        return null;
    }
}
