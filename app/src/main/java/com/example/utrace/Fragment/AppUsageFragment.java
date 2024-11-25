package com.example.utrace.Fragment;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.utrace.Adapter.AppsListAdapter;
import com.example.utrace.Model.AppModel;
import com.example.utrace.R;
import com.example.utrace.utils.FormatHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppUsageFragment extends Fragment {

    private static final String TAG = "AppUsageFragment";
    private RecyclerView recyclerView;
    private View view;
    private AppsListAdapter adapter;
    private List<AppModel> appsList = new ArrayList<>();

    private long filterMinMB = 5 * 1024 * 1024; // 5MB

    private LinearLayout filterMenu;
    private FloatingActionButton fabFilter;
    private RadioGroup timeFilterGroup, dataTypeGroup;
    private Button btnApply;

    private int selectedUsage;
    private long selectedTime;

    private static final int WIFI = NetworkCapabilities.TRANSPORT_WIFI;
    private static final int CELLULAR = NetworkCapabilities.TRANSPORT_CELLULAR;
    private static final long DAY = (24 * 60 * 60 * 1000);
    private static final long WEEK = (7L * 24 * 60 * 60 * 1000);
    private static final long MONTH = (30L * 24 * 60 * 60 * 1000);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_app_usage, container, false);

        setToolbarTitle("App usage");

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        filterMenu = view.findViewById(R.id.filter_menu);
        fabFilter = view.findViewById(R.id.fab_filter);
        timeFilterGroup = view.findViewById(R.id.time_filter_group);
        dataTypeGroup = view.findViewById(R.id.data_type_group);
        btnApply = view.findViewById(R.id.btn_apply);
        fabFilter.setOnClickListener(v -> {
            if (filterMenu.getVisibility() == View.GONE) {
                filterMenu.setVisibility(View.VISIBLE);
            } else {
                filterMenu.setVisibility(View.GONE);
            }
        });
        btnApply.setOnClickListener(v -> applyFilters());

        selectedTime = DAY;
        selectedUsage = WIFI;
        load();

        return view;
    }

    private void load() {
        appsList.clear();
        fetchInstalledApps();
        fetchDataUsage(selectedUsage);
        fetchNetworkStats();
        appsList.removeIf(current -> current.getTotalBytes() < filterMinMB);
        Collections.sort(appsList, Comparator.comparingLong(AppModel::getTotalBytes).reversed());

        adapter = new AppsListAdapter(getActivity(), appsList);
        recyclerView.setAdapter(adapter);
    }

    private void fetchInstalledApps() {
        PackageManager packageManager = getActivity().getPackageManager();
        List<ApplicationInfo> packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo appInfo : packages) {
            AppModel app = new AppModel();
            app.setAppName((String) packageManager.getApplicationLabel(appInfo));
            app.setPackageName(appInfo.packageName);
            app.setUid(appInfo.uid);
            appsList.add(app);
        }
    }

    private void fetchDataUsage(int mode) {
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) getActivity().getSystemService(getContext().NETWORK_STATS_SERVICE);
        long startTime = System.currentTimeMillis() - selectedTime;
        long endTime = System.currentTimeMillis();

        for (AppModel app : appsList) {
            try {
                NetworkStats stats = networkStatsManager.queryDetailsForUid(
                        mode,
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
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) getActivity().getSystemService(getContext().NETWORK_STATS_SERVICE);
        if (networkStatsManager == null) {
            Log.e(TAG, "NetworkStatsManager is null.");
            return;
        }

        long startTime = System.currentTimeMillis() - selectedTime;
        long endTime = System.currentTimeMillis();

        String dataUsage = "Consumi totali\n";

        try {
            NetworkStats.Bucket bucket = networkStatsManager.querySummaryForDevice(CELLULAR, null, startTime, endTime);
            long mobileRxBytes = bucket.getRxBytes();
            long mobileTxBytes = bucket.getTxBytes();
            dataUsage += "Mobile Down: " + FormatHelper.bytesToString(mobileRxBytes) + " Up: " + FormatHelper.bytesToString(mobileTxBytes) + "\n";
            Log.i(TAG, "Mobile Rx: " + mobileRxBytes + " bytes, Tx: " + mobileTxBytes + " bytes");
        } catch (Exception e) {
            Log.e(TAG, "Error querying mobile data usage", e);
        }

        try {
            NetworkStats.Bucket bucket = networkStatsManager.querySummaryForDevice(WIFI, null, startTime, endTime);
            long wifiRxBytes = bucket.getRxBytes();
            long wifiTxBytes = bucket.getTxBytes();
            dataUsage += "Wi-Fi Down: " + FormatHelper.bytesToString(wifiRxBytes) + " Up: " + FormatHelper.bytesToString(wifiTxBytes);
            Log.i(TAG, "Wi-Fi Rx: " + wifiRxBytes + " bytes, Tx: " + wifiTxBytes + " bytes");
        } catch (Exception e) {
            Log.e(TAG, "Error querying Wi-Fi data usage", e);
        }
        TextView dataTotal = view.findViewById(R.id.totalUsage);
        dataTotal.setText(dataUsage);
    }

    private void applyFilters() {
        int selectedTimeId = timeFilterGroup.getCheckedRadioButtonId();
        int selectedDataTypeId = dataTypeGroup.getCheckedRadioButtonId();

        RadioButton selectedTimeButton = view.findViewById(selectedTimeId);
        RadioButton selectedDataTypeButton = view.findViewById(selectedDataTypeId);

        String timeFilter = selectedTimeButton.getText().toString();
        String dataTypeFilter = selectedDataTypeButton.getText().toString();

        if (timeFilter.equals("24h"))
            selectedTime = DAY;
        else if (timeFilter.equals("1 week"))
            selectedTime = WEEK;
        else
            selectedTime = MONTH;

        if (dataTypeFilter.equals("WiFi"))
            selectedUsage = WIFI;
        else
            selectedUsage = CELLULAR;

        load();

        filterMenu.setVisibility(View.GONE);
    }

    private void setToolbarTitle(String title) {
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
        }
    }
}
