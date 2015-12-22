package com.cyou.cma.clockscreen.util;

import java.util.Comparator;

import com.cyou.cma.clockscreen.Constants;
import com.cyou.cma.clockscreen.bean.AppNotification;

public class NotificationDescendComparator implements
		Comparator<AppNotification> {

	@Override
	public int compare(AppNotification lhs, AppNotification rhs) {
		// <0 左边的比右边的小 >0 左边的比右边的大

		if (lhs.type == 1) {
			return -1;
		} else if (rhs.type == 1) {
			return 1;
		} else {
			if (lhs.mTimeLong >= rhs.mTimeLong) {
				return -1;
			} else {
				return 1;
			}
		}
	}

}
