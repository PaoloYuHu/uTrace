package com.example.utrace.utils;

import android.annotation.SuppressLint;

public class FormatHelper {
    //debug tag
    private static final String TAG = FormatHelper.class.getSimpleName();

    @SuppressLint("DefaultLocale")
    public static String bytesToString(Long bytes) {
        if (bytes == null) {
            return "0 Bytes";
        }

        float totalBytes = bytes;
        float totalKB = totalBytes / 1024;
        float totalMB = totalKB / 1024;
        float totalGB = totalMB / 1024;

        if (totalGB > 1) {
            return String.format("%.2f GB", totalGB);
        } else if (totalMB > 1) {
            return String.format("%.2f MB", totalMB);
        } else if (totalKB > 1) {
            return String.format("%.2f KB", totalKB);
        } else {
            return String.format("%d Bytes", bytes);
        }
    }

}
