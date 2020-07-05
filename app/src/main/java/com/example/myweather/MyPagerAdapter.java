package com.example.myweather;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;
    private FragmentManager mfrangmentManager;
    public MyPagerAdapter(@NonNull FragmentManager fm,List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList=fragmentList;
        this.mfrangmentManager=fm;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
    public  void  clear(ViewPager viewPager){
        if (viewPager!=null&&viewPager.getAdapter() != null) {
            //获取FragmentManager实现类的class对象,这里指的就是FragmentManagerImpl
            Class<? extends FragmentManager> aClass = mfrangmentManager.getClass();
            try {
                //1.获取其mAdded字段
                Field f = aClass.getDeclaredField("mAdded");
                f.setAccessible(true);
                //强转成ArrayList
                ArrayList<Fragment> list = (ArrayList) f.get(mfrangmentManager);
                //清空缓存
                list.clear();

                //2.获取mActive字段
                f = aClass.getDeclaredField("mActive");
                f.setAccessible(true);
                //强转成SparseArray
                HashMap<String,Fragment> array  = (HashMap<String, Fragment>) f.get(mfrangmentManager);
                //清空缓存
                array.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
