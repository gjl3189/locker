package com.cyou.cma.clockscreen.bean;

import java.util.HashMap;

import com.cynad.cma.locker.R;

public enum SecurityLevel {
   
    LEVELA(R.string.levela),
    LEVELB(R.string.levelb),
    LEVELC(R.string.levelc),
    LEVELD(R.string.leveld);
    public int levelTip;
    public static HashMap<Integer, String> hashMap= new HashMap<Integer, String>();
    static{
        hashMap.put(0, "LEVELD");
        hashMap.put(1, "LEVELC");
        hashMap.put(2, "LEVELB");
        hashMap.put(3, "LEVELA");
    }
    SecurityLevel(int resId) {
        this.levelTip = resId;
        
    }

}
