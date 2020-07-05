package com.example.myweather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MyAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;//布局填充器
    ArrayList<HashMap<String,Object>> listItem;
    public MyAdapter(Context context, ArrayList<HashMap<String,Object>> listItem){
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
        public TextView location;
        public TextView parent_city;
        public TextView admin_area;
    }
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent){
        ViewHolder holder ;
        if(convertView == null)
        {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.list_item_city,null);
            holder.location=convertView.findViewById(R.id.tv_location);
            holder.parent_city=convertView.findViewById(R.id.tv_parent_city);
            holder.admin_area=convertView.findViewById(R.id.tv_admin_area);
            convertView.setTag(holder);
        }
        else
        {
            holder=(ViewHolder)convertView.getTag();
        }
        holder.location.setText((String)listItem.get(position).get("location"));
        holder.parent_city.setText((String)listItem.get(position).get("parent_city."));
        holder.admin_area.setText((String)listItem.get(position).get("admin_area"));
        return convertView;
    }
}
