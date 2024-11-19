package com.example.utrace;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.Manifest;
import android.content.Context;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import android.content.Context;
import android.content.SharedPreferences;


import java.io.Serializable;

class AppModel implements Serializable {
    private String appName, packageName;
    private Boolean isSystemApp;
    private Boolean isSelected = false;

    public AppModel() {
        // Empty constructor
    }

    public AppModel(String appName, String packageName) {
        this.appName = appName;
        this.packageName = packageName;
    }

    public AppModel(String appName, String packageName, Boolean isSystemApp) {
        this.appName = appName;
        this.packageName = packageName;
        this.isSystemApp = isSystemApp;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String mAppName) {
        this.appName = mAppName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String mPackageName) {
        this.packageName = mPackageName;
    }

    public Boolean getIsSystemApp() {
        return isSystemApp;
    }

    public void setIsSystemApp(Boolean systemApp) {
        isSystemApp = systemApp;
    }

    public Boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(Boolean selected) {
        isSelected = selected;
    }
}

class AppDataUsage {
    private String packageName;
    private long totalBytes;

    public AppDataUsage(String packageName, long totalBytes) {
        this.packageName = packageName;
        this.totalBytes = totalBytes;
    }

    public String getPackageName() {
        return packageName;
    }

    public long getTotalBytes() {
        return totalBytes;
    }
}

