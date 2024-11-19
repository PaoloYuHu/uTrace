package com.example.utrace.Activity;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utrace.Adapter.AppsListAdapter;
import com.example.utrace.Model.AppModel;
import com.example.utrace.R;

import java.util.ArrayList;
import java.util.List;

public class AppUsage extends AppCompatActivity {

    private static final String TAG = "AppUsageActivity";
    private RecyclerView recyclerView;
    private AppsListAdapter adapter;
    private List<AppModel> appsList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_usage);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchInstalledApps();
        fetchDataUsage();
        fetchNetworkStats();

        //filter out apps with lesser than 5MB of internet usage
        appsList.removeIf(current -> current.getTotalBytes() < 5 * 1024 * 1024);

        adapter = new AppsListAdapter(this, appsList);
        recyclerView.setAdapter(adapter);
    }


    private void fetchInstalledApps() {
        PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo appInfo : packages) {
            AppModel app = new AppModel();
            app.setAppName((String) packageManager.getApplicationLabel(appInfo));
            app.setPackageName(appInfo.packageName);
            app.setUid(appInfo.uid);
            appsList.add(app);
        }
    }

    private void fetchDataUsage() {
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) getSystemService(Context.NETWORK_STATS_SERVICE);
        // 1000 * 60 * 60 * 24; // 24 hours ago
        long startTime = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000); // 30 days
        long endTime = System.currentTimeMillis();

        for (AppModel app : appsList) {
            try {
                NetworkStats stats = networkStatsManager.queryDetailsForUid(
                        NetworkCapabilities.TRANSPORT_WIFI,
                        null,
                        startTime,
                        endTime,
                        app.getUid()
                );

                NetworkStats.Bucket bucket = new NetworkStats.Bucket();
                long totalRxBytes = 0;
                long totalTxBytes = 0;

                while (stats.hasNextBucket()) {
                    stats.getNextBucket(bucket);
                    totalRxBytes += bucket.getRxBytes();
                    totalTxBytes += bucket.getTxBytes();
                }

                app.setRxBytes(totalRxBytes);
                app.setTxBytes(totalTxBytes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void fetchNetworkStats() {
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) this.getSystemService(Context.NETWORK_STATS_SERVICE);
        if (networkStatsManager == null) {
            Log.e(TAG, "NetworkStatsManager is null.");
            return;
        }

        // Define the starting and ending timestamps (e.g., last month)
        long startTime = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000); // 30 days ago
        long endTime = System.currentTimeMillis();

        String dataUsage="Consumi totali\n";
        long mebibyte=1048576;

        // Query data usage for mobile
        // Rx= reception Tx= transmission
        try {
            NetworkStats.Bucket bucket = networkStatsManager.querySummaryForDevice(NetworkCapabilities.TRANSPORT_CELLULAR, null, startTime, endTime);
            long mobileRxBytes = bucket.getRxBytes();
            long mobileTxBytes = bucket.getTxBytes();
            dataUsage+="Mobile Down: " + mobileRxBytes/mebibyte + " MB, Up: " + mobileTxBytes/mebibyte + " MB\n";
            Log.i(TAG, "Mobile Rx: " + mobileRxBytes + " bytes, Tx: " + mobileTxBytes + " bytes");
        } catch (Exception e) {
            Log.e(TAG, "Error querying mobile data usage", e);
        }

        // Query data usage for Wi-Fi
        try {
            NetworkStats.Bucket bucket = networkStatsManager.querySummaryForDevice(NetworkCapabilities.TRANSPORT_WIFI, null, startTime, endTime);
            long wifiRxBytes = bucket.getRxBytes();
            long wifiTxBytes = bucket.getTxBytes();
            dataUsage+="Wi-Fi Down: " + wifiRxBytes/mebibyte + " MB, Up: " + wifiTxBytes/mebibyte + " MB";
            Log.i(TAG, "Wi-Fi Rx: " + wifiRxBytes + " bytes, Tx: " + wifiTxBytes + " bytes");
        } catch (Exception e) {
            Log.e(TAG, "Error querying Wi-Fi data usage", e);
        }
        TextView dataTotal = (TextView) findViewById(R.id.totalUsage);
        dataTotal.setText(dataUsage);
    }
}
