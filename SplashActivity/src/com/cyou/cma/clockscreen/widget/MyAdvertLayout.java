package com.cyou.cma.clockscreen.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.cynad.cma.locker.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class MyAdvertLayout extends FrameLayout {
	private AdView mAdView;
	private ImageView mCloseImageView;
	private String id;
	private String idDefault = "";

	public MyAdvertLayout(Context context) {
		this(context, null);
	}

	public MyAdvertLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		if (attrs != null) {
			TypedArray a = getContext().obtainStyledAttributes(attrs,
					R.styleable.Advert);
			id = a.getString(R.styleable.Advert_advertId);
			a.recycle();
		}
		if (TextUtils.isEmpty(id)) {
			id = idDefault;
		}
		initView();
	}

	private void initView() {
		View.inflate(getContext(), R.layout.layout_advert, this);
		mCloseImageView = (ImageView) findViewById(R.id.banner_close);

		mAdView = (AdView) findViewById(R.id.adView);
		mCloseImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MyAdvertLayout.this.setVisibility(View.GONE);
			}
		});
		
		mAdView = new AdView(getContext());
        mAdView.setAdUnitId(id);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdListener(new MyAdListener(getContext()));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
        		FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        this.addView(mAdView, params);
        mAdView.loadAd(new AdRequest.Builder().build());
	}

	public void onPause() {
		mAdView.pause();
	}

	public void onResume() {
		mAdView.resume();
	}

	public void destory() {
		mAdView.destroy();
	}
	
	class MyAdListener extends AdListener {
	    private Context mContext;

	    public MyAdListener(Context context) {
	        this.mContext = context;
	    }

	    @Override
	    public void onAdLoaded() {
	    	mCloseImageView.setVisibility(View.VISIBLE);
	    }

	    @Override
	    public void onAdFailedToLoad(int errorCode) {
	    }

	    @Override
	    public void onAdOpened() {
	    }

	    @Override
	    public void onAdClosed() {
	    }

	    @Override
	    public void onAdLeftApplication() {
	    }
	}


}
