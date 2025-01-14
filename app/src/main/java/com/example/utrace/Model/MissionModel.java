package com.example.utrace.Model;

//Model used to further elaborate Mission object from database

import static com.example.utrace.utils.Constants.CELLULAR;
import static com.example.utrace.utils.Constants.DAY;
import static com.example.utrace.utils.Constants.GiB;
import static com.example.utrace.utils.Constants.MiB;
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
        this.description = description;
        this.repeat = repeat;
        this.type = type;
        this.icon = icon;
        if(repeat.equals("day"))
            this.title = title+ " (punti: "+this.getPoints()+ ")";
        else if (repeat.equals("week")) {
            this.title = title+ " (punti: "+this.getPoints()+ ")";
        }else
            this.title = title;
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
        if(type.equals("usageMOBILE")){
            description+="\n"+FormatHelper.bytesToString(totalMOBILE)+" usati";
            if(repeat.equals("day")){
                return totalMOBILE<(100*MiB) ? 100 : -1;
            }else
                return totalMOBILE<(GiB) ? 100: -1;
        }else if(type.equals("usageWIFI")){
            description+="\n"+FormatHelper.bytesToString(totalWIFI)+" usati";
            if(repeat.equals("day")){
                return totalWIFI<(500*MiB) ? 100 : -1;
            }else
                return totalWIFI<(5*GiB) ? 100: -1;
        }

        description+="\n"+FormatHelper.bytesToString(totalWIFI)+"/"+ FormatHelper.bytesToString(totalMOBILE);
        return Math.min((int) ((totalWIFI * 100) / totalMOBILE), 100);
    }

    public int getPoints(){
        int points=0;
        if(this.repeat.equals("week"))
            points=5;
        else points=1;
        if(this.type.startsWith("usage"))
            points*=3;
        return points;
    }

}
