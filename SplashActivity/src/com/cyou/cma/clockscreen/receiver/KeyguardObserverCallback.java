package com.cyou.cma.clockscreen.receiver;

/**
 * 锁屏的监听接口
 * 
 * @author Peter.Jiang
 */
public interface KeyguardObserverCallback extends BatteryChangeReceiver.OnBatteryChangeReceiver,
        DateTimeObserver.OnDateTimeObserver, MissedCallObserver.OnMissedCallObserver,
        TimeChangeReceiver.OnTimeChangeReceiver, UnreadMsgObserver.OnUnreadMsgObserver {

}
