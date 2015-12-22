package com.cyou.cma.clockscreen.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import com.cyou.cma.clockscreen.AppClient;
import com.cyou.cma.clockscreen.Urls;
import com.cyou.cma.clockscreen.bean.EmptyEntity;
import com.cyou.cma.clockscreen.bean.jsonparser.EmptyEntityParser;
import com.cyou.cma.clockscreen.util.Util;

@SuppressLint("NewApi")
public class PasswordBackupService extends IntentService {
    public static final String SERVICE_NAME = "passwordbackup";
    String TAG = "PasswordBackupService";

    public PasswordBackupService() {
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {//
        String p = intent.getStringExtra("p");
        String mail = intent.getStringExtra("mail");
        Util.Logjb(TAG, "onHandleIntent");
        AppClient appClient = new AppClient();
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair(Urls.PARAM_PASSWORD, p));
        nameValuePairs.add(new BasicNameValuePair(Urls.PARAM_MAIL, mail));

        HttpGet httpGet = appClient.makeHttpGet(Urls.getPasswordBakUrl(), nameValuePairs);
        EmptyEntityParser parser = new EmptyEntityParser();
        try {

            EmptyEntity result = appClient.executeHttp(httpGet, parser);
            if (result != null) {
                Util.Logjb(TAG, "upload successful");
//                Message message = mHandler.obtainMessage();
//                message.arg1 = R.string.backup_successful;
//                mHandler.sendMessage(message);
// ToastMaster.makeText(this, R.string.backup_successful, Toast.LENGTH_SHORT);
//                stopSelf();
            } else {
                Util.Logjb(TAG, "upload failed mull");
//                Message message = mHandler.obtainMessage();
//                message.arg1 = R.string.backup_failed;
//                mHandler.sendMessage(message);
// ToastMaster.makeText(this, R.string.backup_failed, Toast.LENGTH_SHORT);
//                stopSelf();
            }
        } catch (Exception e) {
            Util.Logjb(TAG, "upload failed");
//            Message message = mHandler.obtainMessage();
//            message.arg1 = R.string.backup_failed;
//            mHandler.sendMessage(message);
// ToastMaster.makeText(this, R.string.backup_failed, Toast.LENGTH_SHORT);
//            stopSelf();
        }

    }

//    Handler mHandler = new Handler() {
//        public void handleMessage(android.os.Message msg) {
//            int resid = msg.arg1;
//            ToastMaster.makeText(PasswordBackupService.this, resid, Toast.LENGTH_SHORT);
//        };
//    };
}
