package com.example.myweather;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetCityNameList {
    private OkHttpClient client = new OkHttpClient();
    private Handler myHandle;

    public void GetCityNameList(String city){

    }
    @SuppressLint("HandlerLeak")
    public ArrayList<HashMap<String,Object>> getCityList(String city){
        getCity(city);
        final ArrayList<HashMap<String,Object>> list=new ArrayList<>();
        myHandle = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                String jsonStr=bundle.getString("jsonStr");
                try {
                    JSONObject obj = new JSONObject(jsonStr);//拿到json对象
                    JSONArray jsonArray= obj.getJSONArray("HeWeather6").getJSONObject(0).getJSONArray("basic");
                    //JSONObject obj_day1 = jsonArray.getJSONObject(1);
                    for(int i=0;i<jsonArray.length();i++){
                        HashMap<String,Object> item= new HashMap<>();
                        JSONObject json_item = jsonArray.getJSONObject(i);
                        item.put("city",json_item.getString("location")+","+json_item.getString("admin_area"));
                        list.add(item);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
         return list;
    }
    public void getCity(String city){
        final Request[] request = {new Request.Builder().url("https://search.heweather.net/find?key=a99a512784ca44f195fc5b02ef285f9f&number=100&location="+city).build()};
        client.newCall(request[0]).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.v("okhttp","打开失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                // Log.v("okhttp","打开成功");

                if(response.isSuccessful()){
                    final String[] myText = {""};
                    //Log.v("okhttp",response.body().string());
                    try{
                        //weatherObj = new JSONObject(response.body().string());//先尝试得到个json对象
                        String jsonStr=response.body().string();
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("jsonStr",jsonStr);
                        msg.setData(bundle);
                        myHandle.sendMessage(msg);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
