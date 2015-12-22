package com.cyou.cma.clockscreen.util;

import java.util.Comparator;

import com.cyou.cma.clockscreen.Constants;
import com.cyou.cma.clockscreen.bean.InstallLocker;

public class InstallLockerDescendComparator implements Comparator<InstallLocker> {

    @Override
    public int compare(InstallLocker lhs, InstallLocker rhs) {
        // <0 左边的比右边的小 >0 左边的比右边的大
        if (Constants.SKY_LOCKER_DEFAULT_THEME.equals(lhs.packageName)) {
            return -1;
        } else if (Constants.SKY_LOCKER_DEFAULT_THEME.equals(rhs.packageName)) {
            return 1;
        } else {
            if (lhs.firstInstallTime >= rhs.firstInstallTime) {
                return -1;
            } else {
                return 1;
            }
        }

    }

}
