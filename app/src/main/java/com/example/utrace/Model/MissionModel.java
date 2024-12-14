package com.example.utrace.Model;

//Model used to further elaborate Mission object from database

import static com.example.utrace.utils.Constants.CELLULAR;
import static com.example.utrace.utils.Constants.DAY;
import static com.example.utrace.utils.Constants.WEEK;
import static com.example.utrace.utils.Constants.WIFI;


import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.util.Log;

import com.example.utrace.utils.FormatHelper;

public class MissionModel {
    private String id;
    private String title;
    private String description;
    private String repeat;
    private String type;
    private int icon;

    public MissionModel(String id, String title, String description, String repeat, String type, int icon) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.repeat = repeat;
        this.type = type;
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPercentage(Context context) {
        if(repeat.equals("day"))
            return calcPercentage(context, DAY);
        else if (repeat.equals("week"))
            return calcPercentage(context, WEEK);
        return 0;
    }

    public int getIcon() {
        return icon;
    }

    public String getRepeat() {
        return repeat;
    }

    public String getType() {
        return type;
    }


    public int calcPercentage(Context context, long selectedTime) {
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(Context.NETWORK_STATS_SERVICE);
        if (networkStatsManager == null) {
            return -1;  // Return -1 to indicate an error
        }

        long startTime = System.currentTimeMillis() - selectedTime;
        long endTime = System.currentTimeMillis();

        long totalWIFI = 0;
        long totalMOBILE = 0;

        try {
            NetworkStats.Bucket bucket = networkStatsManager.querySummaryForDevice(CELLULAR, null, startTime, endTime);
            long mobileRxBytes = bucket.getRxBytes();
            long mobileTxBytes = bucket.getTxBytes();
            totalMOBILE = mobileRxBytes + mobileTxBytes;
        } catch (Exception e) {
            Log.e("MissionModel", "Error querying mobile data usage", e);
        }

        try {
            NetworkStats.Bucket bucket = networkStatsManager.querySummaryForDevice(WIFI, null, startTime, endTime);
            long wifiRxBytes = bucket.getRxBytes();
            long wifiTxBytes = bucket.getTxBytes();
            totalWIFI = wifiTxBytes + wifiRxBytes;
        } catch (Exception e) {
            Log.e("MissionModel", "Error querying Wi-Fi data usage", e);
        }

        if (totalMOBILE == 0) {
            return 0; // Avoid division by zero
        }
        description+="\n"+FormatHelper.bytesToString(totalWIFI)+"/"+ FormatHelper.bytesToString(totalMOBILE);

        return (int) ((totalWIFI * 100) / totalMOBILE);
    }

}