public class Data extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    private static final String TAG = "DataUsage";
    private static final String EXCLUDE_APPS_PREFS = "exclude_apps_prefs";

    public static SharedPreferences getExcludeAppsPrefs(Context context) {
        return context.getSharedPreferences(EXCLUDE_APPS_PREFS, Context.MODE_PRIVATE);
    }

    public static String getSubscriberId(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String subscriberId = "";
            try {
                subscriberId =  telephonyManager.getSubscriberId();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return subscriberId;
        } else {
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_data);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent data = getIntent();
        String saluto = data.getStringExtra("saluto");
        //TextView dataTotal = (TextView) findViewById(R.id.dataTotal);
        //msg.setText(saluto);

        // Check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);
            return;
        } else {
            fetchNetworkStats();
            fetchAndSortDataUsageWIFI();
            fetchAndSortDataUsageMOBILE();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchNetworkStats();
                fetchAndSortDataUsageWIFI();
                fetchAndSortDataUsageMOBILE();
            } else {
                // Permission denied
                Log.w(TAG, "Permission denied by user.");
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
        TextView dataTotal = (TextView) findViewById(R.id.dataTotal);
        dataTotal.setText(dataUsage);
    }


    private void fetchAndSortDataUsageWIFI() {
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) this.getSystemService(Context.NETWORK_STATS_SERVICE);
        if (networkStatsManager == null) {
            Log.e(TAG, "NetworkStatsManager is null.");
            return;
        }

        List<AppDataUsage> appDataUsages = new ArrayList<>();

        long startTime = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000); // 30 days ago
        long endTime = System.currentTimeMillis();

        String appUsage = "Consumi WIFI\n";
        long mebibyte = 1048576; // 1 MB = 1048576 bytes


        /** Retrieve the excluded apps list
        SharedPreferences sharedPreferences = getExcludeAppsPrefs(this);
        String jsonData = sharedPreferences.getString(EXCLUDE_APPS_PREFS, null);
        List<AppModel> excludedAppsList = new ArrayList<>();
        if (jsonData != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<AppModel>>() {}.getType();
            excludedAppsList.addAll(gson.fromJson(jsonData, type));
        }
        Log.i(TAG, "JSON HERE: " + jsonData);
        **/

        PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> packagesall = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        //excluded
        for (ApplicationInfo appInfos : packagesall) {
            int uid = appInfos.uid;

            try {
                NetworkStats stats = networkStatsManager
                        .queryDetailsForUid(ConnectivityManager.TYPE_WIFI,
                                getSubscriberId(this),
                                startTime,
                                endTime, uid);
                NetworkStats.Bucket bucket = new NetworkStats.Bucket();
                while (stats.hasNextBucket()) {
                    stats.getNextBucket(bucket);

                    long rxBytes = bucket.getRxBytes();
                    long txBytes = bucket.getTxBytes();
                    long totalBytes = rxBytes + txBytes;

                    if (totalBytes >= 5120) { //5MB
                        PackageManager pm = getPackageManager();
                        String[] packages = pm.getPackagesForUid(uid);
                        if (packages != null) {
                            for (String packageName : packages) {
                                try {
                                    ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
                                    String appName = pm.getApplicationLabel(appInfo).toString();
                                    AppDataUsage appDataUsage = new AppDataUsage(appName, totalBytes);
                                    appDataUsages.add(appDataUsage);
                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }



            } catch (Exception e) {
                Log.e(TAG, "Error querying data usage", e);
            }
        }




        //system
        try {
            NetworkStats stats = networkStatsManager.querySummary(NetworkCapabilities.TRANSPORT_WIFI, null, startTime, endTime);
            NetworkStats.Bucket bucket = new NetworkStats.Bucket();

            while (stats.hasNextBucket()) {
                stats.getNextBucket(bucket);

                int uid = bucket.getUid();
                long rxBytes = bucket.getRxBytes();
                long txBytes = bucket.getTxBytes();
                long totalBytes = rxBytes + txBytes;

                if (totalBytes >= 5*mebibyte) { // 5MB
                    PackageManager pm = getPackageManager();
                    String[] packages = pm.getPackagesForUid(uid);
                    if (packages != null) {
                        for (String packageName : packages) {
                            boolean isExcluded = false;

                            if (!isExcluded) {
                                try {
                                    ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
                                    String appName = pm.getApplicationLabel(appInfo).toString();
                                    AppDataUsage appDataUsage = new AppDataUsage(appName, totalBytes);
                                    appDataUsages.add(appDataUsage);
                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }

            Collections.sort(appDataUsages, Comparator.comparingLong(AppDataUsage::getTotalBytes).reversed());

            for (AppDataUsage app : appDataUsages) {
                appUsage += "App: " + app.getPackageName() + " - Data: " + app.getTotalBytes() / mebibyte + " MB\n";
                Log.i(TAG, "App: " + app.getPackageName() + " - Data: " + app.getTotalBytes() + " bytes");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error querying data usage", e);
        }

        TextView appsDetail = findViewById(R.id.appsWifi);
        appsDetail.setText(appUsage);
    }


    /**
    private void fetchAndSortDataUsageWIFI() {
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) this.getSystemService(Context.NETWORK_STATS_SERVICE);
        if (networkStatsManager == null) {
            Log.e(TAG, "NetworkStatsManager is null.");
            return;
        }

        List<AppDataUsage> appDataUsages = new ArrayList<>();

        long startTime = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000); // 30 days ago
        long endTime = System.currentTimeMillis();

        String appUsage="Consumi WIFI\n";
        long mebibyte=1048576;

        try {
            NetworkStats stats = networkStatsManager.querySummary(NetworkCapabilities.TRANSPORT_WIFI, null, startTime, endTime);
            NetworkStats.Bucket bucket = new NetworkStats.Bucket();

            while (stats.hasNextBucket()) {
                stats.getNextBucket(bucket);

                int uid = bucket.getUid();
                long rxBytes = bucket.getRxBytes();
                long txBytes = bucket.getTxBytes();
                long totalBytes = rxBytes + txBytes;

                if (totalBytes >= 5120) { //5MB
                    PackageManager pm = getPackageManager();
                    String[] packages = pm.getPackagesForUid(uid);
                    if (packages != null) {
                        for (String packageName : packages) {
                            try {
                                ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
                                String appName = pm.getApplicationLabel(appInfo).toString();
                                AppDataUsage appDataUsage = new AppDataUsage(appName, totalBytes);
                                appDataUsages.add(appDataUsage);
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            Collections.sort(appDataUsages, Comparator.comparingLong(AppDataUsage::getTotalBytes).reversed());

            for (AppDataUsage app : appDataUsages) {
                appUsage+="App: " + app.getPackageName() + " - Data: " + app.getTotalBytes()/mebibyte + " MB\n";
                Log.i(TAG, "App: " + app.getPackageName() + " - Data: " + app.getTotalBytes() + " bytes");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error querying data usage", e);
        }
        TextView appsDetail = (TextView) findViewById(R.id.appsWifi);
        appsDetail.setText(appUsage);
    }
**/
    private void fetchAndSortDataUsageMOBILE() {
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) this.getSystemService(Context.NETWORK_STATS_SERVICE);
        if (networkStatsManager == null) {
            Log.e(TAG, "NetworkStatsManager is null.");
            return;
        }

        List<AppDataUsage> appDataUsages = new ArrayList<>();

        long startTime = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000); // 30 days ago
        long endTime = System.currentTimeMillis();

        String appUsage="Consumi Rete Mobile\n";
        long mebibyte=1048576;

        try {
            NetworkStats stats = networkStatsManager.querySummary(NetworkCapabilities.TRANSPORT_CELLULAR,
                    null,
                    startTime,
                    endTime);
            NetworkStats.Bucket bucket = new NetworkStats.Bucket();

            while (stats.hasNextBucket()) {
                stats.getNextBucket(bucket);

                int uid = bucket.getUid();
                long rxBytes = bucket.getRxBytes();
                long txBytes = bucket.getTxBytes();
                long totalBytes = rxBytes + txBytes;

                if (totalBytes >= 5*mebibyte) { //5MB
                    PackageManager pm = getPackageManager();
                    String[] packages = pm.getPackagesForUid(uid);
                    if (packages != null) {
                        for (String packageName : packages) {
                            try {
                                ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
                                String appName = pm.getApplicationLabel(appInfo).toString();
                                AppDataUsage appDataUsage = new AppDataUsage(appName, totalBytes);
                                appDataUsages.add(appDataUsage);
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            Collections.sort(appDataUsages, Comparator.comparingLong(AppDataUsage::getTotalBytes).reversed());

            for (AppDataUsage app : appDataUsages) {
                appUsage+="App: " + app.getPackageName() + " - Data: " + app.getTotalBytes()/mebibyte + " MB\n";
                Log.i(TAG, "App: " + app.getPackageName() + " - Data: " + app.getTotalBytes() + " bytes");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error querying data usage", e);
        }
        TextView appsDetail = (TextView) findViewById(R.id.appsMobile);
        appsDetail.setText(appUsage);
    }



}
