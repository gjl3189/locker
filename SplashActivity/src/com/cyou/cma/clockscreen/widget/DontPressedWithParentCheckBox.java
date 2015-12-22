package com.cyou.cma.clockscreen.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;

public class DontPressedWithParentCheckBox extends CheckBox {
	 public DontPressedWithParentCheckBox(Context context, AttributeSet attrs) {
	        super(context, attrs);
	    }

	    @Override
	    public void setPressed(boolean pressed) {
	        // If the parent is pressed, do not set to pressed.
	        if (pressed && ((View) getParent()).isPressed()) {
	            return;
	        }
	        super.setPressed(pressed);
	    }
	    @Override
	    public boolean onTouchEvent(MotionEvent event) {
	    	// TODO Auto-generated method stub
//	    	return super.onTouchEvent(event);
	    	return false;
	    }
}
