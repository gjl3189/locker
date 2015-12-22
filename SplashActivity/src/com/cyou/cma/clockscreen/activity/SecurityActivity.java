package com.cyou.cma.clockscreen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.bean.SecurityJudge;
import com.cyou.cma.clockscreen.util.Util;
import com.cyou.cma.clockscreen.widget.MyFrameLayout;

public class SecurityActivity extends BaseActivity {
    private MyFrameLayout myFrameLayout;
    private SecurityJudge mLastSecurityJudge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        myFrameLayout = (MyFrameLayout) findViewById(R.id.security_framelayout);
// SecurityJudge securityJudge = Util.getSecurityLevel(this);
// myFrameLayout.updateSecurityLevel(securityJudge);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SecurityJudge securityJudge = Util.getSecurityLevel(this);
        if (mLastSecurityJudge != securityJudge) {
            myFrameLayout.updateSecurityLevel(securityJudge);
        }
        mLastSecurityJudge = securityJudge;
    }
    //add by Jack
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	if(myFrameLayout!=null){
    		View view = myFrameLayout.findViewById(R.id.guide_mask);
	    	if(view!=null){
				view.setVisibility(View.GONE);
	    	}
    	}
    }
    //end
}
