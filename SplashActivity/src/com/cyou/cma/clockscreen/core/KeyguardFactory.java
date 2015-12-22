package com.cyou.cma.clockscreen.core;


public class KeyguardFactory {

    /**
     * 这个版本直接以google play实现方式返回锁屏界面
     * @return
     */
    public static KeyguardView createKeyguardView() {
// if (LockApplication.getDexClassLoader().isSupportNewFramework()) {
// return new KeyguardViewFromLayout();
// } else {
// return new KeyguardViewFromClass();
// }
        return new KeyguardView4PlayFromlayout();
    }
}
