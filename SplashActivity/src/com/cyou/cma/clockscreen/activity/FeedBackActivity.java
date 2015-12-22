
package com.cyou.cma.clockscreen.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.bean.HttpResponseBean;
import com.cyou.cma.clockscreen.util.HttpUtil;
import com.cyou.cma.clockscreen.util.ImageUtil;
import com.cyou.cma.clockscreen.util.SystemUIStatusUtil;
import com.cyou.cma.clockscreen.util.ToastMaster;
import com.cyou.cma.clockscreen.util.Util;
import com.cyou.cma.clockscreen.widget.CustomAlertDialog;
import com.cyou.cma.clockscreen.widget.material.LImageButton;
import com.umeng.analytics.MobclickAgent;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

public class FeedBackActivity extends BaseActivity implements OnClickListener {
    private LImageButton backIv, commitIv;
    private ImageView deleteIv;
    private EditText contentEt, contactEt;
    private TextView textLimit;
    private TextView mContentTextView;

    private CustomAlertDialog feedBackDialog;
    private String loadingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemUIStatusUtil.onCreate(this, this.getWindow());
        setContentView(R.layout.activity_feedback);
        mContext = this;
        initViews();
        View contentView = View.inflate(mContext, R.layout.layout_feedback_dialog_content, null);
        mContentTextView = (TextView) contentView.findViewById(R.id.feedback_dialog_content);
        loadingText = getString(R.string.user_feedback_send);
        mContentTextView.setText(loadingText);
        feedBackDialog = new CustomAlertDialog.Builder(mContext)
                .setTitle(R.string.userfeedback_label)
                .setView(contentView)
                .create();
        feedBackDialog.setCanceledOnTouchOutside(false);
        feedBackDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
                return true;
            }
        });
        if (SystemUIStatusUtil.isStatusBarTransparency(mContext)) {
            findViewById(R.id.root).setPadding(0, ImageUtil.getStatusBarHeight(mContext), 0, 0);
        }
    }

    private void initViews() {
        backIv = (LImageButton) findViewById(R.id.feedback_back);
        backIv.setOnClickListener(this);
        commitIv = (LImageButton) findViewById(R.id.feedback_send);
        commitIv.setOnClickListener(this);
        deleteIv = (ImageView) findViewById(R.id.feedback_delete);
        contentEt = (EditText) findViewById(R.id.feedback_content);
        contactEt = (EditText) findViewById(R.id.feedback_mail_address);
        deleteIv.setOnClickListener(this);
        textLimit = (TextView) findViewById(R.id.feedback_text_limit);

        contentEt.addTextChangedListener(textWatcher);
        contentEt.clearFocus();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.feedback_delete:
                contentEt.setText("");
                deleteIv.setVisibility(View.GONE);
                break;
            case R.id.feedback_back:
                finish();
                break;
            case R.id.feedback_send:
                if (Util.contentIsNull(contentEt.getText().toString())) {
                    // Toaster.getInstance(getApplicationContext()).showToast(R.string.userfeedback_no_text);
                    ToastMaster.makeText(getApplicationContext(),
                            R.string.userfeedback_no_text, Toast.LENGTH_SHORT);
                    return;
                }
                commit();
                break;
            default:
                break;
        }
    }

    private Timer timer;
    private int index = 0;

    private void startTimer() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                index++;
                if (handler != null) {
                    if (timer != null) {
                        timer.cancel();
                    }
                } else {
                    handler.sendEmptyMessage(1000);
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 500);
    }

    private void commit() {
        if (!feedBackDialog.isShowing()) {
            feedBackDialog.show();
        }
        startTimer();
        new Thread() {
            @Override
            public void run() {
                String contact = Util.contentIsNull(contactEt.getText()
                        .toString()) ? "null" : contactEt.getText().toString();
                Map<String, String> paramMap = new HashMap<String, String>();
                paramMap.put("serial", Util.getImeiCode(mContext));
                paramMap.put("email", contact);
                paramMap.put("versionId", Util.getCurrenVersion(mContext));
                paramMap.put("phoneModelName", android.os.Build.MODEL);
                paramMap.put("resolutionName", Util.getScreenHeight(mContext)
                        + "x" + Util.getScreenWidth(mContext));
                paramMap.put("androidVersion", android.os.Build.VERSION.RELEASE);
                paramMap.put("type", "1");
                paramMap.put("date", getCurrentTime());
                paramMap.put("content", contentEt.getText().toString());
                HttpResponseBean resultBean = HttpUtil.httpGet(mContext,
                        getUrl(HttpUtil.URL_FEEDBACK, paramMap));
                if (handler != null) {
                    Message msg = handler.obtainMessage();
                    // Message msg = new Message();
                    msg.what = resultBean.isSuccess() ? FEEDBACK_SUCCESS
                            : FEEDBACK_ERROR;
                    msg.obj = resultBean.getErrorMsg();
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }

    @SuppressLint("SimpleDateFormat")
    private String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy年MM月dd日  HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        return str;
    }

    public static String getUrl(String url, Map<String, String> paramMap) {
        if (paramMap == null || paramMap.size() == 0) {
            return url;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("?");
        for (Entry<String, String> entry : paramMap.entrySet()) {
            sb.append(entry.getKey());
            sb.append("=");
            try {
                sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                //e.printStackTrace();
                sb.append("null");
            }
            sb.append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        return url + sb.toString();
    }

    private final int FEEDBACK_SUCCESS = 1;
    private final int FEEDBACK_ERROR = -1;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1000) {
                String tempDot = "";
                for (int i = 0; i < index % 4; i++) {
                    tempDot += ".";
                }
                mContentTextView.setText(loadingText + tempDot);
                return;
            }
            if (feedBackDialog.isShowing()) {
                feedBackDialog.cancel();
            }
            switch (msg.what) {
                case FEEDBACK_SUCCESS:
                    ToastMaster.makeText(getApplicationContext(),
                            mContext.getString(R.string.userfeedback_success), Toast.LENGTH_SHORT);
                    FeedBackActivity.this.finish();
                    break;
                case FEEDBACK_ERROR:
                    ToastMaster.makeText(getApplicationContext(),
                            mContext.getString(R.string.userfeedback_failed) + " "
                                    + ((String) msg.obj), Toast.LENGTH_SHORT);
                    break;

                default:
                    break;
            }
        }

    };

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count) {
            if (null == contentEt.getText()
                    || contentEt.getText().toString().trim().equals("")) {
                deleteIv.setVisibility(View.INVISIBLE);
                textLimit.setVisibility(View.INVISIBLE);
            } else {
                deleteIv.setVisibility(View.VISIBLE);
                textLimit.setVisibility(View.VISIBLE);
                textLimit.setText(contentEt.getText().length() + "/300");
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        timer = null;
        if (handler != null) {
            handler.removeMessages(1000);
            handler.removeMessages(FEEDBACK_SUCCESS);
            handler.removeMessages(FEEDBACK_ERROR);
        }
        handler = null;
    }

}
