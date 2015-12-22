package com.cyou.cma.clockscreen.adapter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.Constants;
import com.cyou.cma.clockscreen.bean.InstallLocker;
import com.cyou.cma.clockscreen.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

public class LocalThemeDetailPagerAdapter extends PagerAdapter {

    private InstallLocker mLockInfo;
    Context mContext;
    LayoutInflater mLayoutInflater;
    private ImageLoader mImageLoader;
    private String[] previews;
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.loading_thumb)
            .showImageOnFail(R.drawable.loading_failed)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).build();

    public LocalThemeDetailPagerAdapter(Context context, InstallLocker lockInfo) {
        mLockInfo = lockInfo;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context.getApplicationContext());
        mImageLoader = ImageLoader.getInstance();
        try {
            previews = lockInfo.context.getAssets().list("preview");
            for (String preview : previews) {
                Util.Logjb("ThemeDetailAdapter", "ThemeDetailAdapter preview---->" + preview);
            }
        } catch (IOException e) {
        }

    }

// public void addPreviews(String preview) {
// previews.add(preview);
// }

    @Override
    public int getCount() {
        return previews.length + 1;
//        return 1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @SuppressLint("NewApi")
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (position == 0) {
            return inflateAboutView(container, position);
        } else {
            return inflateImageView(container, position);
        }

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
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

        mImageLoader.displayImage(ImageDownloader.Scheme.ASSETS.wrap("preview/" + previews[position - 1]),
                imageView,
                options, mLockInfo.context);
        if (position != 1) {
            if (Build.VERSION.SDK_INT >= 11) {
                relativeLayout.setScaleY(0.936f);
                relativeLayout.findViewById(R.id.view_foreground)
                        .setVisibility(View.VISIBLE);
            }else{
                relativeLayout.findViewById(R.id.view_foreground).setVisibility(View.GONE);
            }

        }
        ((ViewPager) container).addView(relativeLayout, 0, null);

        return relativeLayout;

    }

    /**
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
        String designer = "";
        String date = "";
        String size = "";
        String description = "";
        if (mLockInfo.packageName.equals(Constants.SKY_LOCKER_DEFAULT_THEME)) {
            designer = mContext.getString(R.string.about_designer);
// Formatter f = new Formatter(new StringBuilder(50), Locale.US);
// date = DateUtils.formatDateRange(mContext, f, mLockInfo.lastUpdateTime, mLockInfo.lastUpdateTime,
// DateUtils.FORMAT_SHOW_YEAR).toString();

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            date = sdf.format(new Date(mLockInfo.lastUpdateTime));
            size = mLockInfo.sizeStr;
            description = mContext.getString(R.string.about_description);
        } else {
            String defPackage = mLockInfo.packageName;
            designer = mLockInfo.context.getString(mLockInfo.context.getResources().getIdentifier(
                    "about_designer",
                    "string", defPackage));
// SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
// date=sdf.format(new Date(mLockInfo.lastUpdateTime));

            try {
                date = mLockInfo.context.getString(mLockInfo.context.getResources().getIdentifier(
                        "about_date",
                        "string", defPackage));
            } catch (Exception e) {
                date = "09/23/2014";
            }
            // Formatter f = new Formatter(new StringBuilder(50), Locale.US);
// date = DateUtils.formatDateRange(mContext, f, mLockInfo.lastUpdateTime, mLockInfo.lastUpdateTime,
// DateUtils.FORMAT_SHOW_YEAR).toString();
// date = DateUtils
// .formatDateTime(mContext, mLockInfo.lastUpdateTime, DateUtils.FORMAT_SHOW_YEAR);
// size = mLockInfo.context.getString(mLockInfo.context.getResources().getIdentifier(
// "about_size",
// "string", defPackage));
            size = mLockInfo.sizeStr;

            description = mLockInfo.context.getString(mLockInfo.context.getResources().getIdentifier(
                    "about_description",
                    "string", defPackage));
        }

        viewHolder.textViewDesigner.setText(designer);
        viewHolder.textViewDate.setText(date);
        viewHolder.textViewSize.setText(size);
        viewHolder.textViewDownloadCount.setVisibility(View.GONE);
        viewHolder.textViewIntroduction.setText(description);
        if (Build.VERSION.SDK_INT >= 11) {
            view.setScaleY(0.936f);
            view.findViewById(R.id.view_foreground).setVisibility(View.VISIBLE);
        }else{
            view.findViewById(R.id.view_foreground).setVisibility(View.GONE);
        }
        ((ViewPager) container).addView(view, 0, null);
        return view;
    }

    private static class ViewHolder {
        TextView textViewDesigner;
        TextView textViewSize;
        TextView textViewDate;
        TextView textViewIntroduction;
        TextView textViewDownloadCount;
        TextView textViewVersionName;
        TextView textViewLableDownload;
        View lableView;
    }

}
