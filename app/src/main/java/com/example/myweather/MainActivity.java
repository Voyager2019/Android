package com.example.myweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private PopupMenu popupMenu;
    private List<Fragment> viewList;
    private Button btn;
    private ImageButton imgbtn;
    private ViewPager mViewPage;
    private boolean firstLanuch = false;
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

        setContentView(R.layout.activity_main);
        LayoutInflater inflater = getLayoutInflater().from(MainActivity.this);
        viewList = new ArrayList<>();// 将要分页显示的View装入数组中
        String[] cityStr = new String[0];
        List<String> city_list;
        city_list=getList("city");
        if(city_list.size()==0){
            viewList.add(new GetCtiyWeatherFragment("慈溪"));
        }
        else {
            for(int i=0;i<city_list.size();i++){
                viewList.add(new GetCtiyWeatherFragment(city_list.get(i)));
            }
        }
        mViewPage=findViewById(R.id.viewPage);
        imgbtn=findViewById(R.id.img_btn_menu);
        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu=new PopupMenu(MainActivity.this,v);//右上角按钮的事件
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.setCity:
                                Intent intent = new Intent(MainActivity.this,CityManageActivity.class);
                                int REQUEST_CODE = 1;
                                startActivityForResult(intent,REQUEST_CODE);
                                break;
                            case R.id.setGap:
                                Intent intents = new Intent(MainActivity.this,AboutActivity.class);
                                startActivity(intents);
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(),viewList);
        mViewPage.setAdapter(pagerAdapter);
        mViewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==0||resultCode==1||resultCode==2){
            Intent intent = new Intent(MainActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
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
}
