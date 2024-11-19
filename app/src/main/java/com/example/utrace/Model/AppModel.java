package com.example.utrace.Model;

import java.io.Serializable;

/**
 * Hu note:
 * implements Serializable
 * to make it passable between different components of uTrace
 * if this option is not used consider removing the implementation
 * for, potentially, faster and better optimized code
 * **/
public class AppModel implements Serializable {
    private String appName;
    private String packageName;
    private boolean isSelected;
    private long rxBytes;
    private long txBytes;
    private int uid;

    // Getters and setters
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public long getRxBytes() {
        return rxBytes;
    }

    public void setRxBytes(long rxBytes) {
        this.rxBytes = rxBytes;
    }

    public long getTxBytes() {
        return txBytes;
    }

    public void setTxBytes(long txBytes) {
        this.txBytes = txBytes;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public long getTotalBytes(){
        return txBytes+rxBytes;
    }
}

