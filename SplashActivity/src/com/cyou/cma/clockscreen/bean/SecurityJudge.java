package com.cyou.cma.clockscreen.bean;

import java.util.ArrayList;

public class SecurityJudge {
    public SecurityLevel securityLevel;
    public ArrayList<SecurityBean> mOns;
    public ArrayList<SecurityBean> mOffs;

    @Override
    public boolean equals(Object o) {
        if (o instanceof SecurityJudge) {
            return ((SecurityJudge) o).securityLevel.ordinal() == securityLevel.ordinal();
        }
        return super.equals(o);
    }
}
