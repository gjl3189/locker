
package com.cyou.cma.clockscreen.bean;

public class MsgCenterBean {
    private int type;
    private int count;
    private long lastTime;

    public MsgCenterBean() {
    }

    public MsgCenterBean(int type, int count, long lastTime) {
        this.type = type;
        this.count = count;
        this.lastTime = lastTime;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
