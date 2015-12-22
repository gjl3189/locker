package com.cyou.cma.clockscreen.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.activity.LocalThemeDetailActivity;
import com.cyou.cma.clockscreen.activity.OnlineThemeDetailActivity;
import com.cyou.cma.clockscreen.bean.InstallLocker;
import com.cyou.cma.clockscreen.bean.Theme4Play;
import com.cyou.cma.clockscreen.util.CyLvItemAnim;
import com.cyou.cma.clockscreen.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class OnlineThemeGridViewAdapter4Play extends BaseGroupAdapater<Theme4Play> implements OnClickListener {
    private Context mContext;
    private LayoutInflater mInflater;
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.loading_thumb_small)
            .showImageOnFail(R.drawable.loading_failed_small).cacheInMemory(true).cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).build();
    private int height;

    @Override
    public int getCount() {
        if (group == null || group.size() == 0)
            return 0;
        return group.size() / 2 + group.size() % 2;
    }

    public OnlineThemeGridViewAdapter4Play(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        int empty = mContext.getResources().getDimensionPixelSize(
                R.dimen.online_empty_hoizontal);
        height = (int) ((588 / 352f)
                * (Util.getPreferenceInt(mContext, Util.SAVE_KEY_SCREEN_WIDTH, 720) - empty) / 2f);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Theme4Play lockThemeLeft = getItem(position * 2);
        Theme4Play lockThemeRight = null;
        if (position * 2 + 1 <= group.size() - 1) {
            lockThemeRight = group.get(position * 2 + 1);
        }
        ViewHolder viewHolder = null;
        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.onlinelist_theme_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mLeftLayout = (RelativeLayout) convertView
                    .findViewById(R.id.online_theme_left_layout);
            viewHolder.mRightLayout = (RelativeLayout) convertView
                    .findViewById(R.id.online_theme_right_layout);
            viewHolder.imageViewThemeLeft = (ImageView) convertView
                    .findViewById(R.id.imageview_theme_left);
            viewHolder.textViewDownloadedLeft = (TextView) convertView
                    .findViewById(R.id.textview_downloaded_left);
            viewHolder.textViewNameLeft = (TextView) convertView
                    .findViewById(R.id.textView_themeName_left);

            viewHolder.imageViewThemeRight = (ImageView) convertView
                    .findViewById(R.id.imageview_theme_right);
            viewHolder.textViewDownloadedRight = (TextView) convertView
                    .findViewById(R.id.textview_downloaded_right);
            viewHolder.textViewNameRight = (TextView) convertView
                    .findViewById(R.id.textView_themeName_right);
            viewHolder.imageViewThemeLeft.getLayoutParams().height = height;
            viewHolder.imageViewThemeRight.getLayoutParams().height = height;

            convertView.setTag(viewHolder);
            Util.Logjb("getView", "getView:" + convertView);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textViewNameLeft.setHorizontallyScrolling(false);
        if (lockThemeLeft.hasDownloaded) {

            viewHolder.textViewDownloadedLeft.setVisibility(View.VISIBLE);
        } else {
            viewHolder.textViewDownloadedLeft.setVisibility(View.GONE);

        }
        ImageLoader.getInstance().displayImage(lockThemeLeft.thumbnail,
                viewHolder.imageViewThemeLeft, options, null);
        viewHolder.textViewNameLeft.setText(lockThemeLeft.title);
        viewHolder.mLeftLayout.setTag(lockThemeLeft);
        viewHolder.mLeftLayout.setOnClickListener(this);
        if (lockThemeRight == null) {
            viewHolder.mRightLayout.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.mRightLayout.setVisibility(View.VISIBLE);
            viewHolder.textViewNameRight.setHorizontallyScrolling(false);
            if (lockThemeRight.hasDownloaded) {
                ImageLoader.getInstance().displayImage(lockThemeRight.thumbnail,
                        viewHolder.imageViewThemeRight, options, null, null);
                viewHolder.textViewDownloadedRight.setVisibility(View.VISIBLE);
            } else {
                viewHolder.textViewDownloadedRight.setVisibility(View.GONE);
                ImageLoader.getInstance().displayImage(lockThemeRight.thumbnail,
                        viewHolder.imageViewThemeRight, options, null);
            }
            viewHolder.textViewNameRight.setText(lockThemeRight.title);
            viewHolder.mRightLayout.setTag(lockThemeRight);
            viewHolder.mRightLayout.setOnClickListener(this);

        }

		//add by Jack: add lvItem anim
		if(viewHolder.mLeftLayout!=null
				&&viewHolder.mRightLayout!=null
				&&lockThemeRight!=null
				&&lockThemeRight.needAnim){
			//left
	    	Animation animLeft = CyLvItemAnim.getAnim(true, viewHolder.mLeftLayout.getHeight());
	    	viewHolder.mLeftLayout.startAnimation(animLeft);
	    	//right
	    	Animation animRight = CyLvItemAnim.getAnim(false, viewHolder.mRightLayout.getHeight());
	    	viewHolder.mRightLayout.startAnimation(animRight);
	    	//total
	    	lockThemeRight.needAnim = false;
		}
    	//end
        return convertView;
    }

    static class ViewHolder {
        RelativeLayout mLeftLayout;
        RelativeLayout mRightLayout;
        ImageView imageViewThemeLeft;
        TextView textViewDownloadedLeft;
        TextView textViewNameLeft;
        ImageView imageViewThemeRight;
        TextView textViewDownloadedRight;
        TextView textViewNameRight;
    }

    @Override
    public void onClick(View v) {

        Theme4Play theme4Play = (Theme4Play) v.getTag();
        if (!theme4Play.hasDownloaded) {
            Intent intent = new Intent(mContext, OnlineThemeDetailActivity.class);
            intent.putExtra("theme4play", theme4Play);
            mContext.startActivity(intent);
        } else {
            Intent intent = new Intent(mContext, LocalThemeDetailActivity.class);
            intent.putExtra("packageName", theme4Play.packageName);
            mContext.startActivity(intent);
        }
    }

}
