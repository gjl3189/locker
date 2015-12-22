package com.cyou.cma.clockscreen.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.bean.Theme4Play;
import com.cyou.cma.clockscreen.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class OnlineThemeDetailPagerAdapter extends PagerAdapter {

    private Theme4Play mLockInfo;
    Context mContext;
    LayoutInflater mLayoutInflater;
    private ImageLoader mImageLoader;
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.loading_thumb)
            .showImageOnFail(R.drawable.loading_failed)
            .bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(true)
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).build();

    public OnlineThemeDetailPagerAdapter(Context context, Theme4Play lockInfo) {
        mLockInfo = lockInfo;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context.getApplicationContext());
        mImageLoader = ImageLoader.getInstance();

    }

    @Override
    public int getCount() {
        return mLockInfo.previews.size() + 1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        boolean isViewFromObject = (view == object);
        Util.Logjb("localThemeAdapter", "localThemeAdapter isViewFromObject -->" + isViewFromObject);
        return isViewFromObject;
    }

    @SuppressLint("NewApi")
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Util.Logjb("localThemeAdapter", "localThemeAdapter instantiateItem position-->" + position);
        if (position == 0) {
            return inflateAboutView(container, position);
        } else {
            return inflateImageView(container, position);
        }

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Util.Logjb("localThemeAdapter", "localThemeAdapter destroyItem position-->" + position);
        if (position == 0) {
            ((ViewPager) container).removeView((View) object);
        } else {
            ((ViewPager) container).removeView((RelativeLayout) object);

        }
    }

    @SuppressLint("NewApi")
    public View inflateImageView(ViewGroup container, int position) {

        RelativeLayout relativeLayout = (RelativeLayout) mLayoutInflater
                .inflate(R.layout.lock_imageview, null);
        ImageView imageView = (ImageView) relativeLayout
                .findViewById(R.id.imageView);

        mImageLoader.displayImage(mLockInfo.previews.get(position - 1).getUrl(),
                imageView,
                options, null);
        if (position != 1) {
            if (Build.VERSION.SDK_INT >= 11) {
                relativeLayout.setScaleY(0.936f);
                relativeLayout.findViewById(R.id.view_foreground)
                        .setVisibility(View.VISIBLE);
            }

        }
        ((ViewPager) container).addView(relativeLayout, 0, null);

        return relativeLayout;

    }

    /**
     * 创建主题介绍页面
     * 
     * @param container
     * @param position
     * @return
     */
    @SuppressLint("NewApi")
    public View inflateAboutView(ViewGroup container, int position) {
        ViewHolder viewHolder = new ViewHolder();
        View view = mLayoutInflater.inflate(R.layout.theme_about, null);
        viewHolder.textViewDesigner = (TextView) view
                .findViewById(R.id.textview_designer);

        viewHolder.textViewIntroduction = (TextView) view
                .findViewById(R.id.textview_theme_introducction);
        viewHolder.textViewDate = (TextView) view
                .findViewById(R.id.textview_date);
        viewHolder.textViewSize = (TextView) view
                .findViewById(R.id.textview_size);

        viewHolder.textViewDownloadCount = (TextView) view
                .findViewById(R.id.textview_download);
        viewHolder.textViewLableDownload = (TextView) view.findViewById(R.id.lable_download);
        viewHolder.lableView = view.findViewById(R.id.lable_view);

        viewHolder.textViewDownloadCount.setVisibility(View.VISIBLE);
        viewHolder.lableView.setVisibility(View.VISIBLE);
        viewHolder.textViewLableDownload.setVisibility(View.GONE);

        viewHolder.textViewDesigner.setText(mLockInfo.author);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        String date = sdf.format(new Date(mLockInfo.auditTime));
        viewHolder.textViewDate.setText(date);
        viewHolder.textViewSize.setText(mLockInfo.sizeText);
        viewHolder.textViewDownloadCount.setVisibility(View.GONE);
        viewHolder.textViewIntroduction.setText(mLockInfo.description);
        if (Build.VERSION.SDK_INT >= 11) {
            view.setScaleY(0.936f);
            view.findViewById(R.id.view_foreground).setVisibility(View.VISIBLE);
        }
        ((ViewPager) container).addView(view, position, null);
        return view;
    }

    private static class ViewHolder {
        TextView textViewDesigner;
        TextView textViewSize;
        TextView textViewDate;
        TextView textViewIntroduction;
        TextView textViewDownloadCount;
        TextView textViewLableDownload;
        View lableView;
    }

}
