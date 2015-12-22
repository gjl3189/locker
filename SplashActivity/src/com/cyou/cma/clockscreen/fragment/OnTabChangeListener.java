/**
 * 
 */
package com.cyou.cma.clockscreen.fragment;

/**
 * Tab更改监听接口
 * 实现此接口的对象具有监听Tab更改的能力
 * 
 * @author xuchunlei
 */
public interface OnTabChangeListener {
    /**
     * Tab更改为未选中状态事件
     */
    void onUnselected();

    /**
     * Tab更改为选中状态事件
     */
    void onSelected();
}
