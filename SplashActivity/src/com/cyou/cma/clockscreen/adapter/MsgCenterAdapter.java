
package com.cyou.cma.clockscreen.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.Constants;
import com.cyou.cma.clockscreen.bean.MsgCenterBean;
import com.cyou.cma.clockscreen.util.Util;

import java.util.List;

public class MsgCenterAdapter extends BaseAdapter {

    private Context mContext;
    private List<MsgCenterBean> mList;
    private DataObserver mCallBack;

    private int height = 116;

    public MsgCenterAdapter(Context context, List<MsgCenterBean> list, DataObserver callBack) {
        this.mContext = context;
        this.mList = list;
        this.mCallBack = callBack;
        height = (int) ((116 / 1280f) * Util.getScreenHeight(mContext));
    }

    @Override
    public int getCount() {
        if (mList == null)
            return 0;
        return mList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    public int getType(int position) {
        return mList.get(position).getType();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {
        MsgCenterBean msgBean = mList.get(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.adapter_msgcenter,
                    null);
            viewHolder.icon = (ImageView) convertView
                    .findViewById(R.id.adapter_msgcenter_icon);
            viewHolder.title = (TextView) convertView
                    .findViewById(R.id.adapter_msgcenter_title);
            viewHolder.content = (TextView) convertView
                    .findViewById(R.id.adapter_msgcenter_content);
            viewHolder.icon.getLayoutParams().height = height;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String name = mContext.getString(R.string.msgcenter_tip_content);
        int iconResId = R.drawable.icon_msgcenter_tip;
        String title = mContext.getString(R.string.msgcenter_tip_title);
        switch (msgBean.getType()) {
            case Constants.MSGCENTER_TYPE_SMS:
                iconResId = R.drawable.icon_msgcenter_sms;
                title = mContext.getString(R.string.msgcenter_sms_title);
                name = mContext.getString(msgBean.getCount() > 1 ? R.string.msgcenter_sms_contents
                        : R.string.msgcenter_sms_content, msgBean.getCount());
                break;
            case Constants.MSGCENTER_TYPE_CALL:
                iconResId = R.drawable.icon_msgcenter_call;
                title = mContext.getString(R.string.msgcenter_call_title);
                name = mContext.getString(msgBean.getCount() > 1 ? R.string.msgcenter_call_contents
                        : R.string.msgcenter_call_content, msgBean.getCount());
                break;
            case Constants.MSGCENTER_TYPE_TIP:
                iconResId = R.drawable.icon_msgcenter_tip;
                title = mContext.getString(R.string.msgcenter_tip_title);
                name = mContext.getString(R.string.msgcenter_tip_content);
                break;
        }
        viewHolder.title.setText(title);
        viewHolder.content.setText(name);
        viewHolder.icon.setImageResource(iconResId);
        return convertView;
    }

    private class ViewHolder {
        TextView content;
        TextView title;
        ImageView icon;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (mCallBack != null) {
            mCallBack.onNotifyDataSetChanged(mList == null ? 0 : mList.size());
        }
    }

    public interface DataObserver {
        public void onNotifyDataSetChanged(int count);
    }

}
