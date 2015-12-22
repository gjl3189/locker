package com.cyou.cma.clockscreen.adapter;

import android.widget.BaseAdapter;

import com.cyou.cma.clockscreen.bean.EntityType;
import com.cyou.cma.clockscreen.bean.Group;

public abstract class BaseGroupAdapater<T extends EntityType> extends
        BaseAdapter {
    protected Group<T> group;

    @Override
    public int getCount() {
        return (group == null) ? 0 : group.size();
    }

    @Override
    public T getItem(int position) {
        return group.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return (group == null) ? true : group.isEmpty();
    }

    public void setGroup(Group<T> group) {
        notifyDataSetInvalidated();
        this.group = group;

    }

}
