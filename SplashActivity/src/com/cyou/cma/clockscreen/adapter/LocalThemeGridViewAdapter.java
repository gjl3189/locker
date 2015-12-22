package com.cyou.cma.clockscreen.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.bean.InstallLocker;
import com.cyou.cma.clockscreen.util.CyLvItemAnim;
import com.cyou.cma.clockscreen.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

public class LocalThemeGridViewAdapter extends BaseGroupAdapater<InstallLocker> {
    private Context mContext;
    private LayoutInflater mInflater;
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.loading_thumb_small)
            .showImageOnFail(R.drawable.loading_failed_small).cacheInMemory(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).build();
    private int height;
    private ImageLoader mImageLoader;

    public LocalThemeGridViewAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        int empty = mContext.getResources().getDimensionPixelSize(
                R.dimen.online_empty_hoizontal);
        height = (int) ((588 / 352f)
                * (Util.getPreferenceInt(mContext, Util.SAVE_KEY_SCREEN_WIDTH, 720) - empty) / 2f);
        mImageLoader = ImageLoader.getInstance();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.locallist_theme_item, null);
            viewHolder = new ViewHolder();
            viewHolder.imageViewTheme = (ImageView) convertView.findViewById(R.id.imageview_theme);
            viewHolder.imageViewUsing = (ImageView) convertView.findViewById(R.id.imageView_using);
            viewHolder.textViewName = (TextView) convertView.findViewById(R.id.textView_themeName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        InstallLocker lockInfo = getItem(position);
        mImageLoader
                .displayImage(
                        ImageDownloader.Scheme.ASSETS.wrap("thumb/" + lockInfo.packageName + ".jpg"),
                        viewHolder.imageViewTheme, options, lockInfo.context);
        if (lockInfo.currentTheme) {
            viewHolder.imageViewUsing.setVisibility(View.VISIBLE);
        } else {
            viewHolder.imageViewUsing.setVisibility(View.GONE);
        }
        viewHolder.textViewName.setText(lockInfo.label);
        viewHolder.textViewName.setHorizontallyScrolling(false);
        viewHolder.imageViewTheme.getLayoutParams().height = height;
        viewHolder.imageViewUsing.getLayoutParams().height = height;

		//add by Jack: add lvItem anim
		if(convertView!=null&&lockInfo!=null&&lockInfo.needAnim){
	    	Animation anim = CyLvItemAnim.getAnim(position%2!=0, convertView.getHeight());
	    	convertView.startAnimation(anim);
			lockInfo.needAnim = false;
		}
    	//end
		
        return convertView;
    }

    static class ViewHolder {
        ImageView imageViewTheme;
        ImageView imageViewUsing;
        TextView textViewName;
    }

}
