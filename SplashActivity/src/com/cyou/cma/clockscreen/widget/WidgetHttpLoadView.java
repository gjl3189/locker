package com.cyou.cma.clockscreen.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cynad.cma.locker.R;

public class WidgetHttpLoadView extends LinearLayout {

    private Context mContext;
    private TextView noNetWorkLbl;
    private ProgressBar mLoadingBar;
    private int status;

    public static final int HTTPVIEW_DONE = 0;
    public static final int HTTPVIEW_LOADING = 1;
    public static final int HTTPVIEW_NONETWORK = 2;

    private HttpLoadListener reLoadListener;
    private View mDataView;

    public WidgetHttpLoadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initViews();
        this.setVisibility(View.GONE);
    }

    private void initViews() {
        View.inflate(mContext, R.layout.widget_http_network_layout, this);
        noNetWorkLbl = (TextView) findViewById(R.id.http_no_network);
        mLoadingBar = (ProgressBar) findViewById(R.id.http_progressbar);
        this.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (status != HTTPVIEW_NONETWORK)
                    return;
                if (reLoadListener == null)
                    return;
                setStatus(WidgetHttpLoadView.HTTPVIEW_LOADING);
                reLoadListener.reLoad();
            }
        });
    }

    public void init(View dataView, HttpLoadListener listener) {
        mDataView = dataView;
        reLoadListener = listener;
    }

    public void setStatus(int status) {
        this.status = status;
        refreshView();
    }

    public int getStatus() {
        return status;
    }

    private void refreshView() {
        switch (status) {
            case HTTPVIEW_LOADING:
                this.setVisibility(View.VISIBLE);
                noNetWorkLbl.setVisibility(View.GONE);
                mLoadingBar.setVisibility(View.VISIBLE);
                mDataView.setVisibility(View.GONE);
                break;
            case HTTPVIEW_NONETWORK:
                this.setVisibility(View.VISIBLE);
                noNetWorkLbl.setVisibility(View.VISIBLE);
                mLoadingBar.setVisibility(View.GONE);
                mDataView.setVisibility(View.GONE);
                break;
            case HTTPVIEW_DONE:
                this.setVisibility(View.GONE);
                mDataView.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    public interface HttpLoadListener {
        public void reLoad();
    }

}
