package com.cyou.cma.clockscreen.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.widget.CustomAlertDialog;

public class NotifySettingDialogActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		View v = new View(this);
		setContentView(v);
		View dialogView = View.inflate(this,
				R.layout.layout_notifysetting_dialog, null);
		CustomAlertDialog tipDialog = new CustomAlertDialog.Builder(this)
				.setTitle(R.string.dialog_tips).setView(dialogView)
				.setPositiveButton("ok", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						NotifySettingDialogActivity.this.finish();
					}
				}).create();
		tipDialog.setCancelable(false);
		tipDialog.show();
	}
}
