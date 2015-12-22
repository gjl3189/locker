package com.cyou.cma.clockscreen.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class FontedEditText extends EditText {

    public FontedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface mopozm = Typeface.createFromAsset(context.getAssets(),
                "fonts/ziti.ttf");
        setTypeface(mopozm);
    }

}
