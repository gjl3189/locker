package com.cyou.cma.clockscreen.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.AppClient;
import com.cyou.cma.clockscreen.Urls;
import com.cyou.cma.clockscreen.bean.EmptyEntity;
import com.cyou.cma.clockscreen.bean.jsonparser.EmptyEntityParser;
import com.cyou.cma.clockscreen.event.MailboxEvent;
import com.cyou.cma.clockscreen.util.MailBoxHelper;
import com.cyou.cma.clockscreen.util.ToastMaster;
import com.cyou.cma.clockscreen.util.Util;

import de.greenrobot.event.EventBus;

@SuppressLint("NewApi")
public class MailBoxService extends IntentService {
	public static final String SERVICE_NAME = "mailbox";
	String TAG = "PasswordBackupService";

	public MailBoxService() {
		super(SERVICE_NAME);
	}

	@Override
	protected void onHandleIntent(Intent intent) {//

		String mail = intent.getStringExtra("mail");
		Util.Logjb(TAG, "onHandleIntent");
		AppClient appClient = new AppClient();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair(Urls.PARAM_IMEI, Util
				.getImei(this)));
		nameValuePairs.add(new BasicNameValuePair(Urls.PARAM_ANDROID_ID, Util
				.getAndroidId(this)));
		nameValuePairs.add(new BasicNameValuePair(Urls.PARAM_EMAIL, mail));
		nameValuePairs.add(new BasicNameValuePair(Urls.PARAM_MAC, Util
				.getLocalMacAddress(this)));
		// HttpGet httpGet = appClient.makeHttpGet(Urls.getMailboxUrl(),
		// nameValuePairs);
		HttpPost httpPost = appClient.makeHttpPost(Urls.getMailboxUrl(),
				nameValuePairs);
		EmptyEntityParser parser = new EmptyEntityParser();
		try {

			EmptyEntity result = appClient.executeHttp(httpPost, parser);
			if (result != null) {
				Util.Logjb(TAG, "upload successful");
				Message message = mHandler.obtainMessage();
				message.arg1 = R.string.mailbox_suceess;
				mHandler.sendMessage(message);
				// Util.putPreferenceBoolean(this, MailBoxHelper.MAILBOX_KEY,
				// true);
				MailBoxHelper.saveMailBox(this, mail);
				sendBroadcast(true,mail);

			} else {
				Util.Logjb(TAG, "upload failed mull");
				Message message = mHandler.obtainMessage();
				message.arg1 = R.string.mailbox_failed;
				mHandler.sendMessage(message);
				MailBoxHelper.saveMailBox(this, "");
				sendBroadcast(false,mail);

			}
		} catch (Exception e) {
			Util.Logjb(TAG, "upload failed");
			Message message = mHandler.obtainMessage();
			message.arg1 = R.string.mailbox_failed;
			mHandler.sendMessage(message);
			MailBoxHelper.saveMailBox(this, "");
			sendBroadcast(false,mail);

		}

	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			int resid = msg.arg1;
			ToastMaster
					.makeText(MailBoxService.this, resid, Toast.LENGTH_SHORT);
		};
	};

	// public void sendBroadcast() {
	// Intent intent = new Intent(Intents.ACTION_MAILBOX);
	// sendBroadcast(intent);
	// }
	public void sendBroadcast(boolean successful,String mailbox) {
		EventBus.getDefault().post(new MailboxEvent(successful,mailbox));
	}
}
