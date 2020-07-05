package com.example.myweather;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MyManageCityAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;//布局填充器
    private int currentPos;
    ArrayList<HashMap<String,Object>> listItem;
    public MyManageCityAdapter(Context context, ArrayList<HashMap<String,Object>> listItem){
        this.mLayoutInflater = LayoutInflater.from(context);
        this.listItem=listItem;
    }
    @Override
    public int getCount(){
        return listItem.size();//返回adapter中数据集合的条数
    }
    @Override
    public Object getItem(int position){
        return listItem.get(position);//返回指定位置的数据项
    }
    @Override
    public long getItemId(int position) {
        return position;//这个方法返回了在列表中与指定索引对应的行id
    }
    static class ViewHolder  //利用convertView+ViewHolder来重写getView()
    {
        private TextView tv_city;
        private TextView tv_temperature_city;
        private TextView tv_weather_city;
        private ImageButton img_btn_delete;
        private LinearLayout lly;
        //private LinearLayout linearLayoutMain;
    }
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent){
        ViewHolder holder ;
        if(convertView == null)
        {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.list_item_magnage_city,null);
            holder.tv_city=convertView.findViewById(R.id.tv_city);
            holder.tv_temperature_city=convertView.findViewById(R.id.tv_temperature_city);
            holder.tv_weather_city=convertView.findViewById(R.id.tv_weather_city);
            holder.img_btn_delete=convertView.findViewById(R.id.img_btn_delete);
            holder.lly=convertView.findViewById(R.id.lly_inside);
            //holder.linearLayoutMain=convertView.findViewById(R.id.linearLayoutMain);
            convertView.setTag(holder);
        }
        else
        {
            holder=(ViewHolder)convertView.getTag();
        }
        final String weather=(String)listItem.get(position).get("weather");
        final String cityName=(String)listItem.get(position).get("city");
        holder.tv_city.setText(cityName);
        holder.tv_temperature_city.setText((String)listItem.get(position).get("temperature")+"℃");
        holder.tv_weather_city.setText(weather);
        holder.img_btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new AlertDialog.Builder(v.getContext()).
                        setTitle("确认删除这个城市吗？")
                        .setMessage("您将要删除"+cityName)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String deleted_city=listItem.get(position).get("city").toString();//找到删除的城市名称
                                List<String> city_list;//找到城市数组
                                List<String> city_w_t_list;
                                city_list=getList("city");
                                city_w_t_list=getList("city_w_t");
                                for(int i=0;i<city_list.size();i++){
                                    if(city_list.get(i).contains(deleted_city)){
                                        city_list.remove(i);
                                        city_w_t_list.remove(i);
                                        listItem.remove(i);
                                        break;
                                    }
                                }
                                putList("city",city_list);//写入
                                putList("city_w_t",city_list);//写入
                                //debug先找到当前list是什么内容，然后读取修改，再提交shared的真数据源
                                MyManageCityAdapter.this.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
            }
        });
        assert weather != null;
        if(weather.equals("中雨")||weather.equals("大雨")||weather.equals("小雨")||weather.equals("暴雨")){
            holder.lly.setBackgroundResource(R.drawable.bg_rainy);
            //holder.linearLayoutMain.setBackgroundResource(R.mipmap.act_bg_rainy);
        }
        else if(weather.equals("晴"))
        {
            holder.lly.setBackgroundResource(R.drawable.bg_sunny);
            //holder.linearLayoutMain.setBackgroundResource(R.mipmap.ac_bg_sunny);
        }
        else if(weather.equals("阴")||weather.equals("多云"))
        {
            holder.lly.setBackgroundResource(R.drawable.bg_cloudy);
           // holder.linearLayoutMain.setBackgroundResource(R.mipmap.act_bg_cloudy);
        }
        else {
            holder.lly.setBackgroundResource(R.drawable.bg_sunny);
           // holder.linearLayoutMain.setBackgroundResource(R.mipmap.ac_bg_sunny);
        }
        return convertView;
    }
    private void putList(String key, List<String> list) {
        SharedPreferences preferences = Objects.requireNonNull(mLayoutInflater.getContext()).getSharedPreferences("mySharedCity", Context.MODE_PRIVATE);
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

        SharedPreferences preferences = Objects.requireNonNull(mLayoutInflater.getContext()).getSharedPreferences("mySharedCity",Context.MODE_PRIVATE);
        int size = preferences.getInt(key + "_size", 0);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String city = preferences.getString(key + i, "");
            list.add(city);
        }
        return list;
    }
}
