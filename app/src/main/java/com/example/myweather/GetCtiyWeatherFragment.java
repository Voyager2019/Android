package com.example.myweather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetCtiyWeatherFragment extends Fragment {
    private String cityname;
    private Handler myHandle;
    private TextView tv_city_name;
    private TextView tv_temperature;
    private TextView tv_max_min_temperature;
    private TextView tv_weather;
    private TextView tv_flu;
    private TextView tv_wind_lv;
    private TextView tv_wind_direction;
    private TextView tv_aqi;
    private ListView lv_5day;
    private LinearLayout lly_outside;
    private int wind_lv;
    private My5DayAdapter my5DayAdapter ;
    private ArrayList<HashMap<String,Object>> listItem;
    private OkHttpClient client = new OkHttpClient();
    private Windmill windmill_big;
    private Windmill windmill_small;


    public GetCtiyWeatherFragment(String city){
        this.cityname=city;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)  {
        View view = inflater.inflate(R.layout.page_layout,container,false);
        tv_city_name=view.findViewById(R.id.tv_city_name);
        tv_temperature=view.findViewById(R.id.tv_temperature);
        tv_max_min_temperature=view.findViewById(R.id.tv_max_min_temperature);
        tv_weather=view.findViewById(R.id.tv_weather);
        tv_aqi=view.findViewById(R.id.tv_aqi);
        tv_wind_lv=view.findViewById(R.id.tv_wind_lv);
        tv_flu=view.findViewById(R.id.tv_flu);
        tv_wind_direction=view.findViewById(R.id.tv_wind_direction);
        lv_5day=view.findViewById(R.id.lv_5day);
        lly_outside=view.findViewById(R.id.lly_outside);
        //LayoutInflater factory = LayoutInflater.from(getContext());
        //View layout = factory.inflate(R.layout.activity_main, null);

        return view;
    }
    @SuppressLint("HandlerLeak")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getWeather(cityname);
        windmill_big = view.findViewById(R.id.windmill_big);
        windmill_small =view.findViewById(R.id.windmill_small);
        myHandle = new Handler(){
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                String jsonStr=bundle.getString("jsonStr");
                try {
                    listItem=new ArrayList<>();
                    JSONObject obj = new JSONObject(jsonStr);//拿到json对象
                    JSONArray jsonArray= obj.getJSONObject("data").getJSONArray("forecast");//找到5天的内容
                    for(int i=0;i<jsonArray.length();i++){
                        HashMap<String,Object> item= new HashMap<>();
                        JSONObject json_item = jsonArray.getJSONObject(i);
                        item.put("date",json_item.get("date"));//adapter里get的时候的用到的名字
                        item.put("max_min",json_item.get("high").toString().replace("高温","")+"/"+json_item.get("low").toString().replace("低温",""));
                        item.put("type",json_item.get("type"));
                        listItem.add(item);
                    }
                    my5DayAdapter = new My5DayAdapter(getContext(),listItem);
                    lv_5day.setAdapter(my5DayAdapter);//显示5天天气
                    JSONObject obj_day1 = jsonArray.getJSONObject(1);
                    String today_weather=obj_day1.getString("type");//妈的应该listview的，下次一定
                    String objCity=obj.getJSONObject("data").getString("city");
                    wind_lv=Integer.parseInt(obj_day1.getString("fengli").replace("<![CDATA[","").replace("]]>","").replace("级","").replace("~",""));
                    tv_city_name            .setText(objCity);
                    tv_temperature          .setText(obj.getJSONObject("data").getString("wendu"));
                    tv_weather              .setText(today_weather);
                    tv_flu                  .setText(obj.getJSONObject("data").getString("ganmao"));
                    tv_max_min_temperature  .setText(obj.getJSONObject("data").getJSONArray("forecast").getJSONObject(0).getString("high")+"/"+obj.getJSONObject("data").getJSONArray("forecast").getJSONObject(0).getString("low"));
                    tv_aqi                  .setText("空气优");
                    tv_wind_direction       .setText(obj_day1.getString("fengxiang"));
                    tv_wind_lv              .setText(wind_lv +"级");
                    windmill_small          .setWindSpeed(wind_lv);//找错view了可能
                    windmill_big            .setWindSpeed(wind_lv*3);
                    if(today_weather.equals("阴")||today_weather.equals("多云")){
                        lly_outside.setBackgroundResource(R.mipmap.act_bg_cloudy);
                    }
                    else if(today_weather.equals("晴")){
                        lly_outside.setBackgroundResource(R.mipmap.ac_bg_sunny);
                    }
                    else if(today_weather.equals("小雨")||today_weather.equals("中雨")||today_weather.equals("大雨")||today_weather.equals("暴雨")){
                        lly_outside.setBackgroundResource(R.mipmap.act_bg_rainy);
                    }
                    else {
                        lly_outside.setBackgroundResource(R.mipmap.act_bg_cloudy);
                    }
                    List<String> city_list=new ArrayList<>(getList("city"));//得到当前城市list;
                    if(city_list.size()!=0){
                        List<String> city_weather_t_list = new ArrayList<>(getList("city_w_t"));//debug c_w_t=list,cwt找到的城市下标
                        for(int i=0;i<city_weather_t_list.size();i++){
                            if(city_weather_t_list.get(i).contains(objCity)){
                                city_weather_t_list.set(i,objCity+"/"+obj.getJSONObject("data").getString("wendu")+"/"+today_weather);
                            }
                        }
                        putList("city_w_t",city_weather_t_list);//第一次进去可以的，正常刷新数据
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        super.onViewCreated(view, savedInstanceState);
    }
    public void getWeather(String city){
        final Request[] request = {new Request.Builder().url("http://wthrcdn.etouch.cn/weather_mini?city="+city).build()};
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
    private void putList(String key, List<String> list) {
        SharedPreferences preferences = Objects.requireNonNull(getContext()).getSharedPreferences("mySharedCity", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        //保存集合的size大小
        editor.putInt(key + "_size", list.size());

        for (int i = 0; i < list.size(); i++) {
            //去掉旧的数据
            editor.remove(key + i);
            //添加新的数据
            editor.putString(key + i, list.get(i));
        }
        editor.commit();
    }
    private List<String> getList(String key) {//传入listkey值

        SharedPreferences preferences = Objects.requireNonNull(getContext()).getSharedPreferences("mySharedCity",Context.MODE_PRIVATE);
        int size = preferences.getInt(key + "_size", 0);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String city = preferences.getString(key + i, "");
            list.add(city);
        }
        return list;
    }
    @Override
    public void onResume() {
        super.onResume();
        windmill_big.startAnimation();
        windmill_small.startAnimation();
    }

    @Override
    public void onPause() {
        super.onPause();
        windmill_big.clearAnimation();
        windmill_small.clearAnimation();
    }
}

