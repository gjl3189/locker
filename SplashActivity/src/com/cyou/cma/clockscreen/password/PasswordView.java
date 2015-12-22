package com.cyou.cma.clockscreen.password;


/**
 * 密码界面 应该遵守的设计规则
 * 
 * @author jiangbin
 */
public interface PasswordView<T> {

    public void setSecureAccess(SecureAccess secureAccess);

    public void onShow();

    public void onHide();
    
    public boolean checkPassword(T password);
}
