
package com.cyou.cma.clockscreen.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;

import com.cynad.cma.locker.R;
import com.cyou.cma.cengine.CyActor;
import com.cyou.cma.cengine.CyActor.CyActorListener;
import com.cyou.cma.cengine.CyStage;
import com.cyou.cma.cengine.CyStage.BuildCallback;
import com.cyou.cma.cengine.CyStageFactory;
import com.cyou.cma.cengine.CyStageView;
import com.cyou.cma.clockscreen.widget.material.LFrameLayout;

public class CyStageClickButton extends LFrameLayout {
    protected Context mContext;
    // add by Jack
    private CyStageView cv = null;
    private String id = "trash";
    private String res = "stage/trash/config.xml";
    // end

    public CyStageClickButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        setView(context, attrs);
    }

    protected void setView(Context context, AttributeSet attrs) {
        mContext = context;
    }

    protected void initView(Context ct) {
        View.inflate(ct, R.layout.lwidget_stage_backbutton, this);
        // stageView
        CyStageFactory.addAssetStage(id, res);
        cv = (CyStageView) findViewById(R.id.stage_view);
        BuildCallback cb = new BuildCallback(){
			@Override
			public void onStart(CyStage st) {}
			@Override
			public void onEnd(CyStage st) {
				if(st!=null){
					st.setActorIndex(0);
					CyActorListener al = new CyActorListener(){
						@Override
						public void onStart() {
						}
						@Override
						public void onDrawRc(Canvas can, Bitmap bmp, Rect rc,
								Paint pt) {
						}
						@Override
						public void onRun(float run) {
						}
						@Override
						public void onZero() {
						}
						@Override
						public float onEnd(CyStageView sv, CyStage st) {
							return 1;
						}
					};
					CyActor ac = st.getActor(0);
					if(ac!=null){
						ac.setAl(al);
					}
				}
			}
		};
        cv.start(id, cb);
    }

//    @Override
//    public void onClick(View v) {
//        if (mClickListener != null) {
//            // mClickListener.onLClick(this);
//            mClickListener.onClick(this);
//            if (cv != null) {
//            	CyStage st = cv.getStage();
//            	if(st!=null){
//            		CyActor ac = st.getActor();
//            		if(ac!=null){
//                        ac.isInitiative = true;
//                        st.loadActor(ac);
//                        cv.invalidate();		
//            		}
//            	}
//            }
//        }
//    }

    @Override
	public boolean performClick() {
    	if (cv != null) {
        	CyStage st = cv.getStage();
        	if(st!=null){
        		CyActor ac = st.getActor();
        		if(ac!=null){
                    ac.isInitiative = true;
                    st.loadActor(ac);
                    cv.invalidate();		
        		}
        	}
        }
		return super.performClick();
	}

    // add by Jack
    public void onDestroy() {
        if (cv != null) {
            cv.stop();
        }
    }

    public void resetStageView() {
        if (cv != null) {
        	CyStage st = cv.getStage();
        	if(st!=null){
        		CyActor ac = st.getActor();
        		if(ac!=null){
                    ac.isInitiative = false;
                    st.loadActor(ac);
                    cv.updateRp(0);		
        		}
        	}
        }
    }
    // end
}
