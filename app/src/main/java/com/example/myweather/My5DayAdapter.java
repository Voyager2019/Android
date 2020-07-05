package com.example.myweather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class My5DayAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;//布局填充器
    ArrayList<HashMap<String,Object>> listItem;
    public My5DayAdapter(Context context, ArrayList<HashMap<String,Object>> listItem){
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
        public TextView tv_day1;
        public TextView tv_max_min;
        public ImageView img_day1;
    }
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent){
        ViewHolder holder ;
        if(convertView == null)
        {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.list_item_index,null);
            holder.tv_day1=convertView.findViewById(R.id.tv_day1);
            holder.img_day1=convertView.findViewById(R.id.img_day1);
            holder.tv_max_min=convertView.findViewById(R.id.tv_max_min_temperature_day1);
            convertView.setTag(holder);
        }
        else
        {
            holder=(ViewHolder)convertView.getTag();
        }
        holder.tv_day1.setText((String)listItem.get(position).get("date"));
        String day_weather=(String)listItem.get(position).get("type");
        holder.tv_max_min.setText((String)listItem.get(position).get("max_min"));
        if(day_weather.equals("小雨")){
            holder.img_day1.setImageResource(R.drawable.rain_lv1);
        }
        else if(day_weather.equals("中雨")){
            holder.img_day1.setImageResource(R.drawable.rain_lv2);
        }
        else if(day_weather.equals("大雨")){
            holder.img_day1.setImageResource(R.drawable.rain_lv3);
        }
        else if(day_weather.equals("暴雨")){
            holder.img_day1.setImageResource(R.drawable.rain_lv4);
        }
        else if(day_weather.equals("多云")){
            holder.img_day1.setImageResource(R.drawable.cloudy);
        }
        else if(day_weather.equals("晴天")){
            holder.img_day1.setImageResource(R.drawable.sunny);
        }
        else {
            holder.img_day1.setImageResource(R.drawable.sunny);
        }
        return convertView;
    }
}
