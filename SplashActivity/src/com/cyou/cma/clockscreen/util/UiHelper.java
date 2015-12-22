package com.cyou.cma.clockscreen.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.Constants;
import com.cyou.cma.clockscreen.service.MailBoxService;
import com.cyou.cma.clockscreen.service.PasswordBackupService;
import com.cyou.cma.clockscreen.widget.CustomAlertDialog;
import com.cyou.cma.clockscreen.widget.FontedEditText;

public class UiHelper {
	public static void showMailboxDialog(final Context context) {
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		final FontedEditText fontedEditText = (FontedEditText) layoutInflater
				.inflate(R.layout.edittext, null);
		CustomAlertDialog.Builder builder = new CustomAlertDialog.Builder(
				context);
		builder.setTitle(R.string.security_mailbox_backup);
		builder.setMessage(R.string.tip_password_backup);
		builder.setPositiveButton(context.getString(R.string.send_text),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						if (HttpUtil.isNetworkAvailable(context)) {
							String mail = fontedEditText.getText().toString();

							UiHelper.sendMailboxOnly(context, mail);
						} else {
							ToastMaster.makeText(context,
									R.string.network_unavailable,
									Toast.LENGTH_SHORT);
						}
					}
				});

		builder.setNegativeButton(context.getString(R.string.send_not_text),
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.setView(fontedEditText);
		final CustomAlertDialog dg = builder.create();
		fontedEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

				boolean isEmail = Util.checkEmail(s.toString());
				if (isEmail) {
					dg.setPositiveButtonEnable(true);
				} else {
					dg.setPositiveButtonEnable(false);
				}
			}
		});

		dg.show();
		dg.setPositiveButtonEnable(false);
		fontedEditText.setText(MailBoxHelper.getSavedMailBox(context));

	}

	public static void sendMailboxAndPasssword(Context context, String mail) {
		// String mail = mEditText.getText().toString();
		String password = "";

		password = Util.getPreferenceString(context, Constants.AAAAAA);

		Intent intent = new Intent(context, PasswordBackupService.class);
		intent.putExtra("p", password);
		intent.putExtra("mail", mail);
		context.startService(intent);

		Intent intent2 = new Intent(context, MailBoxService.class);
		intent2.putExtra("mail", mail);
		// startService(intent)
		context.startService(intent2);
	}

	public static void sendMailboxOnly(Context context, String mail) {

		Intent intent2 = new Intent(context, MailBoxService.class);
		intent2.putExtra("mail", mail);
		// startService(intent)
		context.startService(intent2);

	}

}
