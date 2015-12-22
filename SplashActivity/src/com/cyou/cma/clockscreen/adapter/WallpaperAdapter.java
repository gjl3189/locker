package com.cyou.cma.clockscreen.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.bean.WallpaperBean;
import com.cyou.cma.clockscreen.sqlite.ProviderHelper;
import com.cyou.cma.clockscreen.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.List;

public class WallpaperAdapter extends BaseAdapter {
    private Context mContext;
    private List<WallpaperBean> list;

    private int height;
    private boolean isEditMode = false;
    private DisplayImageOptions options;
    private String mPackageName;

    public WallpaperAdapter(Context context, List<WallpaperBean> list,
            String packageName) {
        this.mContext = context;
        this.list = list;
        height = (int) ((3f / 2f)
                * Util.getPreferenceInt(mContext, Util.SAVE_KEY_SCREEN_WIDTH,
                        720) / 2f);
        this.mPackageName = packageName;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading_thumb)
                .showImageOnFail(R.drawable.loading_failed).cacheInMemory(true)
                .cacheOnDisk(false).bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).build();
    }

    @Override
    public int getCount() {
        if (isEditMode) {
            return list.size();
        } else {
            return list.size() + 1;
        }
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {
        if (position == list.size()) {
            View view = View.inflate(mContext,
                    R.layout.adapter_wallpaper_additem, null);
            view.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    height));
            return view;
        } else {
            ViewHolder viewHolder = null;
            WallpaperBean bean = list.get(position);
            if (convertView == null || convertView.getTag() == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext,
                        R.layout.adapter_wallpaper, null);
                viewHolder.indicate = (ImageView) convertView
                        .findViewById(R.id.adapter_wallpaper_indicate);
                viewHolder.selection = (ImageView) convertView
                        .findViewById(R.id.adapter_wallpaper_selection);
                viewHolder.imageView = (ImageView) convertView
                        .findViewById(R.id.adapter_wallpaper_image);
                viewHolder.imageView.getLayoutParams().height = height;
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.selection
                    .setVisibility((isEditMode && (bean.getIsDefault() != 1 && bean
                            .getIsProvide() != 1)) ? View.VISIBLE : View.GONE);
            viewHolder.selection.setSelected(bean.isSelected());

            String defaultPath = ProviderHelper.getWallpaperPath(
                    mContext, mPackageName);

            viewHolder.indicate.setVisibility(bean.getWallpaperPath().equals(
                    defaultPath) ? View.VISIBLE : View.GONE);

            ImageLoader.getInstance().displayImage(
                    "file://" + bean.getThumbPath(), viewHolder.imageView,
                    options,null);
            return convertView;
        }
    }

    private class ViewHolder {
        ImageView imageView;
        ImageView indicate;
        ImageView selection;
    }

    public void setEditMode(boolean isEdit) {
        this.isEditMode = isEdit;
    }

}
