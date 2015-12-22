package com.cyou.cma.clockscreen.util;

import java.util.Comparator;

import com.cyou.cma.clockscreen.bean.SecurityBean;

public class SecurityComparator implements Comparator<SecurityBean> {

    @Override
    public int compare(SecurityBean lhs, SecurityBean rhs) {
        // <0 左边的比右边的小 >0 左边的比右边的大
        if (lhs.level < rhs.level) {
            return -1;
        } else {
            return 1;
        }

    }

}
