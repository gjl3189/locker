package com.cyou.cma.clockscreen.widget;

import com.cyou.cma.clockscreen.widget.material.LButton;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;


public class FontedButton extends LButton {

    public FontedButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface mopozm = Typeface.createFromAsset(context.getAssets(),
                "fonts/ziti.ttf");
        setTypeface(mopozm);
    }

}

