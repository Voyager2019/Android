package com.example.myweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CityManageActivity extends AppCompatActivity {

    private ImageButton img_btn_add;
    private ImageButton img_btn_back;
    private ListView lv_manage;//要显示的list
    private ArrayList<HashMap<String,Object>> listItem;
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
        setContentView(R.layout.activity_city_manage);
        lv_manage=findViewById(R.id.list_manage_city);
        listItem=new ArrayList<>();
        List<String> city_w_t_list;
        city_w_t_list=getList("city_w_t");//找到当前管理的城市信息
        String[] strings;
        for(int i=0;i<city_w_t_list.size();i++){
            HashMap<String,Object> item= new HashMap<>();
            strings=city_w_t_list.get(i).split("/");//慈溪/24/晴天
            item.put("city",strings[0]);
            item.put("temperature",strings[1]);
            item.put("weather",strings[2]);
            listItem.add(item);//listview的数据
        }
        img_btn_add=findViewById(R.id.img_btn_add);
        img_btn_back=findViewById(R.id.img_btn_back);
        img_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();//CityManageActivity.this,MainActivity.class
                setResult(1,intent);
                finish();
            }
        });
        img_btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CityManageActivity.this,CityAddActivity.class);
                startActivity(intent);
                finish();
            }
        });
        MyManageCityAdapter myManageCityAdapter = new MyManageCityAdapter(CityManageActivity.this,listItem);
        lv_manage.setAdapter(myManageCityAdapter);
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
}
