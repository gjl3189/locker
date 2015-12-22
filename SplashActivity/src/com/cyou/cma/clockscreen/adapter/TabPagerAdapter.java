/**
 * 所属项目：	Launcher
 * 文件名：	TabPagerAdapter.java
 * 创建时间：	2013-11-13 下午5:06:48
 * 创建人：	xuchunlei
 */

package com.cyou.cma.clockscreen.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * 类名: 片断页适配器 描述: 创建时间: 2013-11-13下午5:06:48
 * 
 * @author xuchunlei
 */
public class TabPagerAdapter extends FragmentPagerAdapter {

    // Fragment列表数据
    private List<Fragment> mData;

    public TabPagerAdapter(FragmentManager fm, List<Fragment> data) {
        super(fm);
        mData = data;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
     */
    @Override
    public Fragment getItem(int position) {
        return mData.get(position);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.view.PagerAdapter#getCount()
     */
    @Override
    public int getCount() {
        return mData.size();
    }

}
