package com.example.utrace.utils;


import android.net.NetworkCapabilities;

public class Constants {
    public static final int WIFI = NetworkCapabilities.TRANSPORT_WIFI;
    public static final int CELLULAR = NetworkCapabilities.TRANSPORT_CELLULAR;
    public static final long DAY = 24 * 60 * 60 * 1000;
    public static final long WEEK = 7L * 24 * 60 * 60 * 1000;
    public static final long MONTH = 30L * 24 * 60 * 60 * 1000;
    public static final float MiB = 1024*1024;
    public static final float GiB = 1024*1024*1024;
    public static final float CarbonGramPerGB = 4.2f;
    public static final float CarbonGramPerWh = 0.2572f;
    public static final float avgWattPerHour = 3f;
}

