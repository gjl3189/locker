package com.cyou.cma.clockscreen.bean;

import com.cyou.cma.clockscreen.Constants;
import com.cyou.cma.clockscreen.util.Util;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * 已安装锁屏主题的信息
 * 
 * @author jiangbin
 */
public class InstallLocker implements EntityType {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static boolean CheckSignature = !Util.DEBUG;// TODO jiangbin
    public static final String PACKAGENAME = "com.cynad.cma.locker";
    public long firstInstallTime;
    public long lastUpdateTime;
    public String packageName;
    public int versionCode;
    public String versionName;
    public String label;
    public Context context;
    public boolean currentTheme;
    public String sizeStr = "";

    // add by Jack
    public boolean needAnim = true;

    // end

    public static boolean isOurPakcageName(String packageName) {
        return packageName.contains(Constants.THEME_PACKAGENAME_PREFIX);
    }

    private static boolean isSignatureOk(PackageManager packageManager, String packageName) {
        int match = packageManager.checkSignatures(packageName, PACKAGENAME);
        return match == PackageManager.SIGNATURE_MATCH;
    }

    public static boolean isOurLocker(PackageManager packageManager, String packageName) {
        if (CheckSignature) {
            return isOurPakcageName(packageName) && isSignatureOk(packageManager, packageName);
        } else {
            return isOurPakcageName(packageName);
        }
    }
}
