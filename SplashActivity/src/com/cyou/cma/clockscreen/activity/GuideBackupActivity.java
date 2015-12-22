package com.cyou.cma.clockscreen.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.adjust.sdk.Adjust;
import com.cynad.cma.locker.R;
import com.cyou.cma.cengine.CyStageFactory;
import com.cyou.cma.cengine.CyStageView;
import com.cyou.cma.clockscreen.Constants;
import com.cyou.cma.clockscreen.event.MailboxEvent;
import com.cyou.cma.clockscreen.event.SecurityCompleteEvent;
import com.cyou.cma.clockscreen.service.MailBoxService;
import com.cyou.cma.clockscreen.service.PasswordBackupService;
import com.cyou.cma.clockscreen.util.ImageUtil;
import com.cyou.cma.clockscreen.util.SystemUIStatusUtil;
import com.cyou.cma.clockscreen.util.Util;

import de.greenrobot.event.EventBus;

public class GuideBackupActivity extends Activity implements OnClickListener {
	private TextView tileTextView;
	private EditText mEditText;

	// add by Jack
	private CyStageView mask = null;
	private static final String ID = "plane";
	private static final String CONFIG = "stage/plane/config.xml";

	// end

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SystemUIStatusUtil.onCreate(this, this.getWindow());
		setContentView(R.layout.activity_guide_backup);
		initView();
		if (SystemUIStatusUtil.isStatusBarTransparency(this)) {
			findViewById(R.id.root).setPadding(0,
					ImageUtil.getStatusBarHeight(this), 0, 0);
		}
		EventBus.getDefault().register(this);

	}

	private void initView() {
		tileTextView = (TextView) findViewById(R.id.tv_title);
		tileTextView.setText(R.string.mailbackup_title);
		mEditText = (EditText) findViewById(R.id.password_entry);
		findViewById(R.id.btn_left).setOnClickListener(this);
		findViewById(R.id.send).setOnClickListener(this);

		// add by Jack
		mask = (CyStageView) findViewById(R.id.activity_guide_backup_mask);
		mask.setEatTouchEvent(true);
		// end

		// temp
		// tileTextView.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// showMask(true);
		// }
		// });
		// end
	}

	// add by Jack：可在线程中调用
	private Handler hd = new Handler();
	private long showTime = 0;
	private static final int MIN_SHOW = 1000;
	private final Runnable showRn = new Runnable() {
		@Override
		public void run() {
			if (mask != null) {
				CyStageFactory.addAssetStage(ID, CONFIG);
				mask.start(ID);
				showTime = System.currentTimeMillis();
			}
		}
	};
	private final Runnable hideRn = new Runnable() {
		@Override
		public void run() {
			if (mask != null) {
				mask.stop();
			}
		}
	};

	protected void onResume() {
		super.onResume();
		Adjust.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Adjust.onPause();
	}

	public void showMask(final boolean show) {
		if (hd != null) {
			if (show) {
				hd.removeCallbacks(hideRn);
				hd.post(showRn);
			} else {
				long delay = showTime + MIN_SHOW - System.currentTimeMillis();
				if (delay < 0) {
					delay = 0;
				} else if (delay > MIN_SHOW) {
					delay = MIN_SHOW;
				}
				hd.postDelayed(hideRn, delay);
			}
		} else {
			if (show) {
				runOnUiThread(showRn);
			} else {
				runOnUiThread(hideRn);
			}
		}
	}

	// end

	// @Override
	// public void onBackPressed() {
	// if(mask!=null&&mask.getVisibility()==View.VISIBLE){
	// showMask(false);
	// }else{
	// super.onBackPressed();
	// }
	// }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.send:
			String mail = mEditText.getText().toString();
			String password = "";

			password = Util.getPreferenceString(GuideBackupActivity.this,
					Constants.AAAAAA);

			Intent intent = new Intent(GuideBackupActivity.this,
					PasswordBackupService.class);
			intent.putExtra("p", password);
			intent.putExtra("mail", mail);
			startService(intent);

			Intent intent2 = new Intent(GuideBackupActivity.this,
					MailBoxService.class);
			intent2.putExtra("mail", mail);
			// startService(intent)
			startService(intent2);
			// finish();
//			mask.setVisibility(View.VISIBLE);
			showMask(true);
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  
			break;

		default:
			break;
		}
	}

	/**
	 * 服务器返回成功后的 订阅方法
	 * 
	 * @param event
	 */
	public void onEventMainThread(MailboxEvent event) {
		finish();
	}

	// add by Jack
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mask != null) {
			mask.stop();
			mask = null;
		}
		hd = null;
		EventBus.getDefault().post(new SecurityCompleteEvent());
		EventBus.getDefault().unregister(this);
	}
	// end
}
