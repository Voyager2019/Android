package com.example.myweather;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
public class CityAddActivity extends AppCompatActivity {

    private ListView listView;
    private MyAdapter myAdapter;
    //private ArrayList<HashMap<String,Object>> listItem;
    //private ArrayList<HashMap<String,Object>> listItem_show;
    OkHttpClient client = new OkHttpClient();
    private ArrayList<HashMap<String,Object>> listItems = new ArrayList<>();
    //private ArrayList<HashMap<String,Object>> listItem_save;
    private EditText search_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_city_add);
        //initList();//得到数据
        //myAdapter = new MyAdapter(this.getApplicationContext(),listItem);
        listView=findViewById(R.id.list_search_city);
        search_et=findViewById(R.id.search_et);
        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input=s.toString();
                Request request = new Request.Builder().url("https://search.heweather.net/find?key=a99a512784ca44f195fc5b02ef285f9f&number=5&location="+input).build();
                client.newCall(request).enqueue(new okhttp3.Callback(){
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.v("okhttp","打开失败!");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException{
                        if(response.isSuccessful()){
                            String str = response.body().string();
                            Log.v("okhttp",str);
                            String result="" ;
                            try {
                                //listView.setVisibility(View.VISIBLE);
                                JSONObject json = new JSONObject(str);
                                JSONArray cityarray = json.getJSONArray("HeWeather6").getJSONObject(0).getJSONArray("basic");
                                HashMap<String,Object> item;
                                listItems.clear();
                                for(int i=0;i<cityarray.length();i++){
                                    JSONObject jsonObject = cityarray.getJSONObject(i);
                                    item  = new HashMap<>();
                                    item.put("location",jsonObject.getString("location"));
                                    item.put("parent_city",jsonObject.getString("parent_city"));
                                    item.put("admin_area",jsonObject.getString("admin_area"));
                                    listItems.add(item);
                                }
                                Message msg = new Message();
                                mHandler.sendMessage(msg);
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input=search_et.getText().toString();
                if(input.equals("")){
                    //listView.setVisibility(View.INVISIBLE);//不显示listview
                    listItems.clear();
                    myAdapter.notifyDataSetChanged();
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String,Object> item = (HashMap<String,Object>)parent.getItemAtPosition(position);
                List<String> city_list ;//新建一个list
                List<String> city_w_t_list ;
                city_list=getList("city");//尝试去读取当前添加了几个城市
                city_w_t_list=getList("city_w_t");//读取当前的manage城市信息
                if(city_list.size()==0){
                    if(item.get("location").toString().length()>2){
                        city_list.add(item.get("parent_city").toString());//list插入这个城市
                        city_w_t_list.add(item.get("parent_city").toString());
                    }
                    else {
                        city_list.add(item.get("location").toString());//list插入这个城市
                        city_w_t_list.add(item.get("location").toString());
                    }
                    putList("city",city_list);
                    putList("city_w_t",city_w_t_list);
                }
                else {
                    for(int i=0;i<city_list.size();i++){//如果城市数大于0
                        if((item.get("location").toString()).length()>2){//判断是否杂毛地区
                            if(city_list.get(i).contains(item.get("parent_city").toString())){//如果有这个城市
                                break;//退出啥都不做
                            }
                            else {
                                city_list.add(item.get("parent_city").toString());//list插入这个城市
                                city_w_t_list.add(item.get("parent_city").toString());
                                Collections.reverse(city_list);
                                Collections.reverse(city_w_t_list);
                                putList("city",city_list);
                                putList("city_w_t",city_w_t_list);
                                break;
                            }
                        }
                        else {
                            if(city_list.contains(item.get("location").toString())){//如果有这个城市
                                break;
                            }
                            else {
                                city_list.add(item.get("location").toString());//list插入这个城市
                                city_w_t_list.add(item.get("location").toString());
                                Collections.reverse(city_list);
                                Collections.reverse(city_w_t_list);
                                putList("city",city_list);
                                putList("city_w_t",city_w_t_list);
                            }
                        }
                    }
                }
                //Intent intent = new Intent(CityAddActivity.this,MainActivity.class);
                //startActivity(intent);
                //finish();
                Intent intent = new Intent();//CityManageActivity.this,MainActivity.class
                setResult(1,intent);
                finish();
            }
        });
    }
    private void putList(String key, List<String> list) {
        SharedPreferences preferences = getSharedPreferences("mySharedCity",MODE_PRIVATE);
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

        SharedPreferences preferences = getSharedPreferences("mySharedCity",MODE_PRIVATE);
        int size = preferences.getInt(key + "_size", 0);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String city = preferences.getString(key + i, "");
            list.add(city);
        }
        return list;
    }
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            ListView getcity = findViewById(R.id.list_search_city);
            myAdapter = new MyAdapter(CityAddActivity.this,listItems);
            getcity.setAdapter(myAdapter);
            myAdapter.notifyDataSetChanged();
        }
    };
}
